package com.ndobriukha.onlinemarketplace.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.dbutils.QueryRunner;

import com.ndobriukha.onlinemarketplace.PasswordHash;
import com.ndobriukha.onlinemarketplace.dao.PersistConstraintException;
import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleUserDao;
import com.ndobriukha.onlinemarketplace.models.User;
import com.owlike.genson.Genson;

@Path("/")
public class UserManagementService {
	
	public static String USER_ROLE = "USER";
	public static String GUEST_ROLE = "GUEST";
	
	private OracleDaoFactory oraFactory;
	
	
	public UserManagementService() {
		try {
			oraFactory = new OracleDaoFactory("java:/comp/env/jdbc/marketplace");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}

	private static final String[] HEADERS_TO_TRY = { 
	    "X-Forwarded-For",
	    "Proxy-Client-IP",
	    "WL-Proxy-Client-IP",
	    "HTTP_X_FORWARDED_FOR",
	    "HTTP_X_FORWARDED",
	    "HTTP_X_CLUSTER_CLIENT_IP",
	    "HTTP_CLIENT_IP",
	    "HTTP_FORWARDED_FOR",
	    "HTTP_FORWARDED",
	    "HTTP_VIA",
	    "REMOTE_ADDR" };

	private static String getClientIpAddress(HttpServletRequest request) {
	    for (String header : HEADERS_TO_TRY) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	        	return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}
	
	@POST
	@Path("registration")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registration(String formData,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) {
		JsonResponse jsonResp = new JsonResponse();
		Genson genson = new Genson();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = genson.deserialize(formData, Map.class);
		Map<String, Object> fieldErrors = new HashMap<String, Object>();		
		if (!checkRegistrationParams(data, fieldErrors)) {
			jsonResp.setStatus("WRONGPARAM");
			jsonResp.setFieldErrors(fieldErrors);
		}
		User user = new User((String) data.get("fullName"), (String) data.get("billingAddress"), (String) data.get("login"),
				(String) data.get("password"), (String) data.get("email"));
		HttpSession session = req.getSession(true);
		try {
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);
			oraUserDao.save(user);
			
			QueryRunner runner = new QueryRunner(oraFactory.getContext());
			String sql = "INSERT INTO SESSIONS (ID, USER_ID, IP_ADDRESS) VALUES(?,?,?)";
			String ip = getClientIpAddress(req);
			
			session.setAttribute("User", user);
			session.setAttribute("IP", ip);
			session.setAttribute("Role", USER_ROLE);
			runner.update(sql, session.getId(), user.getId(), ip);			
		} catch (PersistConstraintException e) {
			jsonResp.setStatus("EXISTSLOGIN");
			jsonResp.setErrorMsg("Login already exists.");
			return Response.ok().entity(jsonResp).build();
		} catch (SQLException e) {
			jsonResp.setStatus("EXCEPTION");
			jsonResp.setErrorMsg(e.getMessage());
            return Response.ok().entity(jsonResp).build();
		}
		jsonResp.setStatus("SUCCESS");
		return Response.ok().entity(jsonResp).build();
	}
	
	private boolean checkRegistrationParams(Map<String, Object> params, Map<String, Object> errors) {
		for (Entry<String,Object> entry: params.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();			
			switch (field) {
				default:
					if (value == null) {
						errors.put(field, "notEmpty");
					}
					break;
				case "login":
					if (value.toString().length() < 6) {
						errors.put(field, "stringLength");
					}
					if (!checkLoginRegexp((String) value)) {
						errors.put(field, "regexp");
					}
					if (value.equals(params.get("password"))) {
						errors.put(field, "different");
						errors.put("password", "different");
					}
					if (value.equals(params.get("confirnPassword"))) {
						errors.put(field, "different");
						errors.put("confirmPassword", "different");
					}
					break;
				case "password":
					if (value.toString().length() < 6) {
						errors.put(field, "stringLength");
					}
					if (value.equals(params.get("login"))) {
						errors.put(field, "different");
						errors.put("login", "different");
					}
					if (!value.equals(params.get("confirmPassword"))) {
						errors.put(field, "identical");
						errors.put("confirmPassword", "identical");
					}
					break;
				case "confirmPassword":
					if (value.toString().length() < 6) {
						errors.put(field, "stringLength");
					}
					if (value.equals(params.get("login"))) {
						errors.put(field, "different");
						errors.put("login", "different");
					}
					if (!value.equals(params.get("password"))) {
						errors.put(field, "identical");
						errors.put("password", "identical");
					}
					break;
			}
		}
		return (errors.size() == 0);
	}
	
	private boolean checkLoginRegexp(String loginString) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9_.]+$");
		Matcher m = p.matcher(loginString);
		return m.matches();
	}
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("login") String login,
			@FormParam("password") String password,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) throws URISyntaxException, IOException {
		JsonResponse json = new JsonResponse();
		HttpSession session = req.getSession(true);
		try {
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);
			try {
				User user = oraUserDao.getUserByLogin(login);
				if (!PasswordHash.validatePassword(password, user.getPassword())) {
					json.setStatus("WRONGPASSWORD");
					json.setErrorMsg("Wrong password");
	                return Response.ok().entity(json).build();
				}
				else {
					QueryRunner runner = new QueryRunner(oraFactory.getContext());
					String sql = "INSERT INTO SESSIONS (ID, USER_ID, IP_ADDRESS) VALUES(?,?,?)";
					String ip = getClientIpAddress(req);
					session.setAttribute("User", user);
					session.setAttribute("Role", USER_ROLE);
					session.setAttribute("IP", ip);
					runner.update(sql, session.getId(), user.getId(), ip);
				}
			} catch(PersistExistsException e) {
				json.setStatus("NOTEXISTS");
				json.setErrorMsg(e.getMessage());
                return Response.ok().entity(json).build();
			} catch (Exception e) {
				json.setStatus("EXCEPTION");
				json.setErrorMsg(e.getMessage());
                return Response.ok().entity(json).build();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		json.setStatus("SUCCESS");
		return Response.ok().entity(json).build();		
	}
	
	@GET
	@Path("guest")
	public Response guest(@Context HttpServletRequest req,
			@Context HttpServletResponse res) throws IOException {
		HttpSession session = req.getSession();
		session.invalidate();
		session = req.getSession(true);
		try {
			QueryRunner runner = new QueryRunner(oraFactory.getContext());
			String sql = "INSERT INTO SESSIONS (ID, USER_ID, IP_ADDRESS) VALUES(?,?,?)";
			String ip = getClientIpAddress(req);		
			session.setAttribute("User", new User("Guest", null, null, "", null));
			session.setAttribute("Role", GUEST_ROLE);
			session.setAttribute("IP", ip);
			runner.update(sql, session.getId(), null, ip);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		res.sendRedirect(req.getContextPath() + "/items/show/all");
		return Response.ok().build();
	}
	
	@GET
	@Path("logout")
	public Response logout(@Context HttpServletRequest req,
			@Context HttpServletResponse res) throws NamingException, SQLException, IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		res.sendRedirect(req.getContextPath() + "/login.jsp");
		return Response.ok().build();
	}
}