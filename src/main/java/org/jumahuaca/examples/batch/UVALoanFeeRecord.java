package org.jumahuaca.examples.batch;

import org.easybatch.core.record.GenericRecord;
import org.easybatch.core.record.Header;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeRecord extends GenericRecord<UVALoanFee>{

	public UVALoanFeeRecord(Header header, UVALoanFee payload) {
		super(header, payload);
	}

}
