package org.jumahuaca.examples.resources;

import static org.jumahuaca.examples.resources.PathConstants.RESOURCE_VERSION;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_POST_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_SCRAPER_ROOT_PATH;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaScrapingProcessDao;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.model.MonthYear;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.model.UVAScrapingProcess;
import org.jumahuaca.examples.model.UVAScrapingProcessStatus;
import org.jumahuaca.examples.scraper.UVAScraper;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

@Path(RESOURCE_VERSION + UVA_SCRAPER_ROOT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UVAScraperResource {
	
	private static final Logger LOGGER = Logger.getLogger(UVAScraperResource.class.toString());

	private final int FROM_PERIOD_DAY = 16;

	private UvaScrapingProcessDao processDao;

	private UvaExchangeDao uvaDao;
	
	private UVAScraper scraper;


	public UVAScraperResource(UvaScrapingProcessDao processDao, UvaExchangeDao uvaDao, UVAScraper scraper) {
		super();
		this.processDao = processDao;
		this.uvaDao = uvaDao;
		this.scraper = scraper;
	}

	@POST
	@Path(UVA_SCRAPER_POST_PATH)
	public void scrapMonth(MonthYear monthYear, @Suspended final AsyncResponse asyncResponse) {
		UVAScrapingProcess process = new UVAScrapingProcess();
		LocalDate from = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), FROM_PERIOD_DAY);
		process.setFromDate(from);
		process.setToDate(from.minusDays(1).plusMonths(1));
		process.setStatus(UVAScrapingProcessStatus.CREATED);
		process.setProcessDate(LocalDateTime.now());
		final Integer id;
		try {
			id = processDao.createAndReturn(process);
		} catch (Exception e) {
			asyncResponse.resume(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());			
			return;
		}

		asyncResponse.register(new CompletionCallback() {
			@Override
			public void onComplete(Throwable throwable) {
				process.setId(id);
				if (throwable == null) {
					process.setStatus(UVAScrapingProcessStatus.RUNNING);
				} else {
					process.setStatus(UVAScrapingProcessStatus.ERROR);
				}
				processDao.update(process);
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<UVAExchange> result = null;
				try {
					result = scraper.scrap(monthYear.getYear(), monthYear.getMonth());
				} catch (FailingHttpStatusCodeException | IOException e1) {
					UVAScrapingProcess cloned;
					try {
						cloned = (UVAScrapingProcess) process.clone();
						cloned.setStatus(UVAScrapingProcessStatus.ERROR);
						processDao.update(cloned);	
						return;
					} catch (CloneNotSupportedException e) {
						LOGGER.severe("Error cloning");
						return;
					}
				}
				for (UVAExchange uvaExchange : result) {
					try {
						UVAExchange exchange = uvaDao.findExchangeByDay(uvaExchange.getDate());
						uvaDao.delete(exchange);
						uvaDao.create(uvaExchange);
					} catch (NotFoundException e) {
						uvaDao.create(uvaExchange);
					}
				}
				UVAScrapingProcess cloned;
				try {
					cloned = (UVAScrapingProcess) process.clone();
					cloned.setStatus(UVAScrapingProcessStatus.FINISHED);
					processDao.update(cloned);				
				} catch (CloneNotSupportedException e) {
					LOGGER.severe("Error cloning");
					return;
				}
			}
		}).start();
		asyncResponse.resume("200");
		return;

	}

}
