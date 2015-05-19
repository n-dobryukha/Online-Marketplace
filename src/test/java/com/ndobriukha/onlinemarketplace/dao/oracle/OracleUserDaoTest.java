package com.ndobriukha.onlinemarketplace.dao.oracle;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ndobriukha.onlinemarketplace.dao.PersistConstraintException;
import com.ndobriukha.onlinemarketplace.dao.PersistException;
import com.ndobriukha.onlinemarketplace.dao.PersistExistsException;
import com.ndobriukha.onlinemarketplace.models.User;

public class OracleUserDaoTest {

	private OracleDaoFactory oraFactory;
	private DataSource dataSource;
	private QueryRunner runner;

	public OracleUserDaoTest() {
	}

	@Before
	public void setUp() throws PersistException, SQLException, NamingException {
		oraFactory = new OracleDaoFactory("jdbc/marketplace");
		dataSource = oraFactory.getContext();
		runner = new QueryRunner(dataSource);		
	}

	@After
	public void tearDown() throws SQLException {
		runner.update("DELETE FROM Users");
	}

	/**
	 * 1.	Получение всех пользователей.
	 * 
	 * Step to reproduce: С помощью sql-скриптов создать в базе несколько
	 * пользователей. Вызвать метод для получения всех существующих
	 * пользователей.
	 * 
	 * Expected result: Тест считается успешным, если пользователи, добавленные
	 * в базу с помощью sql-скриптов, совпадают с пользователями, которые вернет
	 * вызванный метод.
	 * 
	 * @throws PersistException
	 * @throws NamingException 
	 */
	@Test
	public void testBatchInsert() throws PersistException, NamingException {
		try {
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
			String sql = oraUserDao.getCreateQuery();
			String[][] params = { { "A", "A", "A", "A", "A" },
					{ "B", "B", "B", "B", "B" }, { "C", "C", "C", "C", "C" } };
			int[] rows = runner.batch(sql, params);
			Assert.assertEquals(params.length, rows.length);
			List<User> users = oraUserDao.getAll();
			Assert.assertEquals(rows.length, users.size());
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				Assert.assertArrayEquals(params[i], user.getFieldsValues());
			}
		} catch (SQLException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 2.	Создание нового пользователя.
	 * 
	 * Step to reproduce: Вызвать метод для создания пользователя с определённым
	 * логином и паролем. Вызвать метод для получения всех существующих
	 * пользователей.
	 * 
	 * Expected result: Тест считается успешным, если в списке присутствует
	 * новый пользователь.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testCreate() throws PersistException {
		try {
			OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
			User user = new User("Full Name", "Address", "login",
					"password", "email");
			oraUserDao.save(user);
			Assert.assertNotNull("After persist object ID is null",
					user.getId());
			List<User> users = oraUserDao.getAll();
			Assert.assertEquals("More than one created User.", 1, users.size());
			Assert.assertEquals(user, users.get(0));
		} catch (PersistException e) {
			System.err.println(e);
			throw new PersistException(e);
		}
	}

	/**
	 * 2.1	Создание нового пользователя.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе пользователя с
	 * логином «login». Вызвать метод для создания пользователя с логином
	 * «login».
	 * 
	 * Expected result: Тест считается успешным, если возникает соответствующее
	 * исключение.
	 * 
	 * @throws SQLException
	 * @throws PersistException
	 */
	@Test(expected = PersistConstraintException.class)
	public void testDuplicateexception() throws SQLException, PersistException {
		/*OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		String sql = oraUserDao.getCreateQuery();
		String login = "login";
		int count = runner.update(sql, "Full Name", "Address",
				login, "password", "email");
		User user = oraUserDao.create("Full Name", "Address", login,
				"password", "email");
		Assert.assertNotNull("Persist object is null", user);
		Assert.assertNotNull("After persist object ID is null", user.getId());*/
	}

	/**
	 * 3.	Получение пользователя по его логину.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе пользователя с
	 * логином «login». Вызвать метод для получения пользователя с логином
	 * «login».
	 * 
	 * Expected result: Тест считается успешным, если данные полученного
	 * пользователя совпадают с данными внесёнными помощью sql-скрипта.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testGetUserByLogin() throws PersistException {
		/*OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		String login = "login";
		User createdUser = oraUserDao.create("Full Name", "Address", login,
				"password", "email");
		Assert.assertNotNull("Persist object is null", createdUser);
		Assert.assertNotNull("After persist object ID is null",
				createdUser.getId());
		User receivedUser = oraUserDao.getUserByLogin(login);
		Assert.assertEquals(createdUser, receivedUser);*/
	}

	/**
	 * 3.1	Получение пользователя по его логину.
	 * 
	 * Step to reproduce: Вызвать метод для получения пользователя с
	 * несуществующим логином.
	 * 
	 * Expected result: Тест считается успешным, если возникает соответствующее
	 * исключение.
	 * 
	 * @throws PersistException
	 */
	@Test(expected = PersistExistsException.class)
	public void testGetNotExistsUserByLogin() throws PersistException {
		/*OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		String login = "login";
		User gettedUser = oraUserDao.getUserByLogin(login);*/
	}

	/**
	 * 4.	Изменение данных пользователя.
	 * 
	 * Step to reproduce: С помощью sql-скрипта создать в базе пользователя с
	 * логином «login». Вызвать метод для получения пользователя с логином
	 * «login». Изменить все данные пользователя.
	 * 
	 * Expected result: Тест считается успешным, если изменённые данные
	 * совпадают со значениями соответствующей строки в таблице.
	 * 
	 * @throws PersistException
	 */
	@Test
	public void testUpdateUser() throws PersistException {
		/*OracleUserDao oraUserDao = (OracleUserDao) oraFactory.getDao(dataSource, User.class);
		User createdUser = oraUserDao.create("Full Name", "Address", "login",
				"password", "email");
		Assert.assertNotNull("Persist object is null", createdUser);
		Assert.assertNotNull("After persist object ID is null",
				createdUser.getId());
		User receivedUser = oraUserDao.getUserByLogin(createdUser.getLogin());
		Assert.assertEquals(createdUser, receivedUser);
		receivedUser.setFullName("New Full Name");
		receivedUser.setBillingAddress("New Address");
		receivedUser.setLogin("New login");
		receivedUser.setPassword("New password");
		receivedUser.setEmail("New email");
		oraUserDao.update(receivedUser);
		User receivedUpdatesUser = oraUserDao.getByPK(receivedUser.getId());
		Assert.assertNotNull("Updates object is null", receivedUpdatesUser);
		Assert.assertEquals(receivedUser, receivedUpdatesUser);*/
	}
}
