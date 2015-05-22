package com.ndobriukha.onlinemarketplace.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;

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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			OracleDaoFactory oraFactory = new OracleDaoFactory("java:/comp/env/jdbc/marketplace");
	        Connection conn = oraFactory.getContext().getConnection();
	        System.out.println(conn);
	        response.getWriter().println(conn);
			conn.close();
	        conn=null;
	        
		} catch (NamingException | SQLException e) {
			response.getWriter().println(e);
		}
	    
        
	}

}
