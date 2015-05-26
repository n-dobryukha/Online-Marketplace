package com.ndobriukha.onlinemarketplace.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;
import com.ndobriukha.onlinemarketplace.dao.oracle.OracleUserDao;
import com.ndobriukha.onlinemarketplace.models.User;

@Path("/auth")
public class UserManagementService {

	// This method is called if TEXT_PLAIN is request
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("login") String login,
			@FormParam("password") String password,
			@Context HttpServletRequest req) throws URISyntaxException, IOException {
		JsonResponse json = new JsonResponse();
		try {
			
			
			ServletContext ctx = req.getServletContext();			
			OracleDaoFactory oraFactory = new OracleDaoFactory(
					"java:/comp/env/jdbc/marketplace");
			ctx.log(oraFactory.toString());
			Connection conn = oraFactory.getContext().getConnection();
			ctx.log(conn.toString());
			conn.close();
			conn = null;
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(oraFactory.getContext(), User.class);
			ctx.log(oraUserDao.toString());
			try {
				User user = oraUserDao.getUserByLogin(login);				
			} catch(PersistExistsException e) {
				json.setStatus("NOTEXISTS");
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