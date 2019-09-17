package org.jumahuaca.examples.batch;

import org.easybatch.core.filter.RecordFilter;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateFilter implements RecordFilter<UVALoanFeeRecord> {

	@Override
	public UVALoanFeeRecord processRecord(UVALoanFeeRecord record) {
		UVALoanFee fee = record.getPayload();
		if (fee.getFinalCapital() != null && fee.getFinalInterest() != null && fee.getFinalTotal() != null) {
			return record;
		}
		return null;
	}

}
