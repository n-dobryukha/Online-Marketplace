package com.ndobriukha.onlinemarketplace.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			PoolDataSource  pds = PoolDataSourceFactory.getPoolDataSource();
			pds.setURL("jdbc:oracle:thin:@//localhost:1521/XE");
	        pds.setUser("MARKETPLACE");
	        pds.setPassword("marketplace");
			pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
			Connection conn = pds.getConnection();
			response.getWriter().println(conn);
			conn.close();
	        conn=null;
		} catch (SQLException e) {
			response.getWriter().println(e);
		}
		/*try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/marketplace");
			response.getWriter().println(ds);
			Connection conn = ds.getConnection();
			response.getWriter().println(conn);
//			conn.close();
//	        conn=null;
		} catch (NamingException | SQLException e) {
			response.getWriter().println(e);
		}*/
        
	}

}
