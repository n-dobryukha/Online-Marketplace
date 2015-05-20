package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ndobriukha.onlinemarketplace.dao.CommonDao;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.dbutils.BidRowProcessor;
import com.ndobriukha.onlinemarketplace.models.Bid;

public class OracleBidDao extends CommonDao<Bid> {

	public OracleBidDao(DataSource dataSource) {
		super(dataSource, Bid.class, new BidRowProcessor());
	}

	@Override
	public String getTableName() {
		return "BIDS";
	}

	@Override
	public String[] getColumnsName() {
		return new String[] { "ID", "BIDDER_ID", "ITEM_ID", "AMOUNT" };
	}
	
	/**
	 * Retrieves Bids list by ItemId
	 * @param itemId
	 * @return
	 * @throws PersistExistsException
	 * @throws PersistException
	 */
	public List<Bid> getBidsByItemId(int itemId) throws PersistExistsException, PersistException {
		String sql = getSelectQuery() + " WHERE ITEM_ID = ?";
		QueryRunner query = new QueryRunner(dataSource);
		BeanListHandler<Bid> beanListHandler = new BeanListHandler<Bid>(Bid.class, convert);
		List<Bid> result = null;
		try {
			result = query.query(sql, beanListHandler, itemId);
		} catch (SQLException e) {
			throw new PersistException(e);
		}						
		return result;
	}
	
	/**
	 * Retrieves Bids list by ItemId
	 * @param itemId
	 * @return
	 * @throws PersistExistsException
	 * @throws PersistException
	 */
	public Bid getBestBidByItemId(int itemId) throws PersistExistsException, PersistException {
		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM (");
		sqlBuilder.append(getSelectQuery());
		sqlBuilder.append(" WHERE ITEM_ID = ?");
		sqlBuilder.append(" ORDER BY AMOUNT DESC, ID DESC");
		sqlBuilder.append(") WHERE ROWNUM = 1");
		
		QueryRunner query = new QueryRunner(dataSource);
		BeanListHandler<Bid> beanListHandler = new BeanListHandler<Bid>(Bid.class, convert);
		List<Bid> result = null;
		try {
			result = query.query(sqlBuilder.toString(), beanListHandler, itemId);
		} catch (SQLException e) {
			throw new PersistException(e);
		}
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}
}
