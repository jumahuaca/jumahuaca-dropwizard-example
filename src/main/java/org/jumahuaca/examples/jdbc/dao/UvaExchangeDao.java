package org.jumahuaca.examples.jdbc.dao;

import java.time.LocalDate;
import java.util.List;

import org.jumahuaca.examples.jdbc.model.UVAExchange;

public interface UvaExchangeDao {
	
	List<UVAExchange> searchAll();
	
	UVAExchange findExchangeByDay(LocalDate day);
	
	void create(UVAExchange exchange);
	
	void update(UVAExchange exchange);
	
	void delete(UVAExchange exchange);

}
