package com.boc.binlog;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by guan on 2017/6/12.
 */
class Binlog2Sql {
    //本地数据库名
    private static String clientDB = "canal";
    //服务端数据库名
    private static String serverDB = "canal";
    //数据库驱动
    private static String driver = "com.mysql.jdbc.Driver";
    //数据库url
    private static String dburl = "jdbc:mysql://localhost:3306/";
    //数据库用户名
    private static String username = "root";
    //数据库密码
    private static String password;
    //需要更新的表
    private static Set<String> tables = new HashSet<>();

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver);
            String url = dburl+clientDB + "?serverTimezone=UTC";
            String user = username;
            String pass = password;
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static boolean sqlToSql(InputStream is, String fileName) throws IOException, SQLException {
        //所有SQL语句，最后写成文件
        String allSql = null;
        //每行数据
        String str;
        //表名和列名
        Map<String, String> tableColum = new HashMap<String, String>();
        //SQL语句，包括update、delete、insert
        String sqlUDI = null;
        //update、delete、insert等语句的开始和结束
        String flag = null;
        //查询条件主键
        String whereID = null;
        //表名
        String tableName = null;
        //列名
        List<String> columns = new LinkedList<>();
        //列名和值
        String columValue = null;
        //列的序号
        int columNu = 1;
        //SET标签
        String set = null;

        //File file = new File(sqlPath);
        //FileInputStream inputStream = new FileInputStream(rowPath+fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        Connection connection=null;
        while ((str = bufferedReader.readLine())!=null){
            //System.out.println(str);
            //解析row模式SQL
            str = str.replaceAll("="," = ");
            str = str.replaceAll("'"," ' ");
            str = str.replaceAll("`"," ` ");
            str = str.replaceAll("\\\\","\\\\\\\\");
            String[] elem = str.split(" ");
            if (connection==null){
                connection = getConnection();
            }
            //判断是否为增删改语句
            boolean isUDI = (!str.contains("SET") && !str.contains("@") && !str.contains("WHERE"));
            //获取表名和列名
            if ( isUDI && elem.length > 2 &&(tableColum.get(elem[elem.length-2])==null)){
                //如果表名在待更新表里存在，则表明该表应该上传
                if (tables.contains(elem[elem.length-2])){
                    String sql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '"+elem[elem.length-2]+"' AND TABLE_SCHEMA = '" + clientDB + "'";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    ResultSet rs=stmt.executeQuery(sql);
                    ResultSetMetaData data=rs.getMetaData();
                    String columName = null;
                    while(rs.next()) {
                        for (int i = 1; i < data.getColumnCount()+1; i++) {
                            if (columName==null){
                                columName = rs.getString(i);
                            }else{
                                columName+=","+rs.getString(i);
                            }
                        }
                    }
                    tableColum.put(elem[elem.length-2],columName);
                }else {
                    continue;
                }
            }
            //update时
            if(isUDI && (elem.length > 1 && elem[1].equals("UPDATE")) || (flag!=null && flag.equals("UPDATE"))){
                //确定update的头语句
                if(flag==null){
                    flag = "UPDATE";
                    tableName = elem[elem.length-2];
                    sqlUDI = "UPDATE "+serverDB+"."+tableName+"";
                    String[] temp = tableColum.get(tableName).split(",");
                    for (String clName : temp){
                        columns.add(clName);
                    }
                }
                if (elem[1].equals("SET")){
                    set = "SET";
                    whereID = " WHERE ";
                }else if (elem.length>4 && (elem[3].equals("@1")) && set!=null && set.equals("SET")){
                    //判断主键是否为varchar类型
                    if (elem[5].length()==0){

                        whereID += columns.get(0)+"="+ "'"+elem[7]+"'";
                    }else {
                        whereID += columns.get(0)+"="+elem[5];
                    }
                }else if (elem.length>3 && !(elem[3].equals("@1")) && set!=null && set.equals("SET")){
                    if (columValue==null){
                        if (elem.length>9){
                            //含有空格、等号或单引号的字符串类型
                            String temp = null;
                            for (int e=7; e< elem.length-1; e++ ){
                                if (temp==null){
                                    temp=elem[e];
                                }else {
                                    temp+=" "+elem[e];
                                }
                            }
                            if (temp.contains("'") || temp.contains("=")||temp.contains("`")){
                                temp = temp.replace(" ' ","\\'");
                                temp = temp.replace(" = ","=");
                                temp = temp.replace(" ` ","`");
                            }
                            columValue = columns.get(columNu)+"="+"'"+temp+"'";
                        }else if(elem.length==9){
                            //不含有空格、等号或单引号的字符串类型
                            columValue = columns.get(columNu)+"="+"'"+elem[7]+"'";
                        }else {
                            if (elem.length>6){
                                //datetime型
                                columValue = columns.get(columNu)+"="+"'"+elem[5]+" "+elem[6]+"'";
                            }else {
                                //数字型
                                columValue = columns.get(columNu)+"="+elem[5];
                            }
                        }
                        columNu++;
                    }else{
                        if (elem.length>9){
                            //含有空格、等号或单引号的字符串类型
                            String temp = null;
                            for (int e=7; e< elem.length-1; e++ ){
                                if (temp==null){

                                    temp=elem[e];
                                }else {

                                    temp+=" "+elem[e];
                                }
                            }
                            if (temp.contains("'") || temp.contains("=")||temp.contains("`")){
                                temp = temp.replace(" ' ","\\'");
                                temp = temp.replace(" = ","=");
                                temp = temp.replace(" ` ","`");
                            }

                            columValue += ","+columns.get(columNu)+"="+"'"+temp+"'";
                        }else if (elem.length==9){
                            //不含有空格、等号或单引号的字符串类型
                            columValue += ","+columns.get(columNu)+"="+"'"+elem[7]+"'";
                        }else {
                            if (elem.length>6){
                                //datetime型
                                columValue += ","+columns.get(columNu)+"="+"'"+elem[5]+" "+elem[6]+"'";
                            }else {
                                //数字型
                                columValue += ","+columns.get(columNu)+"="+elem[5];
                            }
                        }
                        columNu++;
                    }
                }
                if (set !=null){
                    if (set.equals("SET") && columNu == columns.size()){
                        sqlUDI += " SET "+columValue+" "+whereID;
                        //将update语句添加到allSql中
                        if (allSql==null){
                            allSql = sqlUDI+";\r\n";
                        }else{
                            allSql += sqlUDI+";\r\n";
                        }
                        //清空所有临时变量
                        sqlUDI = null;
                        flag = null;
                        whereID = null;
                        tableName = null;
                        columns = new LinkedList<>();
                        columValue = null;
                        columNu = 1;
                        set = null;
                        //System.out.println(allSql);
                        continue;
                    }
                }
            }
            //insert时
            if (isUDI &&(elem.length > 1 && elem[1].equals("INSERT")) || (flag!=null && flag.equals("INSERT"))){
                //确定insert的头语句
                if(flag==null){
                    flag = "INSERT";
                    tableName = elem[elem.length-2];
                    sqlUDI = "INSERT INTO "+serverDB+"."+tableName+""+" VALUES(";
                    String[] temp = tableColum.get(tableName).split(",");
                    for (String clName : temp){
                        columns.add(clName);
                    }
                }
                if (elem[1].equals("SET")){
                    set = "SET";
                }else if (set!=null && set.equals("SET")){
                    if (columValue==null){
                        //判断主键是否为varchar类型
                        if (elem[5].length()==0){

                            columValue = "'"+elem[7]+"'";
                        }else{
                            columValue = elem[5];
                        }
                        columNu++;
                    }else{
                        //判断是否为含有空格的数据
                        if (elem.length>9){
                            String temp = null;
                            for (int e=7; e< elem.length-1; e++ ){
                                if (temp==null ){

                                    temp=elem[e];
                                }else {

                                    temp+=" "+elem[e];
                                }
                            }
                            if (temp.contains("'") || temp.contains("=")||temp.contains("`")){
                                temp = temp.replace(" ' ","\\'");
                                temp = temp.replace(" = ","=");
                                temp = temp.replace(" ` ","`");
                            }
                            columValue += ","+"'"+temp+"'";

                        }else if(elem.length==9){
                            //不含有空格、等号或单引号的字符串类型
                            columValue += ","+"'"+elem[7]+"'";
                        }else {
                            if (elem.length>6){
                                //时间型
                                columValue += ","+"'"+elem[5]+" "+elem[6]+"'";
                            }else {
                                //数字型
                                columValue += ","+elem[5];
                            }
                        }
                        columNu++;
                    }
                }
                if (set !=null){
                    if (set.equals("SET") && columNu == columns.size()+1){
                        sqlUDI += columValue+")";
                        //将insert语句添加到allSql中
                        if (allSql==null){
                            allSql = sqlUDI+";\r\n";
                        }else{
                            allSql += sqlUDI+";\r\n";
                        }
                        //清空所有临时变量
                        sqlUDI = null;
                        flag = null;
                        tableName = null;
                        columns = new LinkedList<>();
                        columValue = null;
                        columNu = 1;
                        set = null;
                        //System.out.println(allSql);
                        continue;
                    }
                }
            }
            //delete时
            if (isUDI &&(elem.length > 1 && elem[1].equals("DELETE")) || (flag!=null && flag.equals("DELETE"))){
                //确定delete的头语句
                if(flag==null){
                    flag = "DELETE";
                    tableName = elem[elem.length-2];
                    sqlUDI = "DELETE FROM "+serverDB+"."+tableName+" WHERE ";
                    String[] temp = tableColum.get(tableName).split(",");
                    for (String clName : temp){
                        columns.add(clName);
                    }
                }
                if (elem[1].equals("WHERE")){
                    set = "WHERE";
                }else if (set!=null && set.equals("WHERE")){
                    if (columValue==null){
                        //判断主键是否为varchar类型
                        if (elem[5].length()==0){

                            columValue = columns.get(columNu-1)+"="+ "'"+elem[7]+"'";
                        }else{
                            columValue = columns.get(columNu-1)+"="+elem[5];
                        }
                        columNu++;
                    }
                }
                if (set !=null){
                    if (set.equals("WHERE") && columNu==2){
                        sqlUDI += columValue;
                        //将insert语句添加到allSql中
                        if (allSql==null){
                            allSql = sqlUDI+";\r\n";
                        }else{
                            allSql += sqlUDI+";\r\n";
                        }
                        //清空情况所有临时变量
                        sqlUDI = null;
                        flag = null;
                        tableName = null;
                        columns = new LinkedList<>();
                        columValue = null;
                        columNu = 1;
                        set = null;
                        //System.out.println(allSql);
                        continue;
                    }
                }
            }
        }
        bufferedReader.close();
//        logger.info(allSql);
        if (allSql!=null){
            //将生成的字符串转换成SQL文件存储到本地
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
                writer.write(allSql);
                writer.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                writer.close();
            }
        }
        return false;
    }

    /**
     * 开始解析的方法，封装的解析入口
     * 注意，如果在binlog里执行过DDL语句，也就是修改表结构，这里是没有做处理的，这会造成数据前后字段对不上的问题，如果碰到这种情况要么自己补上DDL语句，要么换别的
     * @param binlogFile 源binlog文件
     * @param toFile 解析后sql要输出的文件，一般为xxx.sql
     * @param parseTables 要解析的表集合，只有在这个集合里的表才会解析
     * @param sourceDB binlog里源数据库的库名
     * @param toDB 解析完成后执行sql的数据库库名
     * @param jdbcUrl jdbc连接，例如：jdbc:mysql://localhost:3306/，这个连接主要是为了从数据库里获取字段映射，因为binlog里的字段都是用下标表示的，比如@1、@2
     * @param dbUserName 数据库链接账号
     * @param dbPassword 数据库链接密码
     */
    public static void parse(String binlogFile, String toFile, Collection<String> parseTables, String sourceDB, String toDB, String jdbcUrl, String dbUserName, String dbPassword) throws Exception {
        //参数初始化
        clientDB = sourceDB;
        serverDB = toDB;
        dburl = jdbcUrl + "/";
        username = dbUserName;
        password = dbPassword;
        tables = new HashSet<>(parseTables);
        // 开始解析
        sqlToSql(new FileInputStream(binlogFile), toFile);
    }

    public static void  main(String[] arg) throws Exception {
        tables.add("user");
        sqlToSql(new FileInputStream("C:\\Users\\boc\\Desktop\\1.binlog"), "C:\\Users\\boc\\Desktop\\to.sql");
    }
}