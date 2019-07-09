package org.jumahuaca.examples.scraper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.scraper.UVAScraper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class UVAScraperTests {

	private UVAScraper scrapper = new UVAScraper();
	
	private static List<UVAExchange> fakedUvas;
	
	@BeforeAll
	public static void setup() {
		fakedUvas = fakeSomeUVAExchanges();
	}

	@ParameterizedTest
	@ValueSource(strings = { "2019-05", "2016-04" })
	public void testScrapperShouldWork(String dayMonth) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Integer year = Integer.valueOf(dayMonth.split("-")[0]);
		Integer month = Integer.valueOf(dayMonth.split("-")[1]);

		List<UVAExchange> result = scrapper.scrap(year, month);
		List<UVAExchange> faked = filterExchanges(fakedUvas,year,month);
		assertThat(result).isEqualTo(faked);
	}
	
	private static List<UVAExchange> filterExchanges(List<UVAExchange> fakedExchanges, Integer year, Integer month){
		return fakedExchanges.stream().filter(u->{
			LocalDate date = u.getDate();
			LocalDate startDate = LocalDate.of(year, month, 15);
			LocalDate endDate = LocalDate.of(year, month+1, 16);
			return date.isAfter(startDate) && date.isBefore(endDate);
		}).collect(Collectors.toList());
	}

	private static List<UVAExchange> fakeSomeUVAExchanges() {
		List<UVAExchange> exchanges = new ArrayList<UVAExchange>();
		exchanges.add(new UVAExchange(LocalDate.parse("16/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.3)));
		exchanges.add(new UVAExchange(LocalDate.parse("17/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.34)));
		exchanges.add(new UVAExchange(LocalDate.parse("18/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.38)));
		exchanges.add(new UVAExchange(LocalDate.parse("19/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.38)));
		exchanges.add(new UVAExchange(LocalDate.parse("20/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.38)));
		exchanges.add(new UVAExchange(LocalDate.parse("21/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.5)));
		exchanges.add(new UVAExchange(LocalDate.parse("22/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.54)));
		exchanges.add(new UVAExchange(LocalDate.parse("23/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.58)));
		exchanges.add(new UVAExchange(LocalDate.parse("24/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.62)));
		exchanges.add(new UVAExchange(LocalDate.parse("25/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.65)));
		exchanges.add(new UVAExchange(LocalDate.parse("26/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.65)));
		exchanges.add(new UVAExchange(LocalDate.parse("27/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.65)));
		exchanges.add(new UVAExchange(LocalDate.parse("28/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.77)));
		exchanges.add(new UVAExchange(LocalDate.parse("29/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.81)));
		exchanges.add(new UVAExchange(LocalDate.parse("30/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.85)));
		exchanges.add(new UVAExchange(LocalDate.parse("31/05/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.89)));
		exchanges.add(new UVAExchange(LocalDate.parse("01/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.93)));
		exchanges.add(new UVAExchange(LocalDate.parse("02/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.93)));
		exchanges.add(new UVAExchange(LocalDate.parse("03/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(36.93)));
		exchanges.add(new UVAExchange(LocalDate.parse("04/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.05)));
		exchanges.add(new UVAExchange(LocalDate.parse("05/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.09)));
		exchanges.add(new UVAExchange(LocalDate.parse("06/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.13)));
		exchanges.add(new UVAExchange(LocalDate.parse("07/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.17)));
		exchanges.add(new UVAExchange(LocalDate.parse("08/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.21)));
		exchanges.add(new UVAExchange(LocalDate.parse("09/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.21)));
		exchanges.add(new UVAExchange(LocalDate.parse("10/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.21)));
		exchanges.add(new UVAExchange(LocalDate.parse("11/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.33)));
		exchanges.add(new UVAExchange(LocalDate.parse("12/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.37)));
		exchanges.add(new UVAExchange(LocalDate.parse("13/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.41)));
		exchanges.add(new UVAExchange(LocalDate.parse("14/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.45)));
		exchanges.add(new UVAExchange(LocalDate.parse("15/06/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				BigDecimal.valueOf(37.49)));
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
		Collections.sort(exchanges, (a,b)-> a.getDate().compareTo(b.getDate()));
		return exchanges;
	}

}
