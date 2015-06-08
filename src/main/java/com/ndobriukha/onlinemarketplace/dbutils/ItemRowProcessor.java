package com.ndobriukha.onlinemarketplace.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.Item.BooleanType;

public class ItemRowProcessor extends BasicRowProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		Item item = new Item();
		item.setId(rs.getInt(1));
		item.setSellerId(rs.getInt(2));
		item.setTitle(rs.getString(3));
		item.setDescription(rs.getString(4));
		item.setStartPrice(rs.getDouble(5));
		item.setTimeLeft(rs.getInt(6));
		item.setStartBidding((rs.getTimestamp(7)));
		item.setBuyItNow(BooleanType.valueOf(rs.getString(8)));
		item.setBidIncrement(rs.getDouble(9));
		item.setSold(BooleanType.valueOf(rs.getString(10)));
		return (T) item;
	}

	@SuppressWarnings("unchecked")
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
