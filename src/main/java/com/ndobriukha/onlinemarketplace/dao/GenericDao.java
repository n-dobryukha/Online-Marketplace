package com.ndobriukha.onlinemarketplace.dao;

import java.util.List;

public interface GenericDao<T extends Identified> {
	/** Create or update dao-object*/
    public void save(T object)  throws PersistException;

    /** Returns object by id */
    public T get(int id) throws PersistException;

    /** Returns all objects */
    public List<T> getAll() throws PersistException;
    
    /** Delete object */
    public void delete(T object) throws PersistException;
    
}
