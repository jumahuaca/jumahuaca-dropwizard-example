package org.jumahuaca.examples.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.easybatch.core.job.JobReport;
import org.easybatch.core.job.JobStatus;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateJobLauncher;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.dao.UvaLoanFeeDaoImpl;
import org.jumahuaca.examples.jdbc.dao.InMemoryDatasource;
import org.jumahuaca.examples.model.UVALoanFee;
import org.jumahuaca.examples.model.UVALoanFeeId;
import org.jumahuaca.extensions.JdbcIntegrationExtension;
import org.jumahuaca.util.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public class UVALoanFeeUpdateEasyBatchJobIntegrationTests {


	private static final int LOAN_DATE_YEAR = 2017;

	private static final int LOAN_DATE_MONTH = 9;

	private static final int LOAN_DATE_DAY = 20;

	private static final Integer FEE_NUMBER_1 = 1;

	private static final Integer LOAN_ID = 1;

	private static final int FEE_1_YEAR = 2017;

	private static final int FEE_1_MONTH = 11;

	private static final int FEE_1_DAY = 10;

	private static final BigDecimal FEE_1_INITIAL_CAPITAL = BigDecimal.valueOf(3265.78);

	private static final BigDecimal FEE_1_INITIAL_INTEREST = BigDecimal.valueOf(5539.86);

	private static final BigDecimal FEE_1_INITIAL_TOTAL = BigDecimal.valueOf(8805.64);

	private static final BigDecimal FEE_1_FINAL_CAPITAL = BigDecimal.valueOf(3356.95);

	private static final BigDecimal FEE_1_FINAL_TOTAL = BigDecimal.valueOf(9051.46);

	private static final BigDecimal FEE_1_FINAL_INTEREST = BigDecimal.valueOf(5694.51);

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
	
	@InMemoryDatasource
	public DataSource datasource;
	
	private UvaExchangeDao exchangeDao;
	private UvaLoanFeeDao feeDao;
	private UVALoanFeeUpdateJobLauncher launcher;
	
	private static final String SCHEMA_FILE_NAME = "test-schema-batch.sql";
	private static final String MODEL_FILE_NAME = "test-model-batch.xml";
	
	@RegisterExtension
	public static final JdbcIntegrationExtension extension = JdbcIntegrationExtension.builder().build(SCHEMA_FILE_NAME,MODEL_FILE_NAME);

	@BeforeEach
	public void importDataSet() throws Exception {
		exchangeDao = new UvaExchangeDaoImpl(datasource);
		feeDao = new UvaLoanFeeDaoImpl(datasource);
		launcher = new UVALoanFeeUpdateJobLauncher(exchangeDao, feeDao);
	}
	
	@IntegrationTest
	public void testLaunchJobShouldWork() {
		JobReport report = launcher.runJob(LOAN_ID);
		assertThat(report.getStatus()).isEqualTo(JobStatus.COMPLETED);		
	}
	
	@IntegrationTest
	public void testLaunchJobShouldUpdateOk() {
		JobReport report = launcher.runJob(LOAN_ID);
		assertThat(report.getStatus()).isEqualTo(JobStatus.COMPLETED);
		List<UVALoanFee> fees = feeDao.findByLoanId(LOAN_ID);
		List<UVALoanFee> toCompare = buildToBeWrittenFakeResult();
		assertThat(fees).isEqualTo(toCompare);
		
	}
	
	public List<UVALoanFee> buildToBeWrittenFakeResult() {
		UVALoanFeeId id1 = new UVALoanFeeId();
		id1.setFeeNumber(FEE_NUMBER_1);
		id1.setLoanId(LOAN_ID);
		UVALoanFee fee1 = new UVALoanFee();
		fee1.setId(id1);
		fee1.setFeeDate(LocalDate.of(FEE_1_YEAR, FEE_1_MONTH, FEE_1_DAY));
		fee1.setInitialCapital(FEE_1_INITIAL_CAPITAL);
		fee1.setInitialInterest(FEE_1_INITIAL_INTEREST);
		fee1.setInitialTotal(FEE_1_INITIAL_TOTAL);
		fee1.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		fee1.setFinalCapital(FEE_1_FINAL_CAPITAL);
		fee1.setFinalInterest(FEE_1_FINAL_INTEREST);
		fee1.setFinalTotal(FEE_1_FINAL_TOTAL);
		fee1.setLoanId(LOAN_ID);
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
		fee2.setFinalCapital(FEE_2_FINAL_CAPITAL);
		fee2.setFinalInterest(FEE_2_FINAL_INTEREST);
		fee2.setFinalTotal(FEE_2_FINAL_TOTAL);
		fee2.setLoanId(LOAN_ID);
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
		fee3.setLoanDate(LocalDate.of(LOAN_DATE_YEAR, LOAN_DATE_MONTH, LOAN_DATE_DAY));
		fee3.setLoanId(LOAN_ID);
		List<UVALoanFee> result = new ArrayList<UVALoanFee>();
		result.add(fee1);
		result.add(fee2);
		result.add(fee3);
		return result;
	}

}
