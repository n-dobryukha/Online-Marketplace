package com.ndobriukha.onlinemarketplace.dao;

public class PersistExistsException extends PersistException {
	public PersistExistsException() { }	
	public PersistExistsException(String msg) { super(msg); }	
	public PersistExistsException(Exception e) { super(e); }
}
