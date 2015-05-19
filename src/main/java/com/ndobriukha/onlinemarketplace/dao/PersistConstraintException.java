package com.ndobriukha.onlinemarketplace.dao;

public class PersistConstraintException extends PersistException {
	public PersistConstraintException() { }
	
	public PersistConstraintException(String msg) { super(msg); }
	
	public PersistConstraintException(Exception e) { super(e); }
}
