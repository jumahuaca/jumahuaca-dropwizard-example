package org.jumahuaca.dao;

import java.io.Serializable;

public interface GenericDao <T, ID extends Serializable>{
	
	T create(T newInstance);
	
	T find(ID id);
	
	void update(T toUpdate);
	
	void delete(T toDelete);

}
