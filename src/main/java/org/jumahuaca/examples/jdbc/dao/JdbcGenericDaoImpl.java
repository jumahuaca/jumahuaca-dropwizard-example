package org.jumahuaca.examples.jdbc.dao;

import java.io.Serializable;

import org.jumahuaca.dao.GenericDao;

public class JdbcGenericDaoImpl <T, ID extends Serializable> implements GenericDao<T,ID>{

	@Override
	public T create(T newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T find(ID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(T toUpdate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(T toDelete) {
		// TODO Auto-generated method stub
		
	}

}
