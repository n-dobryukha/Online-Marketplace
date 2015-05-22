package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ndobriukha.onlinemarketplace.PasswordHashTest;

@RunWith(Suite.class)
@SuiteClasses({ PasswordHashTest.class, OracleUserDaoTest.class, OracleItemDaoTest.class,
		OracleBidDaoTest.class })
public class AllTests {
	@BeforeClass
	public static void setUp() throws NamingException, SQLException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		InitialContext ic = new InitialContext(env);				

		PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
		pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		pds.setURL("jdbc:oracle:thin:@//localhost:1521/XE");
		pds.setUser("MARKETPLACE");
		pds.setPassword("marketplace");
		pds.setInitialPoolSize(5);
		pds.setMinPoolSize(1);
		pds.setMaxPoolSize(5);
		pds.setInactiveConnectionTimeout(0);
		pds.setSQLForValidateConnection("SELECT 1 FROM DUAL");

		ic.unbind("java:/comp/env/jdbc/marketplace");
		ic.bind("java:/comp/env/jdbc/marketplace", pds);
	}
}
