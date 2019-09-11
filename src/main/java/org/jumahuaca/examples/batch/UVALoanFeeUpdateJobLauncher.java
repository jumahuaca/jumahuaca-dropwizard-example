package org.jumahuaca.examples.batch;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;

public class UVALoanFeeUpdateJobLauncher {
	
	private static final String JOB_NAME = "UVA Loan Fee Update Job";
	
	public JobReport runJob(Integer loanId) {
		// Build a batch job
        Job job = new JobBuilder()
            .named(JOB_NAME)
            .reader(new UVALoanFeeUpdateReader(loanId))
            .filter(new UVALoanFeeUpdateFilter())
            .processor(new UVALoanFeeUpdateProcessor())
            .writer(new UVALoanFeeUpdateWriter())
            .build();

        // Execute the job
        JobExecutor jobExecutor = new JobExecutor();
        JobReport report = jobExecutor.execute(job);
        jobExecutor.shutdown();
        return report;

	}

}
