package com.ndobriukha.onlinemarketplace.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.dbutils.QueryRunner;

import com.ndobriukha.onlinemarketplace.dao.oracle.OracleDaoFactory;

public class MyHttpSessionListener implements HttpSessionListener {
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println(new Timestamp((new Date()).getTime()) + ": Session " + se.getSession().getId() + " created");				
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		System.out.println(new Timestamp((new Date()).getTime()) + ": Session " + session.getId() + " destroyed");
		if (session.getAttribute("IP") != null) {
			try {
				OracleDaoFactory oraFactory = new OracleDaoFactory("java:/comp/env/jdbc/marketplace");
				QueryRunner runner = new QueryRunner(oraFactory.getContext());
				String sql = "UPDATE SESSIONS SET END_TS = SYSDATE WHERE ID = ?";
				runner.update(sql, session.getId());
			} catch (NamingException | SQLException e) {
				e.printStackTrace(System.err);
			}
		}
	}

}
