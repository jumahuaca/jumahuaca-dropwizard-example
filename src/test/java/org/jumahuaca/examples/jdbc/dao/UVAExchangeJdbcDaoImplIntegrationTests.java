package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.extensions.JdbcIntegrationExtension;
import org.jumahuaca.util.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public class UVAExchangeJdbcDaoImplIntegrationTests {
	
	@InMemoryDatasource
	public DataSource datasource;
	
	private static final String SCHEMA_FILE_NAME = "test-schema.sql";
	private static final String MODEL_FILE_NAME = "test-model.xml";

	private static final int SIZE_SELECT_ALL = 2;

	private UvaExchangeDao dao;

	private String testDay1 = "2018-12-15";

	private String testDay2 = "2018-12-14";

	private String testDay3 = "2018-12-18";

	private String testDayError1 = "2019-12-15";

	private Double testRate1 = 30.6d;

	private Double testRate2 = 30.55d;

	private Double testRate3 = 30.72d;
	
	@RegisterExtension
	public static final JdbcIntegrationExtension extension = JdbcIntegrationExtension.builder().build(SCHEMA_FILE_NAME,MODEL_FILE_NAME);

	@BeforeEach
	public void importDataSet() throws Exception {
		dao = new UvaExchangeDaoImpl(datasource);
	}

	@IntegrationTest
	public void testFindExchangeByDayShouldWork() throws SQLException {
		LocalDate param = LocalDate.parse(testDay1);
		UVAExchange result = dao.findExchangeByDay(param);
		assertThat(result.getDate()).isEqualTo(param);
		assertThat(result.getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
	}

	@IntegrationTest
	public void testFindExchangeByDayShouldNotFind() throws SQLException {
		LocalDate param = LocalDate.parse(testDayError1);
		assertThrows(NotFoundException.class, () -> dao.findExchangeByDay(param));
	}

	@IntegrationTest
	public void testSearchAllShouldWork() throws SQLException {
		List<UVAExchange> result = dao.searchAll();
		assertThat(result.size()).isEqualTo(SIZE_SELECT_ALL);
		assertThat(result.get(0).getDate()).isEqualTo(testDay1);
		assertThat(result.get(0).getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		assertThat(result.get(1).getDate()).isEqualTo(testDay2);
		assertThat(result.get(1).getRate()).isEqualTo(BigDecimal.valueOf(testRate2));
	}

	@IntegrationTest
	public void testCreateShouldWork() throws SQLException {
		dao.create(new UVAExchange(LocalDate.parse(testDay3), BigDecimal.valueOf(testRate3)));
	}

	@IntegrationTest
	public void testCreateShouldNotWork() throws SQLException {
		assertThrows(ServerErrorException.class, () -> dao.create(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
	}

	@IntegrationTest
	public void testUpdateShouldWork() throws SQLException {
		dao.update(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
	}

	@IntegrationTest
	public void testDeleteShouldWork() throws SQLException {
		dao.delete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
	}

}
