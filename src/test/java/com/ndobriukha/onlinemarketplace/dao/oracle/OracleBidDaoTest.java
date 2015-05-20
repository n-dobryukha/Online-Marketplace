package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ndobriukha.onlinemarketplace.dao.DaoFactory;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.models.Bid;
import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleBidDaoTest {

	private OracleDaoFactory oraFactory;
	private DataSource dataSource;
	private QueryRunner runner;

	private List<User> users;
	private List<Item> items;

	public OracleBidDaoTest() {
	}

	@Before
	public void setUp() throws PersistException, SQLException, NamingException {
		oraFactory = new OracleDaoFactory("jdbc/marketplace");
		dataSource = oraFactory.getContext();
		runner = new QueryRunner(dataSource);
		OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		String sql = oraUserDao.getCreateQuery();
		String[][] userParams = {
				{ "User 01", "Address 01", "login01", "password01",
						"login01@example.com" },
				{ "User 02", "Address 02", "login02", "password02",
						"login02@example.com" },
				{ "User 03", "Address 03", "login03", "password03",
						"login03@example.com" }, };
		runner.batch(sql, userParams);
		users = oraUserDao.getAll();

		OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
		sql = oraItemDao.getCreateQuery();
		Timestamp ts = new Timestamp(new Date().getTime());
		Object[][] itemParams = {
				{ users.get(0).getId(), "Item 01", "Item 01", 100.00, 10, ts, "Y", 10.00, "N" },
				{ users.get(1).getId(), "Item 02", "Item 02", 200.00, 20, ts, "N", 20.00, "N" },
				{ users.get(2).getId(), "Item 03", "Item 03", 300.00, 30, ts, "N", 30.00, "N" } };
		runner.batch(sql, itemParams);
		items = oraItemDao.getAll();
	}

	@After
	public void tearDown() throws SQLException {
		runner.update("DELETE FROM Bids");
		runner.update("DELETE FROM Items");
		runner.update("DELETE FROM Users");
	}

	/**
	 * 10. Получение ставки по товару.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе ставки на
	 * определённый товар. Вызвать метод для получения всех ставок по этому
	 * товару.
	 * 
	 * Expected result: Тест считается успешным, если полученный список
	 * совпадает с данными внесёнными с помощью sql-скрипта.
	 */
	@Test
	public void testGetBidsByItemId() throws PersistException {
		try {
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(dataSource, Bid.class);
			String sql = oraBidDao.getCreateQuery();
			Integer itemId = items.get(0).getId();
			Object[][] params = { { users.get(0).getId(), itemId, 100.00 },
					{ users.get(1).getId(), itemId, 150.00 },
					{ users.get(2).getId(), itemId, 200.00 }, };
			runner.batch(sql, params);

			List<Bid> bids = oraBidDao.getBidsByItemId(itemId);
			Assert.assertEquals(params.length, bids.size());
			for (int i = 0; i < bids.size(); i++) {
				Bid bid = bids.get(i);
				Assert.assertArrayEquals(params[i], bid.getFieldsValues());
			}
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 12. Создание ставки.
	 * 
	 * Step to reproduce: Вызвать метод для создания ставки на покупку. Вызвать
	 * метод для получения всех ставок для определённого товара.
	 * 
	 * Expected result: Тест считается успешным, если в списке присутствует
	 * созданная ставка.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testCreate() throws PersistException {
		try {
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(dataSource, Bid.class);
			Bid bid = new Bid(users.get(0).getId(), items.get(0)
					.getId(), 100.00);
			oraBidDao.save(bid);
			Assert.assertNotNull("Persist object is null", bid);
			Assert.assertNotNull("After persist object ID is null", bid.getId());
			List<Bid> bids = oraBidDao.getAll();
			Assert.assertTrue(bids.contains(bid));
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 13. Поиск последней (лучшей) ставки на лот.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе ставки на
	 * определённый товар. Вызвать метод для получения всех ставок по этому
	 * товару.
	 * 
	 * Expected result: Тест считается успешным, если полученная ставка
	 * предлагает самую лучшую цену и входит в число ставок внесённых с помощью
	 * sql-скрипта.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testBestBidByItemId() throws PersistException {
		try {
			OracleBidDao oraBidDao = (OracleBidDao) oraFactory.getDao(dataSource, Bid.class);
			String sql = oraBidDao.getCreateQuery();
			Integer itemId = items.get(0).getId();
			Object[][] params = { { users.get(0).getId(), itemId, 100.00 },
					{ users.get(1).getId(), itemId, 150.00 },
					{ users.get(2).getId(), itemId, 200.00 }, };
			runner.batch(sql, params);

			Bid bestBid = oraBidDao.getBestBidByItemId(itemId);
			Assert.assertNotNull("Best bid is null", bestBid);

			List<Bid> bids = oraBidDao.getBidsByItemId(itemId);
			Assert.assertEquals(params.length, bids.size());
			for (Bid bid : bids) {
				Assert.assertTrue(bestBid.getAmount() >= bid.getAmount());
			}
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

}
