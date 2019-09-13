package org.jumahuaca.examples.batch;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;

public class UVALoanFeeUpdateJobLauncher {
	
	private static final String JOB_NAME = "UVA Loan Fee Update Job";
	
	private UvaExchangeDao exchangeDao;
	
	private UvaLoanFeeDao feeDao;
	
	public UVALoanFeeUpdateJobLauncher(UvaExchangeDao exchangeDao, UvaLoanFeeDao feeDao) {
		super();
		this.exchangeDao = exchangeDao;
		this.feeDao = feeDao;
	}

	public JobReport runJob(Integer loanId) {
        Job job = new JobBuilder()
            .named(JOB_NAME)
            .reader(new UVALoanFeeUpdateReader(loanId,feeDao))
            .filter(new UVALoanFeeUpdateFilter())
            .processor(new UVALoanFeeUpdateProcessor(exchangeDao))
            .writer(new UVALoanFeeUpdateWriter(feeDao))
            .build();

        JobExecutor jobExecutor = new JobExecutor();
        JobReport report = jobExecutor.execute(job);
        jobExecutor.shutdown();
        return report;

	}

}
