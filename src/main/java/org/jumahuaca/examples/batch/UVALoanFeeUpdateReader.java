package org.jumahuaca.examples.batch;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Header;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateReader implements RecordReader {

	private static final Logger LOGGER = Logger.getLogger(UVALoanFeeUpdateReader.class.getName());

	private Integer loanId;

	private UvaLoanFeeDao feeDao;

	private List<UVALoanFee> items;

	private int size;

	public UVALoanFeeUpdateReader(Integer loanId, UvaLoanFeeDao feeDao) {
		super();
		this.loanId = loanId;
		this.feeDao = feeDao;
	}

	@Override
	public void open() throws Exception {
		LOGGER.info("UVALoanFeeUpdateReader opened");
		items = feeDao.findByLoanId(loanId.intValue());
		size = items.size();

	}

	@Override
	public UVALoanFeeRecord readRecord() throws Exception {
		if (!items.isEmpty()) {
			UVALoanFee fee = items.remove(0);
			Header header = new Header(Long.valueOf(size), "UvaLoanFeeDao", new Date());
			return new UVALoanFeeRecord(header, fee);
		}
		return null;

	}

	@Override
	public void close() throws Exception {
		LOGGER.info("UVALoanFeeUpdateReader closed");

	}

}
