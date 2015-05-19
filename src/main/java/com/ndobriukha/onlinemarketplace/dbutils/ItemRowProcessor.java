package com.ndobriukha.onlinemarketplace.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.Item.BooleanType;

public class ItemRowProcessor extends BasicRowProcessor {

	@Override
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		Item item = new Item();
		item.setId(rs.getInt(1));
		item.setSellerId(rs.getInt(2));
		item.setDescription(rs.getString(3));
		item.setStartPrice(rs.getDouble(4));
		item.setTimeLeft(rs.getInt(5));
		item.setStartBidding((rs.getTimestamp(6)));
		item.setBuyItNow(BooleanType.valueOf(rs.getString(7)));
		item.setBidIncrement(rs.getDouble(8));
		item.setSold(BooleanType.valueOf(rs.getString(9)));
		return (T) item;
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
			throws SQLException {
		List<Item> items = new LinkedList<Item>();
		while (rs.next()) {
            items.add((Item) toBean(rs, type));
        }
		return (List<T>) items;
	}

}
