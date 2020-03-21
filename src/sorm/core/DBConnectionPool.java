package sorm.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库连接池 当前只有MySQL
 */
public class DBConnectionPool {
    private static List<Connection> pool;

    public static final int MIN_SIZE = 10;

    public static final int MAX_SIZE = 100;

    public DBConnectionPool(){
        initPool();
    }

    public void initPool(){
        if(pool==null){
            pool = new ArrayList<>();
        }
        while (pool.size()<MIN_SIZE){
            pool.add(DBManager.getMySQLConnection2());
        }
    }

    public synchronized Connection getConnFromPool(){
        Connection conn = pool.get(pool.size() - 1);
        pool.remove(pool.size()-1);
        return conn;
    }

    public synchronized void close(Connection conn){
        if(pool.size()>MAX_SIZE){
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else {
            pool.add(conn);
        }
    }
}
