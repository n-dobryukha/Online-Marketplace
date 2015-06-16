package com.ndobriukha.onlinemarketplace.services;

import java.net.URI;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleBidDao;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleItemDao;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleUserDao;
import com.ndobriukha.onlinemarketplace.models.Bid;
import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.Item.BooleanType;
import com.ndobriukha.onlinemarketplace.models.User;
import com.owlike.genson.Genson;

@Path("/")
public class ItemManagementService {
	
	private OracleDaoFactory oraFactory;
	
	public ItemManagementService() {
		try {
			oraFactory = new OracleDaoFactory("java:/comp/env/jdbc/marketplace");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}
	
	@GET
	@Path("/show/all")
	@Produces(MediaType.TEXT_HTML)
	public Viewable showAll() {
		return new Viewable("/showitems", "All");
	}
	
	@GET
	@Path("/show/my")
	@Produces(MediaType.TEXT_HTML)
	public Viewable showMy() {
		return new Viewable("/showitems", "My");
	}
	
	@SuppressWarnings("serial")
	private Map<String, Object> getEmptyItemData() {
		Map<String, Object> result = new HashMap<String, Object>();		
		result.put("uid", "");
		result.put("title", "");
		result.put("description", "");
		result.put("seller", new HashMap<String, Object>() {{ put("id",""); put("name", ""); }});
		result.put("startPrice", "");
		result.put("bidInc", "");
		result.put("bestOffer", "");
		result.put("bidder", new HashMap<String, Object>() {{ put("id",""); put("name", ""); }});
		result.put("stopDate", new HashMap<String, Object>() {{ put("display",""); put("timestamp", ""); }});
		result.put("action", "");
		return result;
	}

	private Map<String, Object> buildItemData(Item item) {
		Map<String, Object> data = getEmptyItemData();
		NumberFormat numberFormatter = new DecimalFormat("#0.00");
		try {
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(oraFactory.getContext(), Bid.class);
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);

			Map<String, Object> sellerData = new HashMap<String, Object>();
			User seller = oraUserDao.get(item.getSellerId());
			sellerData.put("id", seller.getId());
			sellerData.put("name", seller.getFullName());
						
			Bid bid = oraBidDao.getBestBidByItemId(item.getId());
			if (bid != null) {
				User bidder = oraUserDao.get(bid.getBidderId());
				if (bidder != null) {
					Map<String, Object> bidderData = new HashMap<String, Object>();
					bidderData.put("id", bidder.getId());
					bidderData.put("name", bidder.getFullName());
					data.put("bidder", bidderData);
				}			
			}
			Map<String, Object> dateData = new HashMap<String, Object>();
			Calendar cal = Calendar.getInstance();
			cal.setTime(item.getStartBidding());
			cal.add(Calendar.HOUR, item.getTimeLeft());
			
			dateData.put("display", (new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(cal.getTime())));
			dateData.put("timestamp", cal.getTime());
			
			data.put("uid", item.getId());
			data.put("title", item.getTitle());
			data.put("description", item.getDescription());				
			data.put("seller", sellerData);
			data.put("startPrice", numberFormatter.format(item.getStartPrice()));
			data.put("bidInc", (!item.isBuyItNow()) ? numberFormatter.format(item.getBidIncrement()) : "");
			data.put("bestOffer", (bid != null) ? numberFormatter.format(bid.getAmount()) : "");
			data.put("stopDate", dateData);
			if (item.isSold() || (cal.before(Calendar.getInstance()))) {
				data.put("action", "");
			} else {
				data.put("action", (item.isBuyItNow()) ? "buy" : "bid");
			}
		} catch (PersistException e) {
			e.printStackTrace(System.err);
		}
		return data;		
	}
	
	@GET
	@Path("/item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getItemData(@PathParam("id") int id) {
		Map<String, Object> data = getEmptyItemData();		
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			Item item = oraItemDao.get(id);
			if (item != null) {
				data = buildItemData(item);
			}
		} catch (PersistException e) {
			e.printStackTrace(System.err);
		}
		Genson genson = new Genson();		
		return genson.serialize(data);
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/show/all")
	@Produces(MediaType.TEXT_HTML)
	public String getAll(@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		final List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		User user = (User) req.getSession().getAttribute("User");
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			List<Item> items = oraItemDao.getAll();
			for (Item item: items) {
				if (item.isSold()) { // || (item.getSellerId().equals(user.getId()))) {
					continue;
				}
				Map<String, Object> dataItem = buildItemData(item);
				if (UserManagementService.GUEST_ROLE.equals(req.getSession().getAttribute("Role"))) {
					dataItem.put("action", "");
				} else if (item.getSellerId().equals(user.getId())) {
					dataItem.put("action", "edit");
				}
				data.add(dataItem);
			}			
			
		} catch (PersistException e) {
			
			e.printStackTrace();
		}
		Map<String, List> results = new HashMap<String, List>();
		results.put("data", data);
		Genson genson = new Genson();		
		return genson.serialize(results);		
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/show/my")
	@Produces(MediaType.TEXT_HTML)
	public String getMy(@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		final List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		User user = (User) req.getSession().getAttribute("User");
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(oraFactory.getContext(), Bid.class);
			List<Item> items = oraItemDao.getAll();
			for (Item item: items) {
				
				List<Bid> bids = oraBidDao.getBidsByItemId(item.getId());
				boolean isInclude = false;
				for (Bid bid: bids) {
					if (bid.getBidderId().equals(user.getId())) {
						isInclude = true;
						break;
					}
				}
				if ((!isInclude) && (!item.getSellerId().equals(user.getId()))) {
					continue;
				}
				Map<String, Object> dataItem = buildItemData(item);
				if (item.isSold()) {
					dataItem.put("action", "sold");
				} else {
					Calendar cal = Calendar.getInstance();
					cal.setTime(item.getStartBidding());
					cal.add(Calendar.HOUR, item.getTimeLeft());
					if (cal.before(Calendar.getInstance())) {
						dataItem.put("action", "time is up");
					} else {
						if (!isInclude) {
							dataItem.put("action", "edit");
						}
					}
				}
				data.add(dataItem);
			}			
			
		} catch (PersistException e) {
			
			e.printStackTrace();
		}
		Map<String, List> results = new HashMap<String, List>();
		results.put("data", data);
		Genson genson = new Genson();		
		return genson.serialize(results);		
	}
	
	@POST
	@Path("/bid/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bid(@PathParam("id") int id, @FormParam("bidValue") String value,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		JsonResponse jsonResp = new JsonResponse();
		HttpSession session = req.getSession();
		if (UserManagementService.GUEST_ROLE.equals(req.getSession().getAttribute("Role"))) {
			jsonResp.setStatus("EXCEPTION");
			jsonResp.setErrorMsg("Wrong role");
            return Response.ok().entity(jsonResp).build();
		}
		try {
			User bidder = (User) session.getAttribute("User");
			Bid bid = new Bid(bidder.getId(), id, Double.parseDouble(value), new Timestamp(new Date().getTime()));
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(oraFactory.getContext(), Bid.class);
			oraBidDao.save(bid);
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			Item item = oraItemDao.get(id);
			if (item.isBuyItNow()) {
				item.sale();
				oraItemDao.save(item);
			}			
			Map<String, Object> data = buildItemData(item);
			Genson genson = new Genson();
			jsonResp.setData(genson.serialize(data));			
			
		} catch (PersistException e) {
			e.printStackTrace(System.err);
		}
		return Response.ok().entity(jsonResp).build();
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/bids/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBidsByItemId(@PathParam("id") int id) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		NumberFormat numberFormatter = new DecimalFormat("#0.00");
		try {
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(oraFactory.getContext(), Bid.class);
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);
			List<Bid> bids = oraBidDao.getBidsByItemId(id);
			int count = 0;
			for (Bid bid: bids) {
				Map<String, Object> dataItem = new HashMap<String, Object>();
				dataItem.put("count", ++count);
				
				Map<String, Object> bidderData = new HashMap<String, Object>();
				User bidder = oraUserDao.get(bid.getBidderId());				
				if (bidder != null) {
					bidderData.put("id", bidder.getId());
					bidderData.put("name", bidder.getFullName());
				}
				dataItem.put("bidder", bidderData);
				dataItem.put("amount", numberFormatter.format(bid.getAmount()));
				
				Map<String, Object> dateData = new HashMap<String, Object>();
				dateData.put("display", (new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(bid.getTimestamp())));
				dateData.put("timestamp", Long.toString(bid.getTimestamp().getTime()));				
				dataItem.put("ts", dateData);
				
				data.add(dataItem);
			}
		} catch (PersistException e) {
			e.printStackTrace(System.err);
		}
		Map<String, List> results = new HashMap<String, List>();
		results.put("data", data);
		Genson genson = new Genson();
		return genson.serialize(results);
	}
	
	@GET
	@Path("/new")
	@Produces(MediaType.TEXT_HTML)
	public Viewable newItem() {
		return new Viewable("/edititem");
	}
	
	@GET
	@Path("/edit/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable editItem(@PathParam("id") int id) {
		
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			Item item = oraItemDao.get(id);
			if (item != null) {
				return new Viewable("/edititem", item);
			}
		} catch (PersistException e) {
			e.printStackTrace(System.err);
		}		
		return new Viewable("/edititem");
	}
	
	@POST
	@Path("save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveItem(String formData,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		JsonResponse jsonResp = new JsonResponse();
		HttpSession session = req.getSession();
		if (UserManagementService.GUEST_ROLE.equals(req.getSession().getAttribute("Role"))) {
			jsonResp.setStatus("EXCEPTION");
			jsonResp.setErrorMsg("Wrong role");
            return Response.ok().entity(jsonResp).build();
		}		
		Genson genson = new Genson();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = genson.deserialize(formData, Map.class);
		Map<String, Object> fieldErrors = new HashMap<String, Object>();		
		if (!checkSaveItemParams(data, fieldErrors)) {
			jsonResp.setStatus("WRONGPARAM");
			jsonResp.setFieldErrors(fieldErrors);
			return Response.ok().entity(jsonResp).build();
		}
		User seller = (User) session.getAttribute("User");
		double bidIncrement = 0;
		BooleanType buyItNow = BooleanType.N;
		if (data.get("buyItNow") != null) {
			buyItNow = BooleanType.valueOf((String) data.get("buyItNow"));
			if (buyItNow.equals(BooleanType.N)) {
				bidIncrement = Double.parseDouble((String) data.get("bidIncrement"));
			}
		} else {
			bidIncrement = Double.parseDouble((String) data.get("bidIncrement"));
		}
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			Item item = null;
			if (data.get("itemId") != null) {
				item = oraItemDao.get( Integer.parseInt((String) data.get("itemId")));
				if (item != null) {
					item.setTitle((String) data.get("title"));
					item.setDescription((String) data.get("description"));
					item.setStartPrice(Double.parseDouble((String) data.get("startPrice")));
					item.setTimeLeft(Integer.parseInt((String) data.get("timeLeft")));
					item.setStartBidding(item.getStartBidding());
					item.setBuyItNow(buyItNow);
					item.setBidIncrement(bidIncrement);
				}
			} else {
				item = new Item(seller.getId(), (String) data.get("title"),
						(String) data.get("description"),
						Double.parseDouble((String) data.get("startPrice")),
						Integer.parseInt((String) data.get("timeLeft")),
						new Timestamp(new Date().getTime()),
						buyItNow,
						bidIncrement);
			}
			oraItemDao.save(item);
		} catch (PersistException e) {
			jsonResp.setStatus("EXCEPTION");
			jsonResp.setErrorMsg(e.getMessage());
            return Response.ok().entity(jsonResp).build();
		}		
		jsonResp.setStatus("SUCCESS");
		return Response.ok().entity(jsonResp).build();
	}
	
	private boolean checkSaveItemParams(Map<String, Object> params, Map<String, Object> errors) {
		final int TIME_LEFT_MIN = 1;
		final int TIME_LEFT_MAX = 1000;
		
		for (Entry<String,Object> entry: params.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			switch (field) {
				default:
					if (value == null) {
						errors.put(field, "notEmpty");
					}
					break;
				case "startPrice":
				case "bidIncrement":
					try {
						double startPrice = Double.parseDouble((String) value);
						if (startPrice <= 0) {
							errors.put(field, "greaterThan");
						}
					} catch(NumberFormatException e) {
						errors.put(field, "numeric");
					}
					break;
				case "timeLeft":
					try {
						int timeLeft = Integer.parseInt((String) value);
						if (timeLeft < TIME_LEFT_MIN) {
							errors.put(field, "greaterThan");
						} else if (timeLeft > TIME_LEFT_MAX) {
							errors.put(field, "lessThan");
						}  
					} catch(NumberFormatException e) {
						errors.put(field, "integer");
					}
					break;
			}
		}
		return (errors.size() == 0);
	}
	
	@DELETE
	@Path("delete/{id}")
	public Response delete(@PathParam("id") int id,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		JsonResponse jsonResp = new JsonResponse();
		try {
			HttpSession session = req.getSession();
			if (UserManagementService.GUEST_ROLE.equals(req.getSession().getAttribute("Role"))) {
				return Response.seeOther(new URI("../auth/logout")).build();
			}
			User user = (User) session.getAttribute("User");			
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(oraFactory.getContext(), Item.class);
			Item item = oraItemDao.get(id);
			if (item != null) {
				if (!user.getId().equals(item.getSellerId())) {
					throw new Exception("You are not an owner");
				}
				oraItemDao.delete(item);
			}			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			jsonResp.setStatus("EXCEPTION");
			jsonResp.setErrorMsg(e.getMessage());
			return Response.ok().entity(jsonResp).build(); 
		}
		jsonResp.setStatus("SUCCESS");
		return Response.ok().entity(jsonResp).build();
	}
}