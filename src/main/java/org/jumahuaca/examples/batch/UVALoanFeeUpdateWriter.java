package org.jumahuaca.examples.batch;

import org.easybatch.core.record.Batch;
import org.easybatch.core.writer.RecordWriter;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;

public class UVALoanFeeUpdateWriter implements RecordWriter{
	
	private UvaLoanFeeDao feeDao;

	public UVALoanFeeUpdateWriter(UvaLoanFeeDao feeDao) {
		this.feeDao = feeDao;
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRecords(Batch batch) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
