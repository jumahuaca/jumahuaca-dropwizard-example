package org.jumahuaca.examples.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateReader implements RecordReader{
	
	private static final Logger LOGGER = Logger.getLogger(UVALoanFeeUpdateReader.class.getName());
	
	private Integer loanId;
	
	private UvaLoanFeeDao feeDao;
	
	private List<UVALoanFee> items;

	
	public UVALoanFeeUpdateReader(Integer loanId, UvaLoanFeeDao feeDao) {
		super();
		this.loanId = loanId;
		this.feeDao = feeDao;
	}

	@Override
	public void open() throws Exception {
		LOGGER.info("UVALoanFeeUpdateReader opened");
		items = new ArrayList<UVALoanFee>();
		
	}

	@Override
	public Record readRecord() throws Exception {
		items = feeDao.findByLoanId(loanId);
		for (UVALoanFee uvaLoanFee : items) {
			
		}
		Header header = new Header(Long.valueOf(items.size()), "UvaLoanFeeDao", new Date());
        return new UVALoanFeeRecord(header, recipe);
		
	}

	@Override
	public void close() throws Exception {
		LOGGER.info("UVALoanFeeUpdateReader closed");
		
	}

}
