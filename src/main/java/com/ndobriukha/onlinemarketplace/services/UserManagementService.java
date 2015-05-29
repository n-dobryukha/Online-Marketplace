package com.ndobriukha.onlinemarketplace.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.dbutils.QueryRunner;

import com.ndobriukha.onlinemarketplace.PasswordHash;
import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleUserDao;
import com.ndobriukha.onlinemarketplace.models.User;

@Path("/")
public class UserManagementService {

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
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip) && !"0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
	        	return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}
	
	// This method is called if TEXT_PLAIN is request
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("login") String login,
			@FormParam("password") String password,
			@Context HttpServletRequest req,
			@Context HttpServletResponse res) throws URISyntaxException, IOException {
		JsonResponse json = new JsonResponse();
		try {
			OracleDaoFactory oraFactory = new OracleDaoFactory(
					"java:/comp/env/jdbc/marketplace");
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
					HttpSession session = req.getSession(true);
					session.setAttribute("User", user);
					session.setAttribute("IP", ip);
					runner.update(sql, session.getId(), user.getId(), ip);
					//res.sendRedirect("../../../WEB-INF/showitems.jsp");
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
		} catch (NamingException | SQLException e) {
			
			e.printStackTrace();
		}
		json.setStatus("SUCCESS");
		return Response.ok().entity(json).build();		
	}

}