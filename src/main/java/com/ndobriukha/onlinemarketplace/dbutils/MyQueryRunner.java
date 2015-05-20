package com.ndobriukha.onlinemarketplace.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.AbstractQueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class MyQueryRunner extends AbstractQueryRunner {
	
	
	public MyQueryRunner(boolean pmdKnownBroken) {
        super(pmdKnownBroken);
    }
	
	public <T> T insert(Connection conn, String sql, String[] columnNames, ResultSetHandler<T> rsh, Object... params)
            throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }

        if (rsh == null) {
            throw new SQLException("Null ResultSetHandler");
        }

        PreparedStatement stmt = null;
        T generatedKeys = null;

        try {
            stmt = conn.prepareStatement(sql, columnNames);
        	this.fillStatement(stmt, params);
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            generatedKeys = rsh.handle(resultSet);
        } catch (SQLException e) {
            this.rethrow(e, sql, params);
        } finally {
            close(stmt);            
        }

        return generatedKeys;
    }
}
