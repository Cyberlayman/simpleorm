package sorm.utils;

import sorm.bean.ColumnInfo;
import sorm.bean.Configuration;
import sorm.bean.JavabeanFileSrc;
import sorm.bean.TableInfo;
import sorm.core.DBManager;
import sorm.core.SqlTypeConvertor;
import sorm.core.TypeConvertor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 拼接标准的Javabean类代码
 * 先封装 属性 get set
 * 再完整的 包 类结构 等
 * 最后写入 包下   路径是配置文件里的srcPath + poPackage
 */
public class JavaSourceFileUtils {
    /**
     *
     * @param c  列信息 属性
     * @param tc  类型转换接口
     * @return    一个属性的 声明 set get 方法
     */
    public static JavabeanFileSrc createJavabeanFileSrc(ColumnInfo c, TypeConvertor tc){
        JavabeanFileSrc jbfs = new JavabeanFileSrc();
        String javaType = tc.dbType2javaType(c.getDataType());
        jbfs.setAttributesLanguage("\tprivate "+javaType+" "+c.getName()+";\n");

        //public String getUsername(){return username;}
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic "+javaType+" get"+StringUtils.firstChar2Uppercase(c.getName())+"(){\n");
        getSrc.append("\t\treturn "+c.getName()+";\n");
        getSrc.append("\t}\n");
        jbfs.setGetMethodLanguage(getSrc.toString());

        //public void setUsername(String username){this.username = username;}
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void"+" set"+StringUtils.firstChar2Uppercase(c.getName())+"("+javaType+" "+c.getName()+"){\n");
        setSrc.append("\t\tthis."+c.getName()+"="+c.getName()+";\n");
        setSrc.append("\t}\n");
        jbfs.setSetMethodLanguage(setSrc.toString());

        return jbfs;
    }

    /**
     * 完整的Javabean源码
     * @param t   表信息
     * @param tc   装换接口
     * @return    String src
     */
    public static String finalJavabeanFileSrc(TableInfo t,TypeConvertor tc){
        Map<String, ColumnInfo> columns = t.getColumns();
        List<JavabeanFileSrc> gsc = new ArrayList<>();
        for (ColumnInfo c:columns.values()){
            gsc.add(createJavabeanFileSrc(c,new SqlTypeConvertor()));
        }
        Configuration conf = DBManager.getConf();

        StringBuilder src = new StringBuilder();
        src.append("package "+conf.getPoPackage()+";\n\n");

        src.append("import java.util.*;\n");
        src.append("import java.sql.*;\n\n");

        src.append("public class "+StringUtils.firstChar2Uppercase(t.getTableName())+" {\n");

        for (JavabeanFileSrc j:gsc){
            src.append(j.getAttributesLanguage());
        }
        src.append("\n");

        for (JavabeanFileSrc j:gsc){
            src.append(j.getGetMethodLanguage());
            src.append(j.getSetMethodLanguage());
            src.append("\n");
        }

        src.append("}");

        return src.toString();
    }

    /**
     * 表信息 转换成类 写入包中 依赖本类的其他方法
     * @param t   表信息
     * @param tc   类型转换接口
     */
    public static void srcIntoPo(TableInfo t,TypeConvertor tc){
        String srcPath = DBManager.getConf().getSrcPath()+"/";
        String poPackage = DBManager.getConf().getPoPackage().replaceAll("\\.","/");
        File f = new File(srcPath + poPackage);
        if(!f.exists()){
            f.mkdirs();
        }

        String src = finalJavabeanFileSrc(t, tc);
        //System.out.println(src);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()
                    +"/"+StringUtils.firstChar2Uppercase(t.getTableName())+".java"));
            bw.write(src);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("建立了表["+t.getTableName()+"]对应的java类："+StringUtils.firstChar2Uppercase(t.getTableName())+".java");
    }
}
