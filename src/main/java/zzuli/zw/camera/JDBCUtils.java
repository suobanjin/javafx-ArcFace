package zzuli.zw.camera;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 处理数据库连接的类，同时封装了对事务的处理
 */
public class JDBCUtils {
    //数据库连接池C3P0
   private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
   //用来处理多线程并发处理问题(并发问题的出现是因为共享了成员变量的原因)
   private static ThreadLocal<Connection> tl = new ThreadLocal<>();
    /**
     * 通过C3p0数据库连接池获取数据库连接Connection
     * @return Connection
     */
    public static Connection getConnection(){
        Connection conn=tl.get();
        try {
            if (conn!=null) return conn;
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取ComboPooledDataSource
     * @return ComboPooledDataSource
     */
    public static ComboPooledDataSource getDataSource(){
        return dataSource;
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = tl.get();
        System.out.println(conn);
        if (conn != null) throw new SQLException("已经开启事务,请勿重复开启！");
        conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        tl.set(conn);
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = tl.get();
        if (conn!=null){
            conn.commit();
            conn.close();
            conn=null;
            tl.remove();
        }else{
            throw new SQLException("事务还未开启,无法提交！");
        }
    }
    /**
     * 回滚事务
     */
    public static void rollbackTransaction() throws SQLException {
        Connection conn=tl.get();
        if (conn!=null){
            conn.rollback();
            conn.close();
            conn=null;
            tl.remove();
        }else{
            throw new SQLException("事务还未开始,无法回滚！");
        }
    }
    /**
     * 关闭连接,释放资源
     * @param connection Connection
     */
    public static void releaseConnection(Connection connection) throws SQLException {
        Connection conn=tl.get();
        if (conn==null){
            connection.close();
        }else if (connection!=conn){
            connection.close();
        }
        tl.remove();
    }
}
