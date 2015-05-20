package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ndobriukha.onlinemarketplace.dao.DaoFactory;
import com.ndobriukha.onlinemarketplace.dao.GenericDao;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.models.Bid;
import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleDaoFactory implements DaoFactory<DataSource> {

	private DataSource dataSource;
	
	@SuppressWarnings("rawtypes")
	private Map<Class, DaoCreator<DataSource>> creators;
    
    @SuppressWarnings("rawtypes")
	public OracleDaoFactory(String context) throws NamingException {
    	Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup("java:/comp/env");
		dataSource = (DataSource) envContext.lookup(context);
		
		creators = new HashMap<Class, DaoCreator<DataSource>>();
        creators.put(User.class, new DaoCreator<DataSource>() {
            @Override
            public GenericDao create(DataSource dataSource) {
                return new OracleUserDao(dataSource);
            }
        });
        creators.put(Item.class, new DaoCreator<DataSource>() {
            @Override
            public GenericDao create(DataSource dataSource) {
                return new OracleItemDao(dataSource);
            }
        });
        creators.put(Bid.class, new DaoCreator<DataSource>() {
            @Override
            public GenericDao create(DataSource dataSource) {
                return new OracleBidDao(dataSource);
            }
        });
	}
            
	@Override
	public DataSource getContext() throws PersistException {
		return dataSource;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public GenericDao getDao(DataSource dataSource, Class dtoClass)
			throws PersistException {
		DaoCreator creator = creators.get(dtoClass);
        if (creator == null) {
            throw new PersistException("Dao object for " + dtoClass + " not found.");
        }
        return creator.create(dataSource);
	}

}
