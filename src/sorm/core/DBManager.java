package sorm.core;

import sorm.bean.Configuration;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * 根据配置文件连接管理
 * 加载配置信息 加载TableClassReflect类，加载表信息
 * 封装了连接和关闭操作
 */
public class DBManager {
    private static Configuration conf;

    private static DBConnectionPool pool;

    static {
        Properties p = new Properties();
        try {
            p.load(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("mysqlconn.properties"));
            conf = new Configuration();
            conf.setDriver(p.getProperty("driver"));
            conf.setUrl(p.getProperty("url"));
            conf.setUser(p.getProperty("user"));
            conf.setPwd(p.getProperty("pwd"));
            conf.setUsingDB(p.getProperty("usingDB"));
            conf.setSrcPath(p.getProperty("srcPath"));
            conf.setPoPackage(p.getProperty("poPackage"));

            System.out.println(conf);
            // 加载这个类
            System.out.println(TableClassReflect.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConf(){
        return conf;
    }

    public static Connection getMySQLConnection(){
        if(pool==null){
            pool = new DBConnectionPool();
        }
        return pool.getConnFromPool();
    }

    /**
     * 这个是原来的getMySQLConnection()，这里复制下给创建连接池时使用
     * 原来的经常被使用，替换成从连接池获取
     * @return
     */
    public static Connection getMySQLConnection2(){
        Connection conn = null;
        try {
            Class.forName(conf.getDriver());
            conn = DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }finally {
            return conn;
        }
    }


    public static void closeAll(ResultSet rs, Statement s, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(s!=null){
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        pool.close(conn);
    }

    public static void closeAll(Statement s,Connection conn){
        if(s!=null){
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        pool.close(conn);
    }

    public static void closeAll(Connection conn){
        pool.close(conn);
    }

    public static void main(String[] args) {

    }
}
