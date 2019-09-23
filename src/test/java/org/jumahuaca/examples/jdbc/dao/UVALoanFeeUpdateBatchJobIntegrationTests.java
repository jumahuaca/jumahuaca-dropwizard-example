package org.jumahuaca.examples.jdbc.dao;

import javax.sql.DataSource;

import org.easybatch.core.job.JobReport;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateJobLauncher;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.dao.UvaLoanFeeDaoImpl;
import org.jumahuaca.extensions.JdbcIntegrationExtension;
import org.jumahuaca.util.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public class UVALoanFeeUpdateBatchJobIntegrationTests {
	
	private static final Integer LOAN_ID = 1;
	
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
	}
	
	//@IntegrationTest
	public void testLaunchJobShouldWork() {
		JobReport report = launcher.runJob(LOAN_ID);
		//report.getStatus()
		
	}

}
