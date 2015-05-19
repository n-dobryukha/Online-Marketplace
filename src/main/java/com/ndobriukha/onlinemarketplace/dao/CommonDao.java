package com.ndobriukha.onlinemarketplace.dao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ndobriukha.onlinemarketplace.dbutils.MyQueryRunner;
import com.ndobriukha.onlinemarketplace.models.RetrieveFieldsValues;

public abstract class CommonDao<T extends Identified> implements GenericDao<T> {

	protected DataSource dataSource;

	private Class<T> type;

	private RowProcessor convert;

	public CommonDao(DataSource dataSource, Class<T> type) {
		this(dataSource, type, new BasicRowProcessor());
	}

	public CommonDao(DataSource dataSource, Class<T> type,
			RowProcessor convert) {
		this.dataSource = dataSource;
		this.type = type;
		this.convert = convert;
	}
	
	public abstract String getTableName();
	
	public abstract String[] getColumnsName();

	public abstract String getSelectQuery();

	public abstract String getUpdateQuery();

	public abstract String getDeleteQuery();

	@Override
	public void save(T object) throws PersistException {
		if (object.getId() == null) {
			create(object);
		} else {
			update(object);
		}
	}
	
	public String getCreateQuery() {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		boolean isFirst = true;
		for (String column: getColumnsName()) {
			if (column.equals("ID")) {
				continue;
			}
			if (isFirst) {
				columns.append(column);
				values.append("?");
			} else {
				columns.append("," + column);
				values.append(",?");
			}
			isFirst = false;
		}
		return "INSERT INTO " + getTableName() + " (" + columns.toString() + ") VALUES(" + values.toString() + ")";
	}
	
	private void create(T object) throws PersistException {
		if (object.getId() != null) {
			throw new PersistException("Object is already persist.");
		}
		T savedObject;
		String sql = getCreateQuery();		
		MyQueryRunner query = new MyQueryRunner(true);
		ResultSetHandler<T> resultHandler = new BeanHandler<T>(
				(Class<T>) object.getClass(), convert);
		String[] columnNames = getColumnsName();
		Object[] columnValues = ((RetrieveFieldsValues) object)
				.getFieldsValues();
		try {
			savedObject = (T) query.insert(dataSource.getConnection(), sql, columnNames,
					resultHandler, columnValues);
			object.setId(savedObject.getId());
		} catch (SQLException e) {
			if (e.getErrorCode() == 1) {
				throw new PersistConstraintException(e);
			} else {
				throw new PersistException(e);
			}
		}
	}
	
	private void update(T object) throws PersistException {
		String sql = getUpdateQuery();
		QueryRunner query = new QueryRunner(dataSource);
		try {
			Object[] params = ((RetrieveFieldsValues) object).getFieldsValues();
			params = Arrays.copyOf(params, params.length + 1);
			params[params.length - 1] = object.getId().toString();
			int count = query.update(sql, params);
			if (count != 1) {
				throw new PersistException(
						"On update modify more then 1 record: " + count);
			}
		} catch (SQLException e) {
			throw new PersistException(e);
		}
	}

	@Override
	public T get(int id) throws PersistException {
		List<T> list = null;
		String sql = "SELECT * FROM " + getTableName() +" WHERE id = ?";
		BeanListHandler<T> beanListHandler = new BeanListHandler<T>(type,
				convert);
		QueryRunner query = new QueryRunner(dataSource);
		try {
			list = query.query(sql, beanListHandler, id);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		if (list == null || list.size() == 0) {
			return null;
		}
		if (list.size() > 1) {
			throw new PersistException("Received more than one record.");
		}
		return list.get(0);
	}

	@Override
	public List<T> getAll() throws PersistException {
		List<T> list = null;
		String sql = "SELECT * FROM " + getTableName();
		BeanListHandler<T> beanListHandler = new BeanListHandler<T>(type,
				convert);
		QueryRunner query = new QueryRunner(dataSource);
		try {
			list = query.query(sql, beanListHandler);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		return list;
	}

	@Override
	public void delete(T object) throws PersistException {
		// TODO Auto-generated method stub
		
	}
	
	
	

	/*public T persist(T object) throws PersistException {
		if (object.getId() != null) {
			throw new PersistException("Object is already persist.");
		}
		T persistInstance;
		String sql = getCreateQuery();
		MyQueryRunner query = new MyQueryRunner(true);
		ResultSetHandler<T> resultHandler = new BeanHandler<T>(
				(Class<T>) object.getClass(), convert);
		String[] columnNames = getAutoGeneratedKeys();
		Object[] columnValues = ((RetrieveFieldsValues) object)
				.getFieldsValues();
		try {
			persistInstance = (T) query.insert(connection, sql, columnNames,
					resultHandler, columnValues);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1) {
				throw new PersistConstraintException(e);
			} else {
				throw new PersistException(e);
			}
		}
		return persistInstance;
	}

	public T getByPK(Integer key) throws PersistException {
		List<T> list = null;
		String sql = getSelectQuery() + " WHERE id = ?";
		BeanListHandler<T> beanListHandler = new BeanListHandler<T>(type,
				convert);
		QueryRunner query = new QueryRunner();
		try {
			list = query.query(connection, sql, beanListHandler, key);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		if (list == null || list.size() == 0) {
			return null;
		}
		if (list.size() > 1) {
			throw new PersistException("Received more than one record.");
		}
		return list.get(0);
	}

	public void update(T object) throws PersistException {
		String sql = getUpdateQuery();
		QueryRunner query = new QueryRunner();
		try {
			Object[] params = ((RetrieveFieldsValues) object).getFieldsValues();
			params = Arrays.copyOf(params, params.length + 1);
			params[params.length - 1] = object.getId().toString();
			int count = query.update(connection, sql, params);
			if (count != 1) {
				throw new PersistException(
						"On update modify more then 1 record: " + count);
			}
		} catch (SQLException e) {
			throw new PersistException(e);
		}
	}

	public void delete(T object) throws PersistException {
		String sql = getDeleteQuery();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			try {
				statement.setObject(1, object.getId());
			} catch (Exception e) {
				throw new PersistException(e);
			}
			int count = statement.executeUpdate();
			if (count != 1) {
				throw new PersistException(
						"On delete modify more then 1 record: " + count);
			}
			statement.close();
		} catch (Exception e) {
			throw new PersistException(e);
		}
	}

	public List<T> getAll() throws PersistException {
		List<T> list = null;
		String sql = getSelectQuery();
		BeanListHandler<T> beanListHandler = new BeanListHandler<T>(type,
				convert);
		QueryRunner query = new QueryRunner();
		try {
			list = query.query(connection, sql, beanListHandler);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		return list;
	}*/
}
