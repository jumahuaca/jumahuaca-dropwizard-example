package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.easybatch.core.job.JobReport;
import org.easybatch.core.job.JobStatus;
import org.jumahuaca.examples.batch.UVALoanFeeRecord;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateFilter;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateProcessor;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateReader;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateWriter;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.dao.UvaLoanFeeDaoImpl;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.model.UVALoan;
import org.jumahuaca.examples.model.UVALoanFee;
import org.jumahuaca.examples.model.UVALoanFeeId;
import org.jumahuaca.extensions.EasyBatchExtension;
import org.jumahuaca.extensions.TestDoubleBatchHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

public class UVALoanFeeUpdateEasyBatchJobTests implements TestDoubleBatchHelper{
	
	private static final Long COHOLDER_DNI = 31111111L;

	private static final Long HOLDER_DNI = 21111111L;

	private static final int LOAN_DATE_YEAR = 2017;

	private static final int LOAN_DATE_MONTH = 9;

	private static final int LOAN_DATE_DAY = 20;

	private static final BigDecimal PESOS_VALUE = BigDecimal.valueOf(1132800.0);

	private static final BigDecimal UVA_VALUE = BigDecimal.valueOf(56470.59);

	private static final Integer FEE_NUMBER_1 = 1;

	private static final Integer LOAN_ID = 1;

	private static final int FEE_1_YEAR = 2017;

	private static final int FEE_1_MONTH = 11;

	private static final int FEE_1_DAY = 10;

	private static final BigDecimal FEE_1_INITIAL_CAPITAL = BigDecimal.valueOf(3265.78);

	private static final BigDecimal FEE_1_INITIAL_INTEREST = BigDecimal.valueOf(5539.86);

	private static final BigDecimal FEE_1_INITIAL_TOTAL = BigDecimal.valueOf(8805.64);

	private static final BigDecimal FEE_1_FINAL_CAPITAL = BigDecimal.valueOf(3356.948335);

	private static final BigDecimal FEE_1_FINAL_TOTAL = BigDecimal.valueOf(5694.512124);

	private static final BigDecimal FEE_1_FINAL_INTEREST = BigDecimal.valueOf(9051.460459);

	private static final BigDecimal FEE_2_FINAL_CAPITAL = BigDecimal.valueOf(3417.36);

	private static final BigDecimal FEE_2_FINAL_INTEREST = BigDecimal.valueOf(3437.35);

	private static final BigDecimal FEE_2_FINAL_TOTAL = BigDecimal.valueOf(6854.71);

	private static final BigDecimal FEE_3_FINAL_CAPITAL = BigDecimal.valueOf(3481.36);

	private static final BigDecimal FEE_3_FINAL_INTEREST = BigDecimal.valueOf(3481.43);

	private static final BigDecimal FEE_3_FINAL_TOTAL = BigDecimal.valueOf(6962.79);

	private static final Integer FEE_NUMBER_2 = 2;

	private static final int FEE_2_YEAR = 2017;

	private static final int FEE_2_MONTH = 12;

	private static final int FEE_2_DAY = 11;

	private static final BigDecimal FEE_2_INITIAL_CAPITAL = BigDecimal.valueOf(3275.31);

	private static final BigDecimal FEE_2_INITIAL_INTEREST = BigDecimal.valueOf(3294.47);

	private static final BigDecimal FEE_2_INITIAL_TOTAL = BigDecimal.valueOf(6569.78);

	private static final Integer FEE_NUMBER_3 = 3;

	private static final int FEE_3_YEAR = 2018;

	private static final int FEE_3_MONTH = 1;

	private static final int FEE_3_DAY = 10;

	private static final BigDecimal FEE_3_INITIAL_CAPITAL = BigDecimal.valueOf(3284.86);

	private static final BigDecimal FEE_3_INITIAL_INTEREST = BigDecimal.valueOf(3284.92);

	private static final BigDecimal FEE_3_INITIAL_TOTAL = BigDecimal.valueOf(6569.78);

	private static final int FEE_0_EXCHANGE_YEAR = 2017;

	private static final int FEE_0_EXCHANGE_MONTH = 9;

	private static final int FEE_0_EXCHANGE_DAY = 20;

	private static final BigDecimal FEE_0_EXCHANGE_RATE = BigDecimal.valueOf(20.06);

	private static final int FEE_1_EXCHANGE_YEAR = 2017;

	private static final int FEE_1_EXCHANGE_MONTH = 11;

	private static final int FEE_1_EXCHANGE_DAY = 10;

	private static final BigDecimal FEE_1_EXCHANGE_RATE = BigDecimal.valueOf(20.62);

	private static final int FEE_2_EXCHANGE_YEAR = 2017;

	private static final int FEE_2_EXCHANGE_MONTH = 12;

	private static final int FEE_2_EXCHANGE_DAY = 11;

	private static final BigDecimal FEE_2_EXCHANGE_RATE = BigDecimal.valueOf(20.93);

	private static final int FEE_3_EXCHANGE_YEAR = 2018;

	private static final int FEE_3_EXCHANGE_MONTH = 01;

	private static final int FEE_3_EXCHANGE_DAY = 10;

	private static final BigDecimal FEE_3_EXCHANGE_RATE = BigDecimal.valueOf(21.26);
	
	@RegisterExtension
	public final EasyBatchExtension<UVALoanFeeRecord, UVALoanFeeRecord> extension = new EasyBatchExtension<UVALoanFeeRecord, UVALoanFeeRecord>();

	private UvaLoanFeeDao feeRepository = (UvaLoanFeeDao) Mockito.mock(UvaLoanFeeDaoImpl.class);

	private UvaExchangeDao uvaExchangeRepository = (UvaExchangeDao) Mockito.mock(UvaExchangeDaoImpl.class);

	private UVALoanFeeUpdateReader reader;

	private UVALoanFeeUpdateProcessor processor;
	
	private UVALoanFeeUpdateFilter filter;

	private UVALoanFeeUpdateWriter writer;

	@BeforeEach
	public void setup() {
		reader = new UVALoanFeeUpdateReader(LOAN_ID,feeRepository);
		processor = new UVALoanFeeUpdateProcessor(uvaExchangeRepository);
		writer = new UVALoanFeeUpdateWriter(feeRepository);
		filter = new UVALoanFeeUpdateFilter();
	}

	@Test
	public void testJobShouldCompleteOk()
			throws Exception {

		JobReport report = extension.launchTestingBatch(reader, filter, processor, writer, this);
		assertThat(report.getStatus()).isEqualTo(JobStatus.COMPLETED);
	}
	
	@Test
	public void testJobShouldUpdateOk()
			throws Exception {

		JobReport report = extension.launchTestingBatch(reader, filter, processor, writer, this);
		assertThat(report.getStatus()).isEqualTo(JobStatus.COMPLETED);
		List<UVALoanFee> toCompare = buildToBeWrittenFakeResult();
		verify(feeRepository).update(argThat((arg) -> arg.equals(toCompare.get(0))));
		verify(feeRepository).update(argThat((arg) -> arg.equals(toCompare.get(1))));
	}

	@Override
	public void mockInjectionsReadOk() {
		UVALoan loan = new UVALoan();
		loan.setCoholderDNI(COHOLDER_DNI);
		loan.setHolderDNI(HOLDER_DNI);
		loan.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		loan.setPesosValue(PESOS_VALUE);
		loan.setUvaValue(UVA_VALUE);
		UVALoanFeeId id1 = new UVALoanFeeId();
		id1.setFeeNumber(FEE_NUMBER_1);
		id1.setLoanId(LOAN_ID);
		UVALoanFee fee1 = new UVALoanFee();
		fee1.setId(id1);
		fee1.setFeeDate(LocalDate.of(FEE_1_YEAR, FEE_1_MONTH, FEE_1_DAY));
		fee1.setInitialCapital(FEE_1_INITIAL_CAPITAL);
		fee1.setInitialInterest(FEE_1_INITIAL_INTEREST);
		fee1.setInitialTotal(FEE_1_INITIAL_TOTAL);
		fee1.setFinalCapital(FEE_1_FINAL_CAPITAL);
		fee1.setFinalInterest(FEE_1_FINAL_INTEREST);
		fee1.setFinalTotal(FEE_1_FINAL_TOTAL);
		fee1.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		fee1.setLoanId(loan.getId());
		UVALoanFeeId id2 = new UVALoanFeeId();
		id2.setFeeNumber(FEE_NUMBER_2);
		id2.setLoanId(LOAN_ID);
		UVALoanFee fee2 = new UVALoanFee();
		fee2.setId(id2);
		fee2.setFeeDate(LocalDate.of(FEE_2_YEAR, FEE_2_MONTH, FEE_2_DAY));
		fee2.setInitialCapital(FEE_2_INITIAL_CAPITAL);
		fee2.setInitialInterest(FEE_2_INITIAL_INTEREST);
		fee2.setInitialTotal(FEE_2_INITIAL_TOTAL);
		fee2.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		fee2.setLoanId(loan.getId());

		UVALoanFeeId id3 = new UVALoanFeeId();
		id3.setFeeNumber(FEE_NUMBER_3);
		id3.setLoanId(LOAN_ID);
		UVALoanFee fee3 = new UVALoanFee();
		fee3.setId(id3);
		fee3.setFeeDate(LocalDate.of(FEE_3_YEAR, FEE_3_MONTH, FEE_3_DAY));
		fee3.setInitialCapital(FEE_3_INITIAL_CAPITAL);
		fee3.setInitialInterest(FEE_3_INITIAL_INTEREST);
		fee3.setInitialTotal(FEE_3_INITIAL_TOTAL);
		fee3.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		fee3.setLoanId(loan.getId());

		List<UVALoanFee> toReturn = new ArrayList<UVALoanFee>();
		toReturn.add(fee1);
		toReturn.add(fee2);
		toReturn.add(fee3);
		when(feeRepository.findByLoanId(LOAN_ID)).thenReturn(toReturn);
	}

	@Override
	public void mockInjectionsProcessOk() {
		UVAExchange exchange0 = new UVAExchange(
				LocalDate.of(FEE_0_EXCHANGE_YEAR, FEE_0_EXCHANGE_MONTH, FEE_0_EXCHANGE_DAY), FEE_0_EXCHANGE_RATE);
		UVAExchange exchange1 = new UVAExchange(
				LocalDate.of(FEE_1_EXCHANGE_YEAR, FEE_1_EXCHANGE_MONTH, FEE_1_EXCHANGE_DAY), FEE_1_EXCHANGE_RATE);
		UVAExchange exchange2 = new UVAExchange(
				LocalDate.of(FEE_2_EXCHANGE_YEAR, FEE_2_EXCHANGE_MONTH, FEE_2_EXCHANGE_DAY), FEE_2_EXCHANGE_RATE);
		UVAExchange exchange3 = new UVAExchange(
				LocalDate.of(FEE_3_EXCHANGE_YEAR, FEE_3_EXCHANGE_MONTH, FEE_3_EXCHANGE_DAY), FEE_3_EXCHANGE_RATE);

		when(uvaExchangeRepository
				.findExchangeByDay(LocalDate.of(FEE_0_EXCHANGE_YEAR, FEE_0_EXCHANGE_MONTH, FEE_0_EXCHANGE_DAY)))
						.thenReturn(exchange0);
		when(uvaExchangeRepository
				.findExchangeByDay(LocalDate.of(FEE_1_EXCHANGE_YEAR, FEE_1_EXCHANGE_MONTH, FEE_1_EXCHANGE_DAY)))
						.thenReturn(exchange1);
		when(uvaExchangeRepository
				.findExchangeByDay(LocalDate.of(FEE_2_EXCHANGE_YEAR, FEE_2_EXCHANGE_MONTH, FEE_2_EXCHANGE_DAY)))
						.thenReturn(exchange2);
		when(uvaExchangeRepository
				.findExchangeByDay(LocalDate.of(FEE_3_EXCHANGE_YEAR, FEE_3_EXCHANGE_MONTH, FEE_3_EXCHANGE_DAY)))
						.thenReturn(exchange3);
	}

	public List<UVALoanFee> buildToBeWrittenFakeResult() {
		UVALoan loan = new UVALoan();
		loan.setCoholderDNI(COHOLDER_DNI);
		loan.setHolderDNI(HOLDER_DNI);
		loan.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		loan.setPesosValue(PESOS_VALUE);
		loan.setUvaValue(UVA_VALUE);
		UVALoanFeeId id2 = new UVALoanFeeId();
		id2.setFeeNumber(FEE_NUMBER_2);
		id2.setLoanId(LOAN_ID);
		UVALoanFee fee2 = new UVALoanFee();
		fee2.setId(id2);
		fee2.setFeeDate(LocalDate.of(FEE_2_YEAR, FEE_2_MONTH, FEE_2_DAY));
		fee2.setInitialCapital(FEE_2_INITIAL_CAPITAL);
		fee2.setInitialInterest(FEE_2_INITIAL_INTEREST);
		fee2.setInitialTotal(FEE_2_INITIAL_TOTAL);
		fee2.setFinalCapital(FEE_2_FINAL_CAPITAL);
		fee2.setFinalInterest(FEE_2_FINAL_INTEREST);
		fee2.setFinalTotal(FEE_2_FINAL_TOTAL);
		fee2.setLoanId(loan.getId());
		UVALoanFeeId id3 = new UVALoanFeeId();
		id3.setFeeNumber(FEE_NUMBER_3);
		id3.setLoanId(LOAN_ID);
		UVALoanFee fee3 = new UVALoanFee();
		fee3.setId(id3);
		fee3.setFeeDate(LocalDate.of(FEE_3_YEAR, FEE_3_MONTH, FEE_3_DAY));
		fee3.setInitialCapital(FEE_3_INITIAL_CAPITAL);
		fee3.setInitialInterest(FEE_3_INITIAL_INTEREST);
		fee3.setInitialTotal(FEE_3_INITIAL_TOTAL);
		fee3.setFinalCapital(FEE_3_FINAL_CAPITAL);
		fee3.setFinalInterest(FEE_3_FINAL_INTEREST);
		fee3.setFinalTotal(FEE_3_FINAL_TOTAL);
		fee3.setLoanId(loan.getId());
		List<UVALoanFee> result = new ArrayList<UVALoanFee>();
		result.add(fee2);
		result.add(fee3);
		return result;
	}

	@Override
	public void mockInjectionsWriteOk() {
		return;
	}


	

}
