package com.reabam.common.utils.reabam.util;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * <p> @Title: DateUtil </p>
 * <p> @Description: 日期处理工具类 </p>
 * <p> @Company: www.reabam.com </p>
 *
 * @author 睿本.刘瑞城
 * @date 2015年7月30日 下午1:10:37	
 * @version 1.0
 */
public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 将字符串转化为DATE 
     *
     * @param date
     *            格式要求：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd或 yyyy-M-dd或 yyyy-M-d或 
     *            yyyy-MM-d或 yyyy-M-dd 
     * @param def
     *            如果格式化失败返回null 
     * @return
     */
    public static Date fmtStrToDate(String date) {
        if (date == null || "".equals(date.trim()))
            return null;
        try {
            if (date.length() == 9 || date.length() == 8) {
                String[] dateStr = date.split("-");
                date = dateStr[0] + (dateStr[1].length() == 1 ? "-0" : "-")
                        + dateStr[1] + (dateStr[2].length() == 1 ? "-0" : "-")
                        + dateStr[2];
            }
            if (date.length() != 10 & date.length() != 19)
                return null;
            if (date.length() == 10)
                date = date + " 00:00:00";
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Date fmtStrToDateV2(String date) {
        if (date == null || "".equals(date.trim()))
            return null;
        try {
            String pattern;
            //20211104；2021-11-04；2021.11.04；2021年11月4日；2021/11/04
            if(date.contains("-")){
                pattern = "yyyy-MM-dd";
            }else if(date.contains(".")){
                pattern = "yyyy.MM.dd";
            }else if(date.contains("年")){
                pattern = "yyyy年MM月dd";
            }else if(date.contains("/")){
                pattern = "yyyy/MM/dd";
            }else{
                pattern = "yyyyMMdd";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    pattern);
            return dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static Date fmtStrToGDate(String date) {
        if (date == null || "".equals(date.trim()))
            return null;
        try {
            if (date.length() == 9 || date.length() == 8) {
                String[] dateStr = date.split("-");
                date = dateStr[0] + (dateStr[1].length() == 1 ? "-0" : "-")
                        + dateStr[1] + (dateStr[2].length() == 1 ? "-0" : "-")
                        + dateStr[2];
            }
            if (date.length() != 10 & date.length() != 19)
                return null;
            if (date.length() == 10)
                date = date + " 23:59:59";
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * Description:格式化日期,如果格式化失败返回def 
     *
     * @param formatDate
     * @param def
     * @return
     */
    public static Date fmtStrToDate(String date, Date def) {
        Date d = fmtStrToDate(date);
        if (d == null)
            return def;
        return d;
    }

    /**
     * 返回当日短日期型 
     *
     * @return
     */
    public static Date getToDay() {
        return toShortDate(new Date());
    }

    /**
     *
     * Description:格式化日期,String字符串转化为Date 
     *
     * @param date
     * @param dtFormat
     *            例如:yyyy-MM-dd HH:mm:ss yyyyMMdd 
     * @return
     */
    public static String fmtDateToStr(Date date, String dtFormat) {
        if (date == null)
            return "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Description:按指定格式 格式化日期
     *
     * @param date
     * @param dtFormat
     * @return
     */
    public static Date fmtStrToDate(String date, String dtFormat) {
        return fmtStrToDate(date, dtFormat, true);
    }

    /**
     * Description:按指定格式 格式化日期
     *
     * @param date
     * @param dtFormat
     * @param lenient 是否顺延计算日期
     * @return
     */
    public static Date fmtStrToDate(String date, String dtFormat, boolean lenient) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
            dateFormat.setLenient(lenient);
            return dateFormat.parse(date);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String fmtDateToYMDHMS(Date date) {
        return fmtDateToStr(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String fmtDateToShortYMD(Date date) {
        return fmtDateToStr(date, "yyyyMMdd");
    }

    public static String fmtDateToYMDHM(Date date) {
        return fmtDateToStr(date, "yyyy-MM-dd HH:mm");
    }

    public static String fmtDateToYMD(Date date) {
        return fmtDateToStr(date, "yyyy-MM-dd");
    }

    public static String fmtDateToYM(Date date) {
        return fmtDateToStr(date, "yyyy-MM");
    }

    public static String fmtDateToM(Date date) {
        return fmtDateToStr(date, "MM");
    }

    public static String fmtDateToHHmmss(Date date) {
        return fmtDateToStr(date, "HH:mm:ss");
    }

    public static String fmtDateToHH(Date date) {
        return fmtDateToStr(date, "HH");
    }

    /**
     *
     * Description:只保留日期中的年月日 
     *
     * @param date
     * @return
     */
    public static Date toShortDate(Date date) {
        String strD = fmtDateToStr(date, "yyyy-MM-dd");
        return fmtStrToDate(strD);
    }

    /**
     *
     * Description:只保留日期中的年月日 
     *
     * @param date格式要求yyyy-MM-dd
     * @return
     */
    public static Date toShortDate(String date) {
        if (date != null && date.length() >= 10) {
            return fmtStrToDate(date.substring(0, 10));
        } else
            return fmtStrToDate(date);
    }

    /**
     * 求对日 
     *
     * @param countMonth
     *            :月份的个数(几个月) 
     * @param flag
     *            :true 求前countMonth个月的对日:false 求下countMonth个月的对日 
     * @return
     */
    public static Date getCounterglow(int countMonth, boolean before) {
        Calendar ca = Calendar.getInstance();
        return getCounterglow(ca.getTime(), before ? -countMonth : countMonth);
    }

    /**
     *
     * Description: 求对日 加月用+ 减月用- 
     *
     * @param date
     * @param countMonth
     * @return
     */
    public static Date getCounterglow(Date date, int num) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, num);
        return ca.getTime();
    }

    /**
     *
     * Description:加一天 
     *
     * @param date
     * @return
     */
    public static Date addDay(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(Calendar.DAY_OF_YEAR, 1);
        return cd.getTime();
    }

    /**
     *
     * Description:判断一个日期是否为工作日(非周六周日) 
     *
     * @param date
     * @return
     */
    public static boolean isWorkDay(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SUNDAY || dayOfWeek != Calendar.SATURDAY)
            return false;
        return true;
    }

    /**
     *
     * Description:取一个月的最后一天 
     *
     * @param date1
     * @return
     */
    public static Date getLastDayOfMonth(Date date1) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, 1);
        date.add(Calendar.DAY_OF_YEAR, -1);
        return toShortDate(date.getTime());
    }

    /**
     * 求开始截至日期之间的天数差（取绝对值并且按相对方式计算相差天数（即按天数部分来计算））. 
     *
     * @param d1
     *            开始日期 
     * @param d2
     *            截至日期 
     * @return 返回相差天数 (取绝对值)
     */
    public static int getDaysInterval(Date d1, Date d2) {
        return getDaysInterval(d1, d2, true);
    }

    /**
     * 求开始截至日期之间的天数差（按相对方式计算相差天数（即按天数部分来计算））. 
     *
     * @param d1
     *            开始日期 
     * @param d2
     *            截至日期 
     * @param isAbs
     *            true:返回参数取绝对值;false则保留正负值
     * @return 返回相差天数
     */
    public static int getDaysInterval(Date d1, Date d2, boolean isAbs) {
        return getDaysInterval(d1, d2, isAbs, false);
    }

    /**
     * 求开始截至日期之间的天数差. 
     *
     * @param d1
     *            开始日期 
     * @param d2
     *            截至日期 
     * @param isAbs
     *            true:返回参数取绝对值;false则保留正负值
     * @param isReal
     *            true:返回实际的相差天数;false则返回相对的相差天数（即按天数部分来计算）        
     * @return 返回相差天数
     */
    public static int getDaysInterval(Date d1, Date d2, boolean isAbs, boolean isReal) {
        if (d1 == null || d2 == null)
            return 0;
        long m = d1.getTime(), n = d2.getTime();
        if(!isReal){
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            m = cal.getTimeInMillis();

            cal.setTime(d2);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            n = cal.getTimeInMillis();
        }
        if(isAbs)
            return (int) Math.abs((m - n) / (1000 * 3600 * 24));
        return (int) ((m - n) / (1000 * 3600 * 24));
    }

    /**
     * 求开始截至日期之间的秒数差. 
     *
     * @param d1
     *            开始日期 
     * @param d2
     *            截至日期 
     * @return 返回相差秒数
     */
    public static int getSecondsInterval(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return 0;
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        int ret = (int) Math.abs((m - n) / 1000 );
        return ret;
    }

    /**
     * 求系统当前日期之间的秒数差. 
     *
     * @param date 日期  
     * @return 返回相差秒数
     */
    public static int getSecondsInterval(Date date) {
        return getSecondsInterval(date,new Date());
    }


    /**
     * 求开始截至日期之间的分数差. 
     *
     * @param d1
     *            开始日期 
     * @param d2
     *            截至日期 
     * @return 返回相差分数
     */
    public static int getMinuteInterval(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return 0;
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        int ret = (int) Math.abs((m - n) / 1000 )/60;
        return ret;
    }

    /**
     * 求系统当前日期之间的分数差. 
     *
     * @param date 日期  
     * @return 返回相差分数
     */
    public static int getMinuteInterval(Date date) {
        return getMinuteInterval(date,new Date());
    }


    /**
     * 获取指定日期的中文星期数
     *
     * @param date 日期
     * @return 中文星期数
     */
    public static String getDayOfWeek(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return "星期" + toChNumber(cl.get(Calendar.DAY_OF_WEEK) - 1, "日一二三四五六");
    }

    /**
     * 获取指定日期的中文星期数
     *
     * @param date 日期
     * @return 中文星期数
     */
    public static String getDayOfWeekV2(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return "周" + toChNumber(cl.get(Calendar.DAY_OF_WEEK) - 1, "日一二三四五六");
    }

    /**
     * 获取指定日期的数字星期数
     *
     * @param date 日期
     * @return 数字星期数
     */
    public static int getDayOfWeekNum(Date date) {
        return getDayOfWeekNum(date, 1);
    }

    /**
     * 获取指定日期的数字星期数
     *
     * @param date 日期
     * @param type 星期类型：[ 1 - 以星期一为一周开始(1234567); 2 - 以星期日为一周开始(0123456)]
     * @return 数字星期数
     */
    public static int getDayOfWeekNum(Date date, int type) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int weekNum = cl.get(Calendar.DAY_OF_WEEK) - 1;
        //处理每周开始日期为周日的问题
        if(type == 1 && weekNum == 0) weekNum = 7;
        return weekNum;
    }

    /**
     * 将数字转为中文。 "0123456789"->"零一二三四五六七八九" 
     *
     * @param num
     *            长度为1,'0'-'9'的字符串 
     * @return
     */
    public static String toChNumber(int num) {
        final String str = "零一二三四五六七八九";
        return str.substring(num, num + 1);
    }

    /**
     * 将数字转为对应的中文
     *
     * @param num 数字
     * @return
     */
    public static String toChNumber(int num, String str) {
        return str.substring(num, num + 1);
    }

    /**
     *
     * Description:指定日期加或减seconds秒
     *
     * @param date1日期
     * @param seconds秒数
     * @return
     */
    public static Date addSecond(Date date1, int seconds) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.SECOND, seconds);
        return date.getTime();
    }

    /**
     *
     * Description:指定日期加或减hour小時
     *
     * @param date1日期
     * @param hour時数
     * @return
     */
    public static Date addHour(Date date1, int hours) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.HOUR, hours);
        return date.getTime();
    }

    /**
     *
     * Description:指定日期加或减days天 
     *
     * @param date1日期
     * @param days天数
     * @return
     */
    public static Date addDay(Date date1, int days) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.DAY_OF_YEAR, days);
        return date.getTime();
    }

    /**
     * Description:指定日期加或减days天
     */
    public static Date addDay(Number days, Date date) {
        BigDecimal second = NumberUtil.roundDown(days, 2)
                .multiply(new BigDecimal("24"))
                .multiply(new BigDecimal("60")).multiply(new BigDecimal("60"));
        return DateUtil.addSecond(date, second.intValue());
    }

    /**
     *
     * Description:指定日期加或减months月 
     *
     * @param date1
     * @param months
     * @return
     */
    public static Date addMonth(Date date1, int months) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.MONTH, months);
        return date.getTime();
    }

    /**
     *
     * Description:指定日期加或减years年 
     *
     * @param date1
     * @param years
     * @return
     */
    public static Date addYear(Date date1, int years) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.YEAR, years);
        return date.getTime();
    }

    /**
     * 指定期间的开始日期 
     *
     * @param date
     *            指定日期 
     * @param type
     *            期间类型 
     * @param diff
     *            与指定日期的范围 
     * @return
     */
    public static Date getPeriodStart(Calendar date, int type, int diff) {
        date.add(type, diff * (-1));
        return date.getTime();
    }

    /**
     * 指定期间的开始日期 
     *
     * @param date
     *            指定日期 
     * @param type
     *            期间类型 
     * @param diff
     *            与指定日期的范围 
     * @return
     */
    public static Date getPeriodStart(Date date, int type, int diff) {
        return getPeriodStart(dateToCalendar(date), type, diff);
    }

    /**
     * 指定期间的结束日期 
     *
     * @param date
     *            指定日期 
     * @param type
     *            期间类型 
     * @param diff
     *            与指定日期的范围 
     * @return
     */
    public static Date getPeriodEnd(Calendar date, int type, int diff) {
        date.add(type, diff);
        return date.getTime();
    }

    /**
     * 指定期间的结束日期 
     *
     * @param date
     *            指定日期 
     * @param type
     *            期间类型 
     * @param diff
     *            与指定日期的范围 
     * @return
     */
    public static Date getPeriodEnd(Date date, int type, int diff) {
        return getPeriodEnd(dateToCalendar(date), type, diff);
    }

    /**
     * 指定日期所在星期的第一天 
     *
     * @param date
     * @return
     */
    public static Date getWeekStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_WEEK, 2);
        return cdate.getTime();
    }

    /**
     * 将java.util.Date类型转换成java.util.Calendar类型 
     *
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cdate = Calendar.getInstance();
        cdate.setTime(date);
        return cdate;
    }

    /**
     * 指定日期所在月的第一天 
     *
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_MONTH, 1);
        return toShortDate(cdate.getTime());
    }

    /**
     * 指定日期所在上月的第一天 
     *
     * @param date
     * @return
     */
    public static Date getLastMonthStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_MONTH, 1);
        cdate.add(Calendar.MONTH, -1);
        return toShortDate(cdate.getTime());
    }

    /**
     * 指定日期所在旬的第一天 
     *
     * @param date
     * @return
     */
    public static Date getTenDaysStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        int day = cdate.get(Calendar.DAY_OF_MONTH) / 10 * 10 + 1;
        if (cdate.get(Calendar.DAY_OF_MONTH) % 10 == 0 || day == 31)
            day = day - 10;
        cdate.set(Calendar.DAY_OF_MONTH, day);
        return cdate.getTime();
    }

    /**
     * 指定日期所在旬的最后一天 
     *
     * @param date
     * @return
     */
    public static Date getTenDaysEnd(Date date) {
        Calendar cdate = dateToCalendar(date);
        if (cdate.get(Calendar.DAY_OF_MONTH) / 10 == 2
                && cdate.get(Calendar.DAY_OF_MONTH) != 20)
            return getLastDayOfMonth(date);
        else
            return addDay(getTenDaysStart(addDay(date, 10)), -1);
    }

    /**
     * 指定日期所在年的第一天 
     *
     * @param date
     * @return
     */
    public static Date getYearStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        cdate.set(Calendar.DAY_OF_YEAR, 1);
        return cdate.getTime();
    }

    /**
     * 指定日期所在季度的第一天 
     *
     * @param date
     * @return
     */
    public static Date getQuarterStart(Date date) {
        Calendar cdate = dateToCalendar(date);
        int month = (cdate.get(Calendar.MONTH) / 3) * 3;
        cdate.set(Calendar.MONTH, month);
        return getMonthStart(cdate.getTime());
    }

    /**
     * 指定日期返回带中文的字符串（目前为年月日类型，之后补充） 
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStringByChinese(String format, Date date) {
        String dateString = fmtDateToStr(date, format);
        String[] dateStringArray = dateString.split("-");
        if ("yyyy-MM-dd".equals(format)) {
            dateString = dateStringArray[0] + "年" + dateStringArray[1] + "月"
                    + dateStringArray[2] + "日";
        } else if ("yyyy-MM".equals(format)) {
            dateString = dateStringArray[0] + "年" + dateStringArray[1] + "月";
        }
        return dateString;
    }

    public static Date getLastDayOfYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        years += "-12-31";
        Date returnDate = fmtStrToDate(years);
        return toShortDate(returnDate);
    }

    /**
     * 计算两个日期之间相差的月数 
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getMonths(Date date1, Date date2) {
        int iMonth = 0;
        int flag = 0;
        try {
            Calendar objCalendarDate1 = Calendar.getInstance();
            objCalendarDate1.setTime(date1);

            Calendar objCalendarDate2 = Calendar.getInstance();
            objCalendarDate2.setTime(date2);

            if (objCalendarDate2.equals(objCalendarDate1))
                return 0;
            if (objCalendarDate1.after(objCalendarDate2)) {
                Calendar temp = objCalendarDate1;
                objCalendarDate1 = objCalendarDate2;
                objCalendarDate2 = temp;
            }
            if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1
                    .get(Calendar.DAY_OF_MONTH))
                flag = 1;

            if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1
                    .get(Calendar.YEAR))
                iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1
                        .get(Calendar.YEAR))
                        * 12 + objCalendarDate2.get(Calendar.MONTH) - flag)
                        - objCalendarDate1.get(Calendar.MONTH);
            else
                iMonth = objCalendarDate2.get(Calendar.MONTH)
                        - objCalendarDate1.get(Calendar.MONTH) - flag;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return iMonth;
    }


    /**
     * 计算两个日期之间相差的小时数
     *
     * @param sdate
     * @param edate
     * @return
     */
    public static double getHours(String sdate, String edate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = sdf.parse(sdate);
        Date end = sdf.parse(edate);
        long cha = end.getTime() - start.getTime();
        return cha * 1.0 / (1000 * 60 * 60);
    }

    /**
     * 计算日期与当前日期相差的小时数
     *
     * @param date
     * @return
     */
    public static double getHours(String date) throws Exception {
        return getHours(date, fmtDateToStr(new Date(), "yyyy-MM-dd HH:mm"));
    }

    /**
     * 求开始截至日期之间的小时数差.
     *
     * @param d1
     *            开始日期
     * @param d2
     *            截至日期
     * @param isAbs
     *            true:返回参数取绝对值;false则保留正负值
     * @param isReal
     *            true:返回实际的相差小时数;false则返回相对的相差小时数（即按小时数部分来计算）
     * @return 返回相差小时数
     */
    public static int getHours(String sdate, String edate, boolean isAbs, boolean isReal) {
        try {
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = sdf.parse(sdate);
            Date d2 = sdf.parse(edate);
            return getHours(d1, d2, isAbs, isReal);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * 求开始截至日期之间的小时数差.
     *
     * @param d1
     *            开始日期
     * @param d2
     *            截至日期
     * @param isAbs
     *            true:返回参数取绝对值;false则保留正负值
     * @param isReal
     *            true:返回实际的相差小时数;false则返回相对的相差小时数（即按小时数部分来计算）
     * @return 返回相差小时数
     */
    public static int getHours(Date d1, Date d2, boolean isAbs, boolean isReal) {
        if (d1 == null || d2 == null)
            return 0;
        long m = d1.getTime(), n = d2.getTime();
        if (!isReal) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            m = cal.getTimeInMillis();

            cal.setTime(d2);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            n = cal.getTimeInMillis();
        }
        if (isAbs)
            return (int) Math.abs(((m - n) * 1.0) / (1000 * 60 * 60));
        return (int) (((m - n) * 1.0) / (1000 * 60 * 60));
    }

    /**
     * 计算两个日期之间相差的小时数
     * @param sdate
     * @param edate
     * @return
     * @throws Exception
     */
    public static double getHours(Date sdate, Date edate) throws Exception {
        long cha = sdate.getTime() - edate.getTime();
        return cha * 1.0 / (1000 * 60 * 60);
    }

    /**
     * 计算日期与当前日期相差的小时数
     * @param date
     * @return
     * @throws Exception
     */
    public static double getHours(Date date) throws Exception {
        return getHours(date, new Date());
    }


    /**
     * 计算两个日期之间相差的秒数
     *
     * @param sdate
     * @param edate
     * @return
     */
    public static double getSeconds(String sdate, String edate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(sdate);
        Date end = sdf.parse(edate);
        long cha = end.getTime() - start.getTime();
        return cha * 1.0 / 1000;
    }

    /**
     * 计算日期与当前日期相差的秒数
     *
     * @param date
     * @return
     */
    public static double getSeconds(String date) throws Exception {
        return getSeconds(date, fmtDateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 指定日期上一个旬的第一天
     */
    public static Date getLastTenStartDate(Date date) {
        Date returnDate = DateUtil.toShortDate(date);
        returnDate = DateUtil.getTenDaysStart(date);
        returnDate = DateUtil.addDay(returnDate, -1);
        returnDate = DateUtil.getTenDaysStart(returnDate);
        return DateUtil.toShortDate(returnDate);
    }

    /**
     * 指定日期上一个旬的最后一天
     */
    public static Date getLastTenEndDate(Date date) {
        Date returnDate = DateUtil.toShortDate(date);
        returnDate = DateUtil.getTenDaysStart(date);
        returnDate = DateUtil.addDay(returnDate, -1);
        return DateUtil.toShortDate(returnDate);
    }

    /**
     * 指定日期上个月第一天
     */
    public static Date getLastMonthStartDate(Date date) {
        Date returnDate = DateUtil.toShortDate(date);
        returnDate = DateUtil.getLastMonthStart(date);
        return DateUtil.toShortDate(returnDate);
    }

    /**
     * 指定日期上个月最后一天
     */
    public static Date getLastMonthEndDate(Date date) {
        Date returnDate = DateUtil.toShortDate(date);
        returnDate = DateUtil.getMonthStart(date);
        returnDate = DateUtil.addDay(returnDate, -1);
        return DateUtil.toShortDate(returnDate);
    }


    /**
     * 比较两个日期之间的大小
     *
     * @param d1
     * @param d2
     * @return 前者大于后者返回true 反之false
     */
    public static boolean compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        int result = c1.compareTo(c2);
        if (result >= 0)
            return true;
        else
            return false;
    }

    /**
     * 得到某年某周的第一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);

        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * 得到某年某周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * 取得指定日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()); // MONDAY
        return calendar.getTime();
    }

    /**
     * 取得指定日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek() + 6); // SUNDAY
        return calendar.getTime();
    }

    /**
     * 取得指定日期所在周的加"num"周的第一天
     *
     * @param date
     * @param num
     * @return
     */
    public static Date getFirstDayOfWeek(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFirstDayOfWeek(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.WEEK_OF_YEAR) + num);
    }

    /**
     * 取得指定日期所在周的加"num"周的最后一天
     *
     * @param date
     * @param num
     * @return
     */
    public static Date getLastDayOfWeek(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getLastDayOfWeek(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.WEEK_OF_YEAR) + num);
    }

    /**
     * 日期转化为cron表达式
     * @param date
     * @return
     */
    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return  DateUtil.fmtDateToStr(date, dateFormat);
    }

    /**
     * cron表达式转为日期
     * @param cron
     * @return
     */
    public static Date getCronToDate(String cron) {
        String dateFormat="ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * 日期格式字符串转换成时间戳 
     * @param date 字符串日期 
     * @param format 如：yyyy-MM-dd HH:mm:ss 
     * @return
     */
    public static String dateTimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**返回日期是本周第几天**/
    public static Integer getWeekday(Date date){//必须yyyy-MM-dd  
        Calendar rightNow=Calendar.getInstance();
        rightNow.setTime(date);
        return rightNow.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * 指定日期所在月的最后一天
     * @param date
     * @return
     */
    public static Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        return toShortDate(cal.getTime());
    }

    public static void main(String[] args) {

        System.out.println(getWeekday(new Date()));
        List<String> dates = new ArrayList<>();
        dates.add("2020-06-01 2020-06-03");
        dates.add("2020-07-01 2020-07-15");
        System.out.println("日期范围是否重叠: " + overlapping(dates));

        dates.add("2020-06-01 2020-8-15");
        System.out.println("日期范围是否重叠: " + overlapping(dates));

    }

    /**
     *
     * Description:指定日期加或减minute分钟
     *
     * @param date1日期
     * @param minute分钟数
     * @return
     */
    public static Date addMinute(Date date1, int minute) {
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.MINUTE, minute);
        return date.getTime();
    }

    /**
     * 年月日 补充时分秒，后者为当前时间
     * @param date
     * @return java.util.Date
     * @throws Exception
     * @author alexli
     * @date 2019/3/13 11:45
     */
    public static  Date fmtStrToDateSupplyTime(Date date) {
        return fmtStrToDateSupplyTime(fmtDateToYMD(date));
    }

    /**
     * 年月日 补充时分秒，后者为当前时间
     * @param date
     * @return java.util.Date
     * @throws Exception
     * @author alexli
     * @date 2019/3/13 11:45
     */
    public static  Date fmtStrToDateSupplyTime(String date) {
        String SupplyTime = fmtDateToStr(new Date(), "HH:mm:ss");
        return fmtStrToDate(date + " " + SupplyTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return fmtDateToYMDHMS(new Date());
    }


    /**
     * 日期指定天数，验证如果日期月没有那一天，则返回日期月的最后一天
     * @param date
     * @param day
     * @return java.util.Date
     * @throws Exception
     * @author alexli
     * @date 2018/12/14 14:55
     */
    public static Date getSpecifiedDate(Date date,Integer day) {
        Date returnDate = null;
        try {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, day);
                ZonedDateTime zdt = localDate.atStartOfDay(zoneId);

                returnDate = Date.from(zdt.toInstant());
            } catch (DateTimeException ex) {
                returnDate = DateUtil.getLastDayOfMonth(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDate;
    }


    /**
     * 时间戳转日期
     * @param str_num
     * @param format
     * @return
     * @author alexli
     * @date 2018年5月18日 下午6:21:40
     */
    public static Date timestampToDate(String str_num) {
        if (str_num.length() == 13) {
            return new Date(Long.parseLong(str_num));
        } else {
            return new Date(Integer.parseInt(str_num) * 1000L);
        }
    }


    /**
     *
     * Description:指定日期加或减seconds秒
     *
     * @param date1 日期
     * @param minutes 分钟数
     * @return
     */
    public static Date addMinutes(Date date1, int minutes) {
        if(minutes == 0){
            return date1;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        date.add(Calendar.MINUTE, minutes);
        return date.getTime();
    }

    /**
     * 获取指定时间-当天结束时间
     * @param date
     * @return
     */
    public static Date beginOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime();
    }

    /**
     * 获取指定时间-当天结束时间
     * @param date
     * @return
     */
    public static Date endOfDay(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime();
    }

    /**
     * 判断日期范围是否有重叠(日期到天)
     * @param list
     * @author alexli 2020/7/1 20:34
     */
    public static boolean overlapping(List<String> list) {
        if (list == null || list.size() <= 1) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String date1[] = list.get(i).split(" ");
            Date beginDate1 = DateUtil.toShortDate(date1[0]);
            Date endDate1 = DateUtil.toShortDate(date1[1]);
            for (int j = i + 1; j < list.size(); j++) {
                String date2[] = list.get(j).split(" ");
                Date beginDate2 = DateUtil.toShortDate(date2[0]);
                Date endDate2 = DateUtil.toShortDate(date2[1]);
                if (!beginDate2.before(beginDate1)) {
                    if (!beginDate2.after(endDate1)) {
                        return true;
                    }
                } else if (!endDate2.before(beginDate1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断时间范围是否有效
     * @param itemRange
     * @param dateStr
     * @author alexli 2020/8/28 11:37
     */
    public static  boolean itemRangeValid(String itemRange,String dateStr,Date startTime) {
        if (StringUtils.isBlank(itemRange)) {
            return false;
        }
        //判断时间是否有效（未过期）
        boolean isTrue = true;
        try {
            String[] timeStrs = itemRange.split("~");
            if (timeStrs != null && timeStrs.length > 0) {
                if (DateUtil.compareDate(startTime, DateUtil.fmtStrToDate(dateStr + " " + timeStrs[timeStrs.length - 1], "yyyy-MM-dd HH:mm"))) {
                    isTrue = false;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return isTrue;
    }

    /**
     * 获取当前时间距离一天结束的剩余秒数
     * @param currentDate
     * @author alexli 2020/9/23 15:44
     */
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

    /**
     * 获取较大时间
     */
    public static Date max(Date date1, Date date2) {
        Date date = cn.hutool.core.date.DateUtil.compare(date1, date2) > 0 ? date1 : date2;
        Assert.notNull(date, "比较两个时间， 获取最大一个， 两个其中一个必须不为空");
        return date;
    }

    /**
     * 获取较小时间
     */
    public static Date min(Date date1, Date date2) {
        Date date = CompareUtil.compare(date1, date2, true) > 0 ? date2 : date1;
        Assert.notNull(date, "比较两个时间， 获取最小一个， 两个其中一个必须不为空");
        return date;
    }

    /**
     * yyyyMMddHHmmssSSS时间返回
     */
    public static String ymdhmss() {
        return cn.hutool.core.date.DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
    }

    /**
     * 专门用来计算会员权益卡有限期，其它业务看情况使用
     * @param date1
     * @param date2
     * @return
     */
    public static int diffDays(Date date1,Date date2)
    {
        BigDecimal bigDecimal = new BigDecimal(((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24D))).setScale(0, BigDecimal.ROUND_UP);
        return bigDecimal.intValue();
    }

    /**
     * Date类型转换为10位时间戳
     * @param time
     * @return
     */
    public static Integer DateToTimestamp(Date time){
        Timestamp ts = new Timestamp(time.getTime());

        return (int) ((ts.getTime())/1000);
    }

    /**
     * 当前时间是否在开始时间和结束时间之间
     * @param start 开始时间
     * @param end 结束时间
     * @return true or false
     */
    public static boolean nowInBetween(Date start, Date end) {
        if (start == null || end == null) {
            return false;
        }
        Date now = new Date();
        return compareDate(now, start) && compareDate(end, now);
    }

    /**
     * 获取当天最后一秒的时间
     * @return
     */
    public static Date getLastSecondTime(Date time){
        Instant instant = time.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        localDateTime = localDateTime.withHour(23).withMinute(59).withSecond(59);
        instant = localDateTime.atZone(zone).toInstant();
        time = Date.from(instant);
        System.out.println(localDateTime);
        System.out.println(time);
        return time;
    }

    public static Date getFirstSecondTime(Date time){
        Instant instant = time.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        localDateTime = localDateTime.withHour(00).withMinute(00).withSecond(00);
        instant = localDateTime.atZone(zone).toInstant();
        time = Date.from(instant);
        System.out.println(localDateTime);
        System.out.println(time);
        return time;
    }

    /**
     * 饿了么字段获取日期
     */
    public static Date getEleDate(String dateString) {
        if (dateString.contains("T")) {
            dateString = dateString.replace("T"," ");
        }


        return DateUtil.fmtStrToDate(dateString);
    }

    public static java.sql.Time strToTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time time = new java.sql.Time(d.getTime());
        return time.valueOf(str);
    }

    // 01. java.util.Date --> java.time.LocalDateTime
    public static LocalDateTime UDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    // 02. java.util.Date --> java.time.LocalDate
    public static LocalDate UDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    // 03. java.util.Date --> java.time.LocalTime
    public static LocalTime UDateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }


    // 04. java.time.LocalDateTime --> java.util.Date
    public static Date LocalDateTimeToUdate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    // 05. java.time.LocalDate --> java.util.Date
    public Date LocalDateToUdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    // 06. java.time.LocalTime --> java.util.Date
    public Date LocalTimeToUdate(LocalTime localTime) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 指定日期是否为当天
     * @param date 日期
     * @return true or false
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        Date today = new Date();
        return today.getYear() == date.getTime() && today.getMonth() == date.getMonth() && today.getDay() == date.getDay();
    }

}
