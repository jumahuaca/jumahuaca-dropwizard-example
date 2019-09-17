package org.jumahuaca.examples.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.easybatch.core.processor.RecordProcessor;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateProcessor implements RecordProcessor<UVALoanFeeRecord, UVALoanFeeRecord>{
	
	private static final int ROUNDING_DECIMALS = 2;

	private static final int DECIMALS = 6;
	
	private UvaExchangeDao exchangeDao;

	public UVALoanFeeUpdateProcessor(UvaExchangeDao exchangeDao) {
		this.exchangeDao = exchangeDao;
	}

	@Override
	public UVALoanFeeRecord processRecord(UVALoanFeeRecord record) throws Exception {
		UVALoanFee item = record.getPayload();
		try {
			LocalDate initialLoanDate = item.getLoanDate();
			LocalDate feeDate = item.getFeeDate();
			UVAExchange initialUva = exchangeDao.findExchangeByDay(initialLoanDate);
			UVAExchange finalUva = exchangeDao.findExchangeByDay(feeDate);
			BigDecimal capital = updateValue(item.getInitialCapital(),initialUva,finalUva);
			BigDecimal interest = updateValue(item.getInitialInterest(),initialUva,finalUva);
			item.setFinalCapital(capital.setScale(ROUNDING_DECIMALS,RoundingMode.HALF_UP));
			item.setFinalInterest(interest.setScale(ROUNDING_DECIMALS,RoundingMode.HALF_UP));
			item.setFinalTotal(capital.add(interest).setScale(ROUNDING_DECIMALS,RoundingMode.HALF_UP));
		} catch (Exception e) {
			return null;
		}
		return record;
	}

	private BigDecimal updateValue(BigDecimal value, UVAExchange initialUva, UVAExchange finalUva) {
		return value.divide(initialUva.getRate(),DECIMALS,RoundingMode.HALF_UP).multiply(finalUva.getRate());
	}
}
