package org.jumahuaca.examples.dao;

import org.jumahuaca.examples.model.UVAScrapingProcess;

public interface UvaScrapingProcessDao {
	
	Integer createAndReturn(UVAScrapingProcess process);
	
	void update(UVAScrapingProcess process);

}
