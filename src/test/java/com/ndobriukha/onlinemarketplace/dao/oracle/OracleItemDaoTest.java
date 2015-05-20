package com.ndobriukha.onlinemarketplace.dao.oracle;

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

import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.models.Item;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleItemDaoTest {

	private OracleDaoFactory oraFactory;
	private DataSource dataSource;
	private QueryRunner runner;

	private User user = new User("Full Name", "Address", "login", "password",
			"email@example.com");

	public OracleItemDaoTest() {
	}

	@Before
	public void setUp() throws PersistException, SQLException, NamingException {
		oraFactory = new OracleDaoFactory("jdbc/marketplace");
		dataSource = oraFactory.getContext();
		runner = new QueryRunner(dataSource);
		OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		oraUserDao.save(user);;
	}

	@After
	public void tearDown() throws SQLException {
		runner.update("DELETE FROM Items");
		runner.update("DELETE FROM Users");
	}

	/**
	 * 5.	Получение всех товаров.
	 * 
	 * Step to reproduce: С помощью sql-скриптов создать в базе несколько
	 * товаров. Вызвать метод для получения всех существующих товаров.
	 * 
	 * Expected result: Тест считается успешным, если товары, добавленные в базу
	 * с помощью sql-скриптов, совпадают с товарами, которые вернет вызванный
	 * метод.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testBatchInsert() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			String sql = oraItemDao.getCreateQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			Object[][] params = {
					{ user.getId(), "Item A", "Item A", 100.00, 10, ts, "Y", 10.00, "N" },
					{ user.getId(), "Item B", "Item B", 200.00, 20, ts, "N", 20.00, "N" },
					{ user.getId(), "Item C", "Item C", 300.00, 30, ts, "N", 30.00, "N" } };
			int[] rows = runner.batch(sql, params);
			Assert.assertEquals(params.length, rows.length);

			List<Item> items = oraItemDao.getAll();
			Assert.assertEquals(rows.length, items.size());
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				Assert.assertArrayEquals(params[i], item.getFieldsValues());
			}
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 6.	Поиск товаров по подстроке названия.
	 * 
	 * Step to reproduce: С помощью sql-скриптов создать в базе несколько
	 * товаров. Вызвать метод для получения товаров по подстроке названия.
	 * 
	 * Expected result: Тест считается успешным, если полученный список товаров
	 * удовлетворяет условию и входит в число товаров добавленных в базу с
	 * помощью sql-скриптов.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetItemsBySubstrDescr() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			String sql = oraItemDao.getCreateQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			Object[][] params = {
					{ user.getId(), "Item A", "Item A", 100.00, 10, ts, "Y", 10.00, "N" },
					{ user.getId(), "Item B", "Item B", 200.00, 20, ts, "N", 20.00, "N" },
					{ user.getId(), "Item C", "Item C", 300.00, 30, ts, "N", 30.00, "N" } };
			int[] rows = runner.batch(sql, params);
			Assert.assertEquals(params.length, rows.length);
			List<Item> items = oraItemDao.getItemsBySubstrDescr("Item");
			Assert.assertEquals(params.length, items.size());
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 7.	Поиск товара по указанному UID.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе товаров с
	 * определённым UID. Вызвать метод для получения товара по этому UID.
	 * 
	 * Expected result: Тест считается успешным, если данные полученного товара
	 * совпадают с данными внесёнными с помощью sql-скрипта. созданный товар.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testGetByPK() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			Item createdItem = new Item(user.getId(), "Test item title", "Test item description",
					100.00, 10, new Timestamp(new Date().getTime()),
					Item.BooleanType.Y, 10.00);
			oraItemDao.save(createdItem);
			Assert.assertNotNull("Persist object is null", createdItem);
			Assert.assertNotNull("After persist object ID is null",
					createdItem.getId());
			Item receivedItem = oraItemDao.get(createdItem.getId());
			Assert.assertNotNull("Received object is null", receivedItem);
			Assert.assertEquals(createdItem, receivedItem);
		} catch (PersistException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 8.	Поиск товара по продавцу.
	 * 
	 * Step to reproduce: С помощью sql-скриптов создать в базе несколько
	 * товаров. Вызвать метод для получения товаров по подстроке продавцу.
	 * 
	 * Expected result: Тест считается успешным, если полученный список товаров
	 * удовлетворяет условию и входит в число товаров добавленных в базу с
	 * помощью sql-скриптов.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetItemsBySeller() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			String sql = oraItemDao.getCreateQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			Object[][] params = {
					{ user.getId(), "Item A", "Item A", 100.00, 10, ts, "Y", 10.00, "N" },
					{ user.getId(), "Item B", "Item B", 200.00, 20, ts, "N", 20.00, "N" },
					{ user.getId(), "Item C", "Item C", 300.00, 30, ts, "N", 30.00, "N" } };
			int[] rows = runner.batch(sql, params);
			Assert.assertEquals(params.length, rows.length);
			List<Item> items = oraItemDao.getItemsBySellerId(user.getId());
			Assert.assertEquals(params.length, items.size());
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 9.	Создание нового товара.
	 * 
	 * Step to reproduce: Вызвать метод для создания товара с определённым
	 * характеристиками. Вызвать метод для получения всех существующих товаров.
	 * 
	 * Expected result: Тест считается успешным, если в списке присутствует
	 * созданный товар.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testCreate() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			Item item = new Item(user.getId(), "Test item title", "Test item description",
					100.00, 10, new Timestamp(new Date().getTime()),
					Item.BooleanType.Y, 10.00);
			oraItemDao.save(item);
			Assert.assertNotNull("Persist object is null", item);
			Assert.assertNotNull("After persist object ID is null",
					item.getId());
			List<Item> items = oraItemDao.getAll();
			Assert.assertTrue(
					"Received items list doesn't contain created item",
					items.contains(item));
		} catch (PersistException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 11. Продажа товара.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе товар с
	 * определённым характеристиками. Вызвать метод для получения товара по
	 * указанному UID. Вызвать метод продажи товара.
	 * 
	 * Expected result: Тест считается успешным, если изменённые данные
	 * совпадают со значениями соответствующей строки в таблице.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testSaleItem() throws PersistException {
		try {
			OracleItemDao oraItemDao = (OracleItemDao) oraFactory.getDao(dataSource, Item.class);
			Item createdItem = new Item(user.getId(), "Test item title", "Test item description",
					100.00, 10, new Timestamp(new Date().getTime()),
					Item.BooleanType.Y, 10.00);
			oraItemDao.save(createdItem);
			Assert.assertNotNull("Persist object is null", createdItem);
			Assert.assertNotNull("After persist object ID is null",
					createdItem.getId());
			Item receivedItem = oraItemDao.get(createdItem.getId());
			Assert.assertNotNull("Received object is null", receivedItem);
			Assert.assertEquals(createdItem, receivedItem);
			receivedItem.sale();
			oraItemDao.save(receivedItem);
			Item receivedItem2 = oraItemDao.get(createdItem.getId());
			Assert.assertNotNull("Received object is null", receivedItem2);
			Assert.assertEquals(receivedItem, receivedItem2);
		} catch (Exception e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}
}
