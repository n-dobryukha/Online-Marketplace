package com.ndobriukha.onlinemarketplace.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleBidDao;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleItemDao;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleUserDao;
import com.ndobriukha.onlinemarketplace.models.Bid;
import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.User;
import com.owlike.genson.Genson;

@Path("/")
public class ItemManagementService {
	
	@GET
	@Path("/show")
	@Produces(MediaType.TEXT_HTML)
	public Viewable show() {
		return new Viewable("/showitems");
	}
	
	@POST
	@Path("/show/all")
	@Produces(MediaType.TEXT_HTML)
	public String getAll(@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		HttpSession session = req.getSession();
		
		final List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		NumberFormat numberFormatter = new DecimalFormat("#0.00");
		try {
			OracleDaoFactory oraFactory = (OracleDaoFactory) session.getAttribute("daoFactory");
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(oraFactory.getContext(), Bid.class);
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);			
			List<Item> items = oraItemDao.getAll();
			for (Item item: items) {
				int sellerId = item.getSellerId();
				User seller = oraUserDao.get(sellerId);
				Map<String, Object> sellerData = new HashMap<String, Object>();
				sellerData.put("id", seller.getId());
				sellerData.put("name", seller.getFullName());
				
				Map<String, Object> bidderData = new HashMap<String, Object>() {{
					put("id", "");
					put("name", "");
				}};
				Bid bid = oraBidDao.getBestBidByItemId(item.getId());
				if (bid != null) {
					User bidder = oraUserDao.get(bid.getBidderId());
					if (bidder != null) {
						bidderData.put("id", seller.getId());
						bidderData.put("name", seller.getFullName());
					}			
				}
				Map<String, Object> dateData = new HashMap<String, Object>();
				Calendar cal = Calendar.getInstance();
				cal.setTime(item.getStartBidding());
				cal.add(Calendar.HOUR, item.getTimeLeft());
				
				dateData.put("display", (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cal.getTime())));
				dateData.put("timestamp", cal.getTime());
				
				Map<String, Object> dataItem = new HashMap<String, Object>();
				dataItem.put("uid", item.getId());
				dataItem.put("title", item.getTitle());
				dataItem.put("description", item.getDescription());				
				dataItem.put("seller", sellerData);
				dataItem.put("startPrice", numberFormatter.format(item.getStartPrice()));
				dataItem.put("bidInc", (!item.isBuyItNow()) ? numberFormatter.format(item.getBidIncrement()) : "");
				dataItem.put("bestOffer", (bid != null) ? numberFormatter.format(bid.getAmount()) : "");
				dataItem.put("bidder", bidderData);
				dataItem.put("stopDate", dateData);
				dataItem.put("action", (item.isBuyItNow()) ? "buy" : "bid");
				data.add(dataItem);
			}			
			
		} catch (PersistException e) {
			
			e.printStackTrace();
		}
		Map<String, List> results = new HashMap<String, List>() {{
			put("data", data);
		}};
		Genson genson = new Genson();		
		return genson.serialize(results);
		
/*		return "{"
				+"\"data\": ["
				+"{"
				+"\"uid\": \"1\","
				+"\"title\": \"A parrot\","
				+"\"description\": \"Very beautiful bird\","
				+"\"seller\": {"
				+"\"id\": 1,"
				+"\"name\": \"Mr Smith\""
				+"},"
				+"\"startPrice\": \"10.00\","
				+"\"bidInc\": \"1.50\","
				+"\"bestOffer\": \"15.00\","
				+"\"bidder\": {"
				+"\"id\": 4,"
				+"\"name\": \"Mrs Jonsen\""
				+"},"
				+"\"stopDate\": {"
				+"\"display\": \"24.11.2007 11:00\","
				+"\"timestamp\": \"1195902000\""
				+"},"
				+"\"action\": \"bid\""
				+"}]}";*/
	}
}