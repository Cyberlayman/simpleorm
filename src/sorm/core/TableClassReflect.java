package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JavaSourceFileUtils;
import sorm.utils.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取表信息的核心类 在 DBmanager中有加载
 * 管理获取的表结构和类结构关系，并可以根据表生成类（javabean）
 */
public class TableClassReflect {
    /**
     * 表名和表信息 的映射集合
     */
    private static Map<String, TableInfo> tables = new HashMap<>();
    /**
     * 反射类 与表信息的映射集合
     */
    public static Map<Class,TableInfo> poClassTableMap = new HashMap<>();

    private TableClassReflect(){}

    static {
        Connection conn = null;
        try {
            conn = DBManager.getMySQLConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet tableSet = dbmd.getTables("myfirstdb", "%", "%", new String[]{"TABLE"});
            while(tableSet.next()){
                String tableName = (String) tableSet.getObject("TABLE_NAME");
                TableInfo ti = new TableInfo(tableName, new HashMap<String, ColumnInfo>(), new ArrayList<ColumnInfo>());
                tables.put(tableName,ti);
                ResultSet set = dbmd.getColumns(null, "%", tableName, "%"); //查询所有
                while (set.next()){
                    ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
                    ti.getColumns().put(set.getString("COLUMN_NAME"),ci);
                }
                ResultSet set2 = dbmd.getPrimaryKeys("myfirstdb", "%",tableName);  //查询主键  catalog都写上
                while(set2.next()){
                    ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
                    ci2.setKeyType(1);  //设置为主键
                    ti.getPriKeys().add(ci2);
                }
                if(ti.getPriKeys().size()>0){    //取唯一主键 ，如果主键不止一个则为空
                    ti.setOnlyKey(ti.getPriKeys().get(0));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBManager.closeAll(conn);
        }
        updatePoPackage();
        loadPoPackage();
    }


    /**
     * 生成 对应的java代码并放入po包中  此方法放入上面的静态代码块中 相当于每次执行都更新
     */
    public static void updatePoPackage(){
        //Map<String, TableInfo> tables = TableClassReflect.getTables();
        for (TableInfo t:tables.values()){
            JavaSourceFileUtils.srcIntoPo(t,new SqlTypeConvertor());
        }
    }

    /**
     * 生成的Javabean文件 写入po包中
     */
    public static void loadPoPackage(){
        for(TableInfo t:tables.values()){
            Class<?> c = null;
            try {
                c = Class.forName(DBManager.getConf()
                        .getPoPackage() + "." + StringUtils.firstChar2Uppercase(t.getTableName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            poClassTableMap.put(c,t);
        }
    }

    /**
     * 获取tables方法
     * @return
     */
    public static Map<String,TableInfo> getTables(){
        return tables;
    }
}
