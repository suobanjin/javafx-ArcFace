package zzuli.zw.camera;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 该类继承了QueryRunner类，主要是为了简化操作，在Dao层处理事务调用其中的方法时就不用再指定connection
 */
public class TxQueryRunner extends QueryRunner {
    @Override
    public int[] batch(String sql, Object[][] params) throws SQLException {
        Connection conn= JDBCUtils.getConnection();
        assert conn != null;
        int[] res=super.batch(conn,sql,params);
        JDBCUtils.releaseConnection(conn);
        return res;
    }

    @Override
    public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        T t= super.query(conn,sql, rsh, params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        T t= super.query(conn,sql, rsh);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public int update(String sql) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        int t= super.update(conn,sql);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public int update(String sql, Object param) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        int t= super.update(conn,sql,param);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public int update(String sql, Object... params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        int t= super.update(conn,sql,params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public <T> T insert(String sql, ResultSetHandler<T> rsh) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        T t= super.insert(conn,sql,rsh);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public <T> T insert(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        T t= super.insert(conn,sql,rsh,params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public <T> T insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        T t= super.insertBatch(conn,sql,rsh,params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public int execute(String sql, Object... params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        int t= super.execute(conn,sql,params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }

    @Override
    public <T> List<T> execute(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        Connection conn=JDBCUtils.getConnection();
        assert conn != null;
        List<T> t= super.execute(conn,sql,rsh,params);
        JDBCUtils.releaseConnection(conn);
        return t;
    }
}
