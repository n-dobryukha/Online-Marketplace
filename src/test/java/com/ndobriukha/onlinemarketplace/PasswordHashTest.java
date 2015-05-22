package com.ndobriukha.onlinemarketplace;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Assert;
import org.junit.Test;

public class PasswordHashTest {

	@Test
	public void testCreateHash() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		for (int i = 0; i < 10; i++) {
			String password = "" + i;
			String hash = PasswordHash.createHash(password);
			String secondHash = PasswordHash.createHash(password);
			Assert.assertNotEquals("Two hashes are equals!", hash, secondHash);
			String wrongPassword = "" + (i + 1);
			Assert.assertFalse("Wrong password accepted!",
					PasswordHash.validatePassword(wrongPassword, hash));
			Assert.assertTrue("Good password not accepted!",
					PasswordHash.validatePassword(password, hash));
		}
	}
}