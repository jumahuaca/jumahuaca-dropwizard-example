package org.jumahuaca.examples.dao;

import java.util.List;

import org.jumahuaca.examples.model.UVALoanFee;

public interface UvaLoanFeeDao {
	
	List<UVALoanFee> findByLoanId(Integer loanId);
	
	void update(UVALoanFee fee);

 
}
