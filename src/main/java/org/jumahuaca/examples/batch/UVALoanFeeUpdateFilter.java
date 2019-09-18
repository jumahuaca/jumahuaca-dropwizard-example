package org.jumahuaca.examples.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.easybatch.core.filter.RecordFilter;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateFilter implements RecordFilter<UVALoanFeeRecord> {

	private static final int SCALE = 2;

	@Override
	public UVALoanFeeRecord processRecord(UVALoanFeeRecord record) {
		UVALoanFee fee = record.getPayload();
		if (fee.getFinalCapital() != null && fee.getFinalInterest() != null && fee.getFinalTotal() != null 
				&& fee.getFinalCapital().setScale(SCALE,RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO.setScale(SCALE,RoundingMode.HALF_UP))!=0
				&& fee.getFinalInterest().setScale(SCALE,RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO.setScale(SCALE,RoundingMode.HALF_UP))!=0
				&& fee.getFinalTotal().setScale(SCALE,RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO.setScale(SCALE,RoundingMode.HALF_UP))!=0
				) {
			return null;
		}
		return record;
	}

}
