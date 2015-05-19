package com.ndobriukha.onlinemarketplace.dao;

import java.sql.SQLException;

public class PersistException extends SQLException {

	public PersistException() { }
	
	public PersistException(String msg) { super(msg); }
	
	public PersistException(Exception e) { super(e); }

}
