package sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class JDBCUtils {
    /**
     * 给sql语句设置参数
     * @param ps   statement
     * @param params  参数数组
     * @return  影响的行数
     */
    public static int setSqlParams(PreparedStatement ps,Object[] params) {
        int count = 0;
        if(params!=null){
            int len = params.length;
            for (int i = 0; i < len; i++) {
                try {
                    ps.setObject(i+1,params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                count = ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 查询语句设置参数 并执行
     * @param ps  statement
     * @param params  参数数组
     */
    public static void selectSqlParams(PreparedStatement ps,Object[] params) {
        if(params!=null){
            int len = params.length;
            for (int i = 0; i < len; i++) {
                try {
                    ps.setObject(i+1,params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
