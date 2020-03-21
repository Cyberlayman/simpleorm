package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用的核心类
 *
 */
public class MysqlQuery implements Query {
    /**
     * java.lang.NoSuchMethodException: com.sorm.po.Regtable.setId(java.lang.Integer)
     * 写反射类代码时  尽量不要使用基本数据类型 如 int 使用 Integer等 否则各种不方便
     */
    @Override
    public int executeDML(String sql, Object[] params) {
        int count = 0;
        Connection conn = DBManager.getMySQLConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            count = JDBCUtils.setSqlParams(ps,params);
            System.out.println(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBManager.closeAll(ps,conn);
        }
        return count;
    }

    @Override
    public void insert(Object o) {
        if(o==null){
            System.out.println("对象为空");
            return;
        }
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields(); // 注意和 getFields() 的区别
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getTableName()+" (");
        int notNullFieldCount = 0;
        for (Field f:fields){
            String fName = f.getName();
            Object fValue = ReflectUtils.invokeGet(fName, o);
            if(fValue instanceof Integer){   //基本数据类型 不能用是否为null判断
                notNullFieldCount++;
                sql.append(fName+",");
                params.add(fValue);
            }else if(fValue!=null){
                notNullFieldCount++;
                sql.append(fName+",");
                params.add(fValue);
            }
        }
        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for (int i = 0; i < notNullFieldCount; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');

        executeDML(sql.toString(),params.toArray());

    }

    @Override
    public int delete(Class clazz, Object priKey) {
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        ColumnInfo onlyKey = tableInfo.getOnlyKey();

        String sql = "delete from "+tableInfo.getTableName()+" where "+onlyKey.getName()+"=?";

        executeDML(sql,new Object[]{priKey});
        return 1;
    }

    @Override
    public void delete(Object o) {
        Class<?> clazz = o.getClass();
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        ColumnInfo onlyKey = tableInfo.getOnlyKey();

        Object priKeyValue = ReflectUtils.invokeGet(onlyKey.getName(), o);

        delete(clazz,priKeyValue);
    }

    @Override
    public int update(Object o, String[] propNames) {
        int count = 0;
        // update tableName set propName=?,propName=? where priKey=?
        Class<?> clazz = o.getClass();
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("update "+tableInfo.getTableName()+" set ");

        for(String PropName:propNames){
            sql.append(PropName+"=?,");
            Object pValue = ReflectUtils.invokeGet(PropName, o);
            params.add(pValue);
        }
        sql.setCharAt(sql.length()-1,' ');
        String onlyKeyName = tableInfo.getOnlyKey().getName();
        params.add(ReflectUtils.invokeGet(onlyKeyName,o));
        sql.append("where "+onlyKeyName+"=?");
        //System.out.println(sql.toString());

        executeDML(sql.toString(),params.toArray());
        return count;
    }

    @Override
    public int update(Object o) {
        Class<?> clazz = o.getClass();
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        Map<String, ColumnInfo> columns = tableInfo.getColumns();
        String priName = tableInfo.getOnlyKey().getName();
        List<String> sl = new ArrayList<>();
        for(ColumnInfo c:columns.values()){
            if(!priName.equals(c.getName())){
                sl.add(c.getName());
            }
        }
        //sl.forEach(System.out::println);
        int n = sl.size();
        String[] ss = new String[n];
        int i = 0;
        for(String s:sl){
            ss[i] = s;
            i++;
        }
        return update(o,ss);
    }

    @Override
    public List queryRows(String sql, Class clazz, Object[] params) {
        List<Object> resultList = null;
        Connection conn = DBManager.getMySQLConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            JDBCUtils.selectSqlParams(ps,params);
            rs = ps.executeQuery();
            System.out.println(ps);
            ResultSetMetaData metaData = rs.getMetaData();

            while(rs.next()){
                if(resultList==null){
                    resultList = new ArrayList<>();
                }
                // 单行 或者说单个对象
                Object o = clazz.getDeclaredConstructor().newInstance();
                //处理列 column
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String labelName = rs.getMetaData().getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    //  调用Javabean 的反射set方法 设置值
                    ReflectUtils.invokeSet(o,labelName,value);
                }
                resultList.add(o);
            }
        } catch (SQLException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            DBManager.closeAll(rs,ps,conn);
        }
        return resultList;
    }

    @Override
    public List queryRows(Class clazz, Object[] limits) {
        if(limits.length==0||limits.length>2){
            System.out.println("参数错误");
            return null;
        }
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        String tableName = tableInfo.getTableName();
        String priKeyName = tableInfo.getOnlyKey().getName();
        String sql = "select * from "+tableName+" where "+priKeyName+">?";
        if(limits.length==1){

        }else if(limits.length==2 && limits[0]!=null){
            sql = sql + " and "+priKeyName+"<?";
        }else {
            sql = "select * from "+tableName+" where "+priKeyName+"<?";
        }
        System.out.println(sql);
        return queryRows(sql,clazz,limits);
    }

    @Override
    public Object queryRow(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql, clazz, params);
        return list.size()==1?list.get(0):null;
    }

    @Override
    public Object queryRow(Class clazz, Object keyValue) {
        TableInfo tableInfo = TableClassReflect.poClassTableMap.get(clazz);
        String tableName = tableInfo.getTableName();
        String priKeyName = tableInfo.getOnlyKey().getName();
        String sql = "select * from "+tableName+" where "+priKeyName+"=?";

        return queryRow(sql,clazz,new Object[]{keyValue});
    }

    @Override
    public Object queryValue(String FieldName, String tableName, Object priKey) {
        // select ? from tableName where priKey=?
        Object value = null;
        Connection conn = DBManager.getMySQLConnection();
        TableInfo tableInfo = TableClassReflect.getTables().get(tableName);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select "+FieldName+" from "+tableName+" where "+tableInfo.getOnlyKey().getName()+"=?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1,priKey);
            rs = ps.executeQuery();
            while (rs.next()){
                value = (Object)rs.getObject(FieldName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.closeAll(rs,ps,conn);
            return value;
        }
    }

    @Override
    public Number queryNumber(String FieldName, String tableName, Object priKey) {
        Object o = queryValue(FieldName, tableName, priKey);
        if(o instanceof Number){
            return (Number)o;
        }else {
            System.out.println("所查询的属性不可以转成数字");
            return null;
        }
    }
}
