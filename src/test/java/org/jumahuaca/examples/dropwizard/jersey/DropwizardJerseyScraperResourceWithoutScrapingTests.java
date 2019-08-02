package org.jumahuaca.examples.dropwizard.jersey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_POST_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_ROOT_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaScrapingProcessDao;
import org.jumahuaca.examples.dao.UvaScrapingProcessDaoImpl;
import org.jumahuaca.examples.model.MonthYear;
import org.jumahuaca.examples.model.UVAScrapingProcess;
import org.jumahuaca.examples.model.UVAScrapingProcessStatus;
import org.jumahuaca.examples.resources.PathConstants;
import org.jumahuaca.examples.resources.UVAScraperResource;
import org.jumahuaca.examples.scraper.UVAScraper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardJerseyScraperResourceWithoutScrapingTests {
	
private static final String PREFIX = PathConstants.RESOURCE_VERSION+UVA_SCRAPER_ROOT_PATH;

	
	public static UvaExchangeDao daoExchange = Mockito.mock(UvaExchangeDaoImpl.class);
	
	public static UvaScrapingProcessDao daoProcess = Mockito.mock(UvaScrapingProcessDaoImpl.class);
	
	public static UVAScraper scraper = Mockito.mock(UVAScraper.class);
	
	private static final Integer TEST_MONTH = 4;
	private static final Integer TEST_YEAR = 2016;
	private static final Integer TEST_DAY = 16;

	private static final String OK_RESULT = "200";
	
	private static final int ESTIMATED_TIME_TO_COMPLETE = 500;


	@RegisterExtension
	public static final ResourceExtension mockedResources = ResourceExtension.builder()
	        .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
			.addResource(new UVAScraperResource(daoProcess,daoExchange,scraper)).build();
	
	@Test			
	public void scrapOneMonthShouldFailOnScraping() throws InterruptedException, ExecutionException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		MonthYear monthYear = new MonthYear(TEST_MONTH,TEST_YEAR);
		UVAScrapingProcess process = new UVAScrapingProcess();
		LocalDate from = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), TEST_DAY);
		process.setFromDate(from);
		process.setToDate(from.minusDays(1).plusMonths(1));
		process.setStatus(UVAScrapingProcessStatus.CREATED);
		process.setProcessDate(LocalDateTime.now());
				
		mockProcessCreationOk(process);
		mockProcessUpdateOk();
		mockScrapingError(TEST_YEAR, TEST_MONTH);
		
		Future<Response> futureResponse = mockedResources.target(PREFIX+UVA_SCRAPER_POST_PATH).request().async().
				post(Entity.entity(monthYear, MediaType.APPLICATION_JSON));
		Response response = futureResponse.get();
		Thread.sleep(ESTIMATED_TIME_TO_COMPLETE);
		assertThat(response.readEntity(String.class)).isEqualTo(OK_RESULT);
		verifyProcessUpdateOk(process);
		resetMockedDao();
	}
	
	private void mockScrapingError(Integer year, Integer month) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		doThrow(IOException.class).when(scraper).scrap(year, month);		
	}

	private void mockProcessCreationOk(UVAScrapingProcess process) {
		when(daoProcess.createAndReturn(process)).thenReturn(Integer.valueOf(1));
	}
	
	private void mockProcessUpdateOk() {
		doNothing().when(daoProcess).update(any(UVAScrapingProcess.class));
	}

	private void verifyProcessUpdateOk(UVAScrapingProcess process) {
		process.setId(1);
		verify(daoProcess).update(argThat((arg) -> arg.getStatus().equals(UVAScrapingProcessStatus.RUNNING)));
		verify(daoProcess).update(argThat((arg) -> arg.getStatus().equals(UVAScrapingProcessStatus.ERROR)));
	}
	
	private void resetMockedDao() {
		Mockito.reset(daoProcess);
		Mockito.reset(daoExchange);
	}
	

	
	

}
