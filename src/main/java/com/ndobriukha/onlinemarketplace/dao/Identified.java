package com.ndobriukha.onlinemarketplace.dao;

/**
 * Identifiable interface
 */
public interface Identified {
	/** Return Id of object */
	public Integer getId();
	
	/** Insert object Id */
	public void setId(Integer Id);
}
