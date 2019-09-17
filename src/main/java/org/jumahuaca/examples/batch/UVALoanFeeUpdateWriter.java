package org.jumahuaca.examples.batch;

import java.util.logging.Logger;

import org.easybatch.core.record.Batch;
import org.easybatch.core.record.Record;
import org.easybatch.core.writer.RecordWriter;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.model.UVALoanFee;

public class UVALoanFeeUpdateWriter implements RecordWriter {

	private static final Logger LOGGER = Logger.getLogger(UVALoanFeeUpdateWriter.class.getName());

	private UvaLoanFeeDao feeDao;

	public UVALoanFeeUpdateWriter(UvaLoanFeeDao feeDao) {
		this.feeDao = feeDao;
	}

	@Override
	public void open() throws Exception {
		LOGGER.info("UVALoanFeeUpdateWriter opened");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeRecords(Batch batch) throws Exception {
		for (Record<UVALoanFee> record : batch) {
			feeDao.update(record.getPayload());
		}

	}

	@Override
	public void close() throws Exception {
		LOGGER.info("UVALoanFeeUpdateWriter closed");

	}

}
