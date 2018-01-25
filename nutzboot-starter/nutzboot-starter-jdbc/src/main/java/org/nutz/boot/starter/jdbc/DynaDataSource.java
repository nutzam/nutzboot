package org.nutz.boot.starter.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 动态数据源
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DynaDataSource implements DataSource {
    
    protected Iterator<DataSource> it;

    public DynaDataSource(Iterator<DataSource> it) {
        this.it = it;
    }

    /**
     * 通过迭代器获取下一个连接池,然后获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        return it.next().getConnection();
    }
    
    //------------------------------------------------------------
    // 其他方法是多余的

    public PrintWriter getLogWriter() throws SQLException {
        return it.next().getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return it.next().getConnection(username, password);
    }

}
