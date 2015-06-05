package com.ndobriukha.onlinemarketplace.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.ndobriukha.onlinemarketplace.models.Bid;

public class BidRowProcessor extends BasicRowProcessor {
	@Override
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		Bid bid = new Bid();
		bid.setId(rs.getInt(1));
		bid.setItemId(rs.getInt(2));
		bid.setBidderId(rs.getInt(3));		
		bid.setAmount(rs.getDouble(4));
		bid.setTimestamp(rs.getTimestamp(5));
		return (T) bid;
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
			throws SQLException {
		List<Bid> bids = new LinkedList<Bid>();
		while (rs.next()) {
            bids.add((Bid) toBean(rs, type));
        }
		return (List<T>) bids;
	}
}
