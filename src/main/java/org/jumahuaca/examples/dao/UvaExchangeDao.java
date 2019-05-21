package org.jumahuaca.examples.dao;

import java.time.LocalDate;
import java.util.List;

import org.jumahuaca.examples.model.UVAExchange;

public interface UvaExchangeDao {
	
	List<UVAExchange> searchAll();
	
	UVAExchange findExchangeByDay(LocalDate day);
	
	void create(UVAExchange exchange);
	
	void update(UVAExchange exchange);
	
	void delete(UVAExchange exchange);

}
