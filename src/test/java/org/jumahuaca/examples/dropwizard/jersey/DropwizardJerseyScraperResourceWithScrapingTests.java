package org.jumahuaca.examples.dropwizard.jersey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_POST_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_ROOT_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaScrapingProcessDao;
import org.jumahuaca.examples.dao.UvaScrapingProcessDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.MonthYear;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.model.UVAScrapingProcess;
import org.jumahuaca.examples.model.UVAScrapingProcessStatus;
import org.jumahuaca.examples.resources.PathConstants;
import org.jumahuaca.examples.resources.UVAScraperResource;
import org.jumahuaca.examples.scraper.UVAScraper;
import org.jumahuaca.util.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardJerseyScraperResourceWithScrapingTests {

	private static final int ESTIMATED_TIME_TO_COMPLETE = 10000;

	private static final String PREFIX = PathConstants.RESOURCE_VERSION + UVA_SCRAPER_ROOT_PATH;

	public static UvaExchangeDao daoExchange = Mockito.mock(UvaExchangeDaoImpl.class);

	public static UvaScrapingProcessDao daoProcess = Mockito.mock(UvaScrapingProcessDaoImpl.class);

	@RegisterExtension
	public static final ResourceExtension resources = ResourceExtension.builder()
			.setTestContainerFactory(new GrizzlyWebTestContainerFactory())
			.addResource(new UVAScraperResource(daoProcess, daoExchange, new UVAScraper())).build();

	private static final Integer TEST_MONTH = 4;
	private static final Integer TEST_YEAR = 2016;
	private static final Integer TEST_DAY = 16;

	private static final String OK_RESULT = "200";

	@IntegrationTest
	public void scrapOneMonthShouldWork() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		MonthYear monthYear = new MonthYear(TEST_MONTH, TEST_YEAR);
		UVAScrapingProcess process = new UVAScrapingProcess();
		LocalDate from = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), TEST_DAY);
		process.setFromDate(from);
		process.setToDate(from.minusDays(1).plusMonths(1));
		process.setStatus(UVAScrapingProcessStatus.CREATED);
		process.setProcessDate(LocalDateTime.now());

		stubProcessCreationOk(process);
		stubProcessUpdateOk();
		stubFindUvaThrowsNotFound();
		stubCreateUvaOk();

		Future<Response> futureResponse = resources.target(PREFIX + UVA_SCRAPER_POST_PATH).request().async()
				.post(Entity.entity(monthYear, MediaType.APPLICATION_JSON));
		Response response = futureResponse.get();
		Thread.sleep(ESTIMATED_TIME_TO_COMPLETE);
		assertThat(response.readEntity(String.class)).isEqualTo(OK_RESULT);
		verifyProcessUpdateOk(process);
		verifyAllCreations(fakeSomeUVAExchanges());
		resetStubbedDao();
	}

	@IntegrationTest
	public void scrapOneMonthShouldReCreate()
			throws InterruptedException, ExecutionException, CloneNotSupportedException {
		MonthYear monthYear = new MonthYear(TEST_MONTH, TEST_YEAR);
		UVAScrapingProcess process = new UVAScrapingProcess();
		LocalDate from = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), TEST_DAY);
		process.setFromDate(from);
		process.setToDate(from.minusDays(1).plusMonths(1));
		process.setStatus(UVAScrapingProcessStatus.CREATED);
		process.setProcessDate(LocalDateTime.now());

		stubProcessCreationOk(process);
		stubProcessUpdateOk();
		stubFindUva();
		stubDeleteUvaOk();
		stubCreateUvaOk();

		Future<Response> futureResponse = resources.target(PREFIX + UVA_SCRAPER_POST_PATH).request().async()
				.post(Entity.entity(monthYear, MediaType.APPLICATION_JSON));
		Response response = futureResponse.get();
		Thread.sleep(ESTIMATED_TIME_TO_COMPLETE);
		assertThat(response.readEntity(String.class)).isEqualTo(OK_RESULT);
		verifyProcessUpdateOk(process);
		verifyAllDeletions(fakeSomeUVAExchanges());
		verifyAllCreations(fakeSomeUVAExchanges());
		resetStubbedDao();
	}

	private void stubFindUva() {
		Map<LocalDate, UVAExchange> mapToStub = fakeSomeUVAExchangesByDate();
		for (LocalDate date : mapToStub.keySet()) {
			when(daoExchange.findExchangeByDay(date)).thenReturn(mapToStub.get(date));
		}

	}

	@IntegrationTest
	public void scrapOneMonthShouldFailOnCreate() throws InterruptedException, ExecutionException {
		MonthYear monthYear = new MonthYear(TEST_MONTH, TEST_YEAR);
		UVAScrapingProcess process = new UVAScrapingProcess();
		LocalDate from = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), TEST_DAY);
		process.setFromDate(from);
		process.setToDate(from.minusDays(1).plusMonths(1));
		process.setStatus(UVAScrapingProcessStatus.RUNNING);
		process.setProcessDate(LocalDateTime.now());

		stubProcessCreationError(process);

		Future<Response> futureResponse = resources.target(PREFIX + UVA_SCRAPER_POST_PATH).request().async()
				.post(Entity.entity(monthYear, MediaType.APPLICATION_JSON));
		Response response = futureResponse.get();
		assertThat(response.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		resetStubbedDao();
	}

	private void stubProcessCreationError(UVAScrapingProcess process) {
		doThrow(ServerErrorException.class).when(daoProcess).createAndReturn(any(UVAScrapingProcess.class));
	}

	private void verifyAllCreations(List<UVAExchange> fakeSomeUVAExchanges) {
		for (UVAExchange uvaExchange : fakeSomeUVAExchanges) {
			verify(daoExchange).create(uvaExchange);
		}
	}

	private void verifyAllDeletions(List<UVAExchange> fakeSomeUVAExchanges) {
		for (UVAExchange uvaExchange : fakeSomeUVAExchanges) {
			verify(daoExchange).delete(uvaExchange);
		}
	}

	private void stubCreateUvaOk() {
		doNothing().when(daoExchange).create(any(UVAExchange.class));
	}

	private void stubDeleteUvaOk() {
		doNothing().when(daoExchange).delete(any(UVAExchange.class));
	}

	private void stubFindUvaThrowsNotFound() {
		when(daoExchange.findExchangeByDay(any(LocalDate.class))).thenThrow(NotFoundException.class);

	}

	private void verifyProcessUpdateOk(UVAScrapingProcess process) throws CloneNotSupportedException {
		verify(daoProcess).update(argThat((arg) -> arg.getStatus().equals(UVAScrapingProcessStatus.RUNNING)));
		verify(daoProcess).update(argThat((arg) -> arg.getStatus().equals(UVAScrapingProcessStatus.FINISHED)));
	}

	private void stubProcessUpdateOk() {
		doNothing().when(daoProcess).update(any(UVAScrapingProcess.class));

	}

	private void stubProcessCreationOk(UVAScrapingProcess process) {
		when(daoProcess.createAndReturn(process)).thenReturn(Integer.valueOf(1));
	}

	private void resetStubbedDao() {
		Mockito.reset(daoProcess);
		Mockito.reset(daoExchange);
	}

	private static List<UVAExchange> fakeSomeUVAExchanges() {
		List<UVAExchange> exchanges = new ArrayList<UVAExchange>();
		exchanges.add(new UVAExchange(LocalDate.parse("16/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.23)));
		exchanges.add(new UVAExchange(LocalDate.parse("15/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.61)));
		exchanges.add(new UVAExchange(LocalDate.parse("14/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.61)));
		exchanges.add(new UVAExchange(LocalDate.parse("13/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.6)));
		exchanges.add(new UVAExchange(LocalDate.parse("12/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.58)));
		exchanges.add(new UVAExchange(LocalDate.parse("11/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.57)));
		exchanges.add(new UVAExchange(LocalDate.parse("10/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.55)));
		exchanges.add(new UVAExchange(LocalDate.parse("09/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.51)));
		exchanges.add(new UVAExchange(LocalDate.parse("08/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.51)));
		exchanges.add(new UVAExchange(LocalDate.parse("07/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.51)));
		exchanges.add(new UVAExchange(LocalDate.parse("06/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.5)));
		exchanges.add(new UVAExchange(LocalDate.parse("05/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.48)));
		exchanges.add(new UVAExchange(LocalDate.parse("04/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.47)));
		exchanges.add(new UVAExchange(LocalDate.parse("03/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.46)));
		exchanges.add(new UVAExchange(LocalDate.parse("02/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.41)));
		exchanges.add(new UVAExchange(LocalDate.parse("01/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.41)));
		exchanges.add(new UVAExchange(LocalDate.parse("30/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.41)));
		exchanges.add(new UVAExchange(LocalDate.parse("29/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.4)));
		exchanges.add(new UVAExchange(LocalDate.parse("28/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.38)));
		exchanges.add(new UVAExchange(LocalDate.parse("27/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.37)));
		exchanges.add(new UVAExchange(LocalDate.parse("26/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.36)));
		exchanges.add(new UVAExchange(LocalDate.parse("25/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.32)));
		exchanges.add(new UVAExchange(LocalDate.parse("24/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.32)));
		exchanges.add(new UVAExchange(LocalDate.parse("23/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.32)));
		exchanges.add(new UVAExchange(LocalDate.parse("22/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.31)));
		exchanges.add(new UVAExchange(LocalDate.parse("21/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.29)));
		exchanges.add(new UVAExchange(LocalDate.parse("20/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.28)));
		exchanges.add(new UVAExchange(LocalDate.parse("19/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.27)));
		exchanges.add(new UVAExchange(LocalDate.parse("18/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.23)));
		exchanges.add(new UVAExchange(LocalDate.parse("17/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(14.23)));
		Collections.sort(exchanges, (a, b) -> a.getDate().compareTo(b.getDate()));
		return exchanges;
	}

	private static Map<LocalDate, UVAExchange> fakeSomeUVAExchangesByDate() {
		Map<LocalDate, UVAExchange> exchanges = new HashMap<LocalDate, UVAExchange>();
		exchanges.put(LocalDate.parse("16/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("16/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.23)));
		exchanges.put(LocalDate.parse("15/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("15/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.61)));
		exchanges.put(LocalDate.parse("14/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("14/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.61)));
		exchanges.put(LocalDate.parse("13/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("13/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.6)));
		exchanges.put(LocalDate.parse("12/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("12/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.58)));
		exchanges.put(LocalDate.parse("11/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("11/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.57)));
		exchanges.put(LocalDate.parse("10/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("10/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.55)));
		exchanges.put(LocalDate.parse("09/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("09/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.51)));
		exchanges.put(LocalDate.parse("08/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("08/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.51)));
		exchanges.put(LocalDate.parse("07/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("07/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.51)));
		exchanges.put(LocalDate.parse("06/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("06/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.5)));
		exchanges.put(LocalDate.parse("05/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("05/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.48)));
		exchanges.put(LocalDate.parse("04/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("04/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.47)));
		exchanges.put(LocalDate.parse("03/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("03/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.46)));
		exchanges.put(LocalDate.parse("02/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("02/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.41)));
		exchanges.put(LocalDate.parse("01/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("01/05/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.41)));
		exchanges.put(LocalDate.parse("30/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("30/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.41)));
		exchanges.put(LocalDate.parse("29/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("29/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.4)));
		exchanges.put(LocalDate.parse("28/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("28/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.38)));
		exchanges.put(LocalDate.parse("27/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("27/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.37)));
		exchanges.put(LocalDate.parse("26/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("26/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.36)));
		exchanges.put(LocalDate.parse("25/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("25/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.32)));
		exchanges.put(LocalDate.parse("24/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("24/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.32)));
		exchanges.put(LocalDate.parse("23/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("23/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.32)));
		exchanges.put(LocalDate.parse("22/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("22/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.31)));
		exchanges.put(LocalDate.parse("21/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("21/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.29)));
		exchanges.put(LocalDate.parse("20/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("20/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.28)));
		exchanges.put(LocalDate.parse("19/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("19/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.27)));
		exchanges.put(LocalDate.parse("18/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("18/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.23)));
		exchanges.put(LocalDate.parse("17/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), new UVAExchange(
				LocalDate.parse("17/04/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")), BigDecimal.valueOf(14.23)));
		return exchanges;
	}

}
