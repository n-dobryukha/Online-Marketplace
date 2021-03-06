package com.ndobriukha.onlinemarketplace.dao;


/** Фабрика объектов для работы с базой данных */
public interface DaoFactory<Context> {

    public interface DaoCreator<Context> {
        @SuppressWarnings("rawtypes")
		public GenericDao create(Context context);
    }
    
    /** Возвращает подключение к базе данных */
    public Context getContext() throws PersistException;

    /** Возвращает объект для управления персистентным состоянием объекта */
    @SuppressWarnings("rawtypes")
	public GenericDao getDao(Context context, Class dtoClass) throws PersistException;
}