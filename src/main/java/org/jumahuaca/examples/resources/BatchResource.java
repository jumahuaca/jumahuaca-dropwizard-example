package org.jumahuaca.examples.resources;

import static org.jumahuaca.examples.resources.PathConstants.BATCH_ROOT_PATH;
import static org.jumahuaca.examples.resources.PathConstants.RESOURCE_VERSION;
import static org.jumahuaca.examples.resources.PathConstants.UVA_UPDATE_FEES_POST_PATH;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.easybatch.core.job.JobReport;
import org.easybatch.core.job.JobStatus;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateJobLauncher;

@Path(RESOURCE_VERSION+BATCH_ROOT_PATH)
@Produces(MediaType.TEXT_PLAIN)
public class BatchResource {
	
	private UVALoanFeeUpdateJobLauncher launcher;
	
	public BatchResource(UVALoanFeeUpdateJobLauncher launcher) {
		super();
		this.launcher = launcher;
	}

	@POST
	@Path(UVA_UPDATE_FEES_POST_PATH)
	public Response updateFeed(Integer loanId) {
		try {
			JobReport report = launcher.runJob(loanId);
			if(report.getStatus().equals(JobStatus.COMPLETED)) {
				return Response.ok().build();				
			}
			return Response.serverError().entity("Batch did not complete").build();			
		} catch(Exception e) {
			return Response.serverError().entity("Error running batch.").build();			
		}
	}

}
