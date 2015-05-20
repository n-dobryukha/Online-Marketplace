package com.ndobriukha.onlinemarketplace.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.ndobriukha.onlinemarketplace.models.User;

public class UserRowProcessor extends BasicRowProcessor {
	
	@Override
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		User user = new User();
		user.setId(rs.getInt(1));
		user.setFullName(rs.getString(2));
		user.setBillingAddress(rs.getString(3));
		user.setLogin(rs.getString(4));
		user.setPassword(rs.getString(5));
		user.setEmail(rs.getString(6));
		return (T) user;
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
			throws SQLException {
		List<User> users = new LinkedList<User>();
		while (rs.next()) {
            users.add((User) toBean(rs, type));
        }
		return (List<T>) users;
	}

}
