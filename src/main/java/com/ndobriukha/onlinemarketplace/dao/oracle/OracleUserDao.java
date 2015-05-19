package com.ndobriukha.onlinemarketplace.dao.oracle;

import javax.sql.DataSource;

import com.ndobriukha.onlinemarketplace.dao.CommonDao;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleUserDao extends CommonDao<User> {

	public OracleUserDao(DataSource dataSource) {
		super(dataSource, User.class);
	}
	
	@Override
	public String[] getColumnsName() {
		return new String[] { "ID", "FULL_NAME", "BILLING_ADDRESS", "LOGIN",
				"PASSWORD", "EMAIL" };
	}

	@Override
	public String getSelectQuery() {
		return "SELECT id, fullName, billingAddress, login, password, email FROM Users";
	}

	@Override
	public String getUpdateQuery() {
		return "UPDATE Users SET fullName = ?, billingAddress = ?, login = ?, password = ?, email = ? WHERE id = ?";
	}

	@Override
	public String getDeleteQuery() {
		return "DELETE FROM Users WHERE id = ?";
	}
/*
	public User create(String fullName, String billingAddress, String login, String password, String email) throws PersistException {
		User user = new User(fullName, billingAddress, login, password, email);
		return persist(user);
	}

	public User getUserByLogin(String login) throws PersistExistsException, PersistException {
		String sql = getSelectQuery() + " WHERE login = ?";
		QueryRunner query = new QueryRunner();
		BeanListHandler<User> beanListHandler = new BeanListHandler<User>(User.class);
		List<User> users = null;
		try {
			users = query.query(connection, sql, beanListHandler, login);
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
*/
	@Override
	public String getTableName() {
		return "Users";
	}
}
