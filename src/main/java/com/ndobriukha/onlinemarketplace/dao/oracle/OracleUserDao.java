package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ndobriukha.onlinemarketplace.dao.CommonDao;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.dbutils.UserRowProcessor;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleUserDao extends CommonDao<User> {

	public OracleUserDao(DataSource dataSource) {
		super(dataSource, User.class, new UserRowProcessor());
	}
	
	@Override
	public String getTableName() {
		return "USERS";
	}
	
	@Override
	public String[] getColumnsName() {
		return new String[] { "ID", "FULL_NAME", "BILLING_ADDRESS", "LOGIN",
				"PASSWORD", "EMAIL" };
	}

	public User getUserByLogin(String login) throws PersistExistsException, PersistException {
		String sql = getSelectQuery() + " WHERE login = ?";
		QueryRunner query = new QueryRunner(dataSource);
		BeanListHandler<User> beanListHandler = new BeanListHandler<User>(User.class, convert);
		List<User> users = null;
		try {
			users = query.query(sql, beanListHandler, login);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		if (users == null || users.size() == 0) {
			throw new PersistExistsException("User with login = '" + login + "' doesn't exists");
		}
		if (users.size() > 1) {
			throw new PersistException("Received more than one record.");
		}						
		return users.get(0);
	}	
}
