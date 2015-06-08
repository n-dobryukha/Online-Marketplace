package com.ndobriukha.onlinemarketplace.dao;

@SuppressWarnings("serial")
public class PersistExistsException extends PersistException {
	public PersistExistsException() { }	
	public PersistExistsException(String msg) { super(msg); }	
	public PersistExistsException(Exception e) { super(e); }
}
