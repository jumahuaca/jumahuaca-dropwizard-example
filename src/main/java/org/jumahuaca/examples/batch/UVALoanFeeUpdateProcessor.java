package org.jumahuaca.examples.batch;

import org.easybatch.core.processor.RecordProcessor;
import org.jumahuaca.examples.dao.UvaExchangeDao;

public class UVALoanFeeUpdateProcessor implements RecordProcessor<UVALoanFeeRecord, UVALoanFeeRecord>{
	
	private UvaExchangeDao exchangeDao;

	public UVALoanFeeUpdateProcessor(UvaExchangeDao exchangeDao) {
		this.exchangeDao = exchangeDao;
	}

	@Override
	public UVALoanFeeRecord processRecord(UVALoanFeeRecord record) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
