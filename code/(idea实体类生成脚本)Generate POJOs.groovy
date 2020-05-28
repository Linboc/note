import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil
import java.io.*
import java.text.SimpleDateFormat

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

packageName = "com.bmvc.service.crm.entity;"
typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "Date",
        (~/(?i)date/)                     : "Date",
        (~/(?i)time/)                     : "Date",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
  SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}


def generate(table, dir) {
  def tableComment = getTableComment(table)
  if (tableComment == null || "".equals(tableComment)) {
    tableComment = ""
  }
  def className = javaName(table.getName(), true)
  def fields = calcFields(table)
  PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, className + ".java")), "utf-8"))
  output.withPrintWriter { out -> generate(out, className, table.getName(), fields, tableComment) }
}

def generate(out, className, tableName, fields, tableComment) {
  out.println "package $packageName\n"
  out.println "import java.io.Serializable;"
  out.println "import javax.persistence.Column;"
  out.println "import javax.persistence.Entity;"
  out.println "import javax.persistence.Table;"
  out.println "import java.util.Date;"
  out.println "import javax.persistence.Temporal;"
  out.println "import javax.persistence.TemporalType;"

  out.println ""
  out.println ""
  out.println "/**\n" +
          " * " + tableComment + "\n" +
          " * @author boc\n" +
          " * Date: " + getNowDateYMS() + "\n" +
          " */"

  out.println "@Entity"
  out.println "@Table(name =  \"$tableName\")"
  out.println "public class $className implements Serializable {"
  out.println ""
  fields.each() {
    if (isNotEmpty(it.commoent)) {
      out.println "    /** ${it.commoent} */"
    }
    if (it.annos != "") out.println "  ${it.annos}"
    out.println "    private ${it.type} ${it.name};\n"
  }
  fields.each() {
    out.println "    @Column(name = \"${it.colName}\")"
    if (it.type == "Date") out.println "    @Temporal(TemporalType.TIMESTAMP)"
    out.println "    public ${it.type} get${it.name.capitalize()}() {"
    out.println "        return ${it.name};"
    out.println "    }"
    out.println ""
    out.println "    public void set${it.name.capitalize()}(${it.type} ${it.name}) {"
    out.println "        this.${it.name} = ${it.name};"
    out.println "    }"
    out.println ""
  }
  out.println "}"
}

def calcFields(table) {
  DasUtil.getColumns(table).reduce([]) { fields, col ->
    def spec = Case.LOWER.apply(col.getDataType().getSpecification())
    def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
    fields += [[
                       name    : javaName(col.getName(), false),
                       type    : typeStr,
                       colName : col.getName(),
                       commoent: col.getComment(),
                       annos   : ""]]
  }
}

def getTableComment(table) {

  return table.getComment();
}


def javaName(str, capitalize) {
  def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
          .collect { Case.LOWER.apply(it).capitalize() }
          .join("")
          .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
  capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

def isNotEmpty(content) {
  return content != null && content.toString().trim().length() > 0
}

def getNowDateYMS() {
  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")// 设置日期格式
  return df.format(new Date())
}