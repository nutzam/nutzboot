package org.nutz.boot.starter.uflo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.boot.AppContext;
import org.nutz.dao.impl.SimpleDataSource;

public class UfloDataSourceProxy extends SimpleDataSource {

    protected DataSource origin;

    public Connection getConnection() throws SQLException {
        if (origin == null) {
            origin = AppContext.getDefault().getIoc().get(DataSource.class);
        }
        return origin.getConnection();
    }

}
