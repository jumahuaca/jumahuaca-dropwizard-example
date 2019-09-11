package org.jumahuaca.examples.batch;

import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Record;

public class UVALoanFeeUpdateReader implements RecordReader{
	
	private Integer loanId;
	
	public UVALoanFeeUpdateReader(Integer loanId) {
		super();
		this.loanId = loanId;
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Record readRecord() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
