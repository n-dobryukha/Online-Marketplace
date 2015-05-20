package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ndobriukha.onlinemarketplace.dao.CommonDao;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dbutils.ItemRowProcessor;
import com.ndobriukha.onlinemarketplace.models.Item;

public class OracleItemDao extends CommonDao<Item> {

	public OracleItemDao(DataSource dataSource) {
		super(dataSource, Item.class, new ItemRowProcessor());
	}
	
	@Override
	public String getTableName() {
		return "ITEMS";
	}

	@Override
	public String[] getColumnsName() {
		return new String[] { "ID", "SELLER_ID", "TITLE", "DESCRIPTION", "START_PRICE",
				"TIME_LEFT", "START_BIDDING", "BUY_IT_NOW", "BID_INCREMENT", "SOLD" };
	}
	
	/**
	 * Retrieves items list by Description substring
	 * 
	 * @param substr
	 * @return
	 * @throws PersistException
	 */
	public List<Item> getItemsBySubstrDescr(String substr) throws PersistException {
		List<Item> result = null;
		String sql = getSelectQuery() + " WHERE DESCRIPTION LIKE ?";
		QueryRunner query = new QueryRunner(dataSource);
		BeanListHandler<Item> beanListHandler = new BeanListHandler<Item>(Item.class, new ItemRowProcessor());
		try {
			result = query.query(sql, beanListHandler, "%" + substr + "%");
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		return result;
	}

	/**
	 * Retrieves items list by Seller ID
	 * 
	 * @param selelrId
	 * @return
	 * @throws PersistException
	 */
	public List<Item> getItemsBySellerId(int selelrId) throws PersistException {
		List<Item> result = null;
		String sql = getSelectQuery() + " WHERE SELLER_ID = ?";
		QueryRunner query = new QueryRunner(dataSource);
		BeanListHandler<Item> beanListHandler = new BeanListHandler<Item>(Item.class, new ItemRowProcessor());
		try {
			result = query.query(sql, beanListHandler, selelrId);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		return result;
	}
}
