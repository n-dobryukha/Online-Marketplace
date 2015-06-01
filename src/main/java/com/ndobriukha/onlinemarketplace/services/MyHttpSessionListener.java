package com.ndobriukha.onlinemarketplace.services;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.dbutils.QueryRunner;

import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;

public class MyHttpSessionListener implements HttpSessionListener {
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		session.setMaxInactiveInterval(15*60);
		try {
			session.setAttribute("daoFactory", new OracleDaoFactory(
					"java:/comp/env/jdbc/marketplace"));
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		try {
			QueryRunner runner = new QueryRunner( ((OracleDaoFactory) session.getAttribute("daoFactory")).getContext());
			String sql = "UPDATE SESSIONS SET END_TS = SYSDATE WHERE ID = ?";
			runner.update(sql, session.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

}
