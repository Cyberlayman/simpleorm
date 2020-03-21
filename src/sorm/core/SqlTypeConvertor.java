package sorm.core;

/**
 * sql 和 java 数据类型的转换
 * 如char -> String
 * 注意int -> 最好转 Integer
 *
 */
public class SqlTypeConvertor implements TypeConvertor {
    /**
     * sql 类型转java类型  常用的
     * @param dbType
     * @return
     */
    @Override
    public String dbType2javaType(String dbType) {
        if(dbType.endsWith("char")||dbType.endsWith("CHAR")){
            return "String";
        }else if(dbType.endsWith("int")||dbType.endsWith("INT")){
            return "Integer";
        }else if(dbType.endsWith("bigint")||dbType.endsWith("BIGINT")){
            return "long";
        }else if(dbType.endsWith("date")||dbType.endsWith("DATE")){
            return "java.sql.Date";
        }else if(dbType.endsWith("time")||dbType.endsWith("TIME")){
            return "java.sql.Time";
        }else if(dbType.equalsIgnoreCase("timestamp")){
            return "java.sql.Timestamp";
        }else if(dbType.equalsIgnoreCase("clob")){
            return "java.sql.Clob";
        }
        return null;
    }

    /**
     * 此方法未实现
     * @param javaType
     * @return
     */
    @Override
    public String javaType2dbType(String javaType) {
        return null;
    }
}
