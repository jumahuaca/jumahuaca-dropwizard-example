package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.jdbc.model.UVAExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JdbcExchangeDaoImplIntegrationTests {

	private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
	private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private static final String USER = "sa";
	private static final String PASSWORD = "";

	private static final String SCHEMA_FILE_NAME = "test-schema.sql";
	private static final String MODEL_FILE_NAME = "test-model.xml";

	private static final int SIZE_SELECT_ALL = 2;

	private UvaExchangeDao dao;

	private DaoInvocation<UVAExchange, LocalDate> daoInvocation;

	private String testDay1 = "2018-12-15";

	private String testDay2 = "2018-12-14";

	private String testDay3 = "2018-12-18";

	private String testDayError1 = "2019-12-15";

	private Double testRate1 = 30.6d;

	private Double testRate2 = 30.55d;

	private Double testRate3 = 30.72d;

	@BeforeAll
	public static void createSchema() throws Exception {
		RunScript.execute(JDBC_URL, USER, PASSWORD, SCHEMA_FILE_NAME, null, false);
	}

	@BeforeEach
	public void importDataSet() throws Exception {
		IDataSet dataSet = readDataSet();
		cleanlyInsert(dataSet);
		dao = new UvaExchangeDaoImpl(buildDataSource());
		daoInvocation = new JdbcExchangeDaoInvocation(dao);
	}

	private DataSource buildDataSource() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(JDBC_URL);
		dataSource.setUser(USER);
		dataSource.setPassword(PASSWORD);
		return dataSource;
	}

	private IDataSet readDataSet() throws Exception {
		return new FlatXmlDataSetBuilder().build(new File(MODEL_FILE_NAME));
	}

	private void cleanlyInsert(IDataSet dataSet) throws Exception {
		IDatabaseTester databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
		databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		databaseTester.setDataSet(dataSet);
		databaseTester.onSetup();
	}

	@Test
	@Tag("integration")
	public void testFindExchangeByDayShouldWork() throws SQLException {
		LocalDate param = LocalDate.parse(testDay1);
		UVAExchange result = daoInvocation.invokeDaoFindById(param);
		assertThat(result.getDate()).isEqualTo(param);
		assertThat(result.getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
	}

	@Test
	@Tag("integration")
	public void testFindExchangeByDayShouldNotFind() throws SQLException {
		LocalDate param = LocalDate.parse(testDayError1);
		assertThrows(NotFoundException.class, () -> daoInvocation.invokeDaoFindById(param));
	}

	@Test
	@Tag("integration")
	public void testSearchAllShouldWork() throws SQLException {
		List<UVAExchange> result = daoInvocation.invokeDaoSearchAll();
		assertThat(result.size()).isEqualTo(SIZE_SELECT_ALL);
		assertThat(result.get(0).getDate()).isEqualTo(testDay1);
		assertThat(result.get(0).getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		assertThat(result.get(1).getDate()).isEqualTo(testDay2);
		assertThat(result.get(1).getRate()).isEqualTo(BigDecimal.valueOf(testRate2));
	}

	@Test
	@Tag("integration")
	public void testCreateShouldWork() throws SQLException {
		daoInvocation.invokeDaoCreate(new UVAExchange(LocalDate.parse(testDay3), BigDecimal.valueOf(testRate3)));
	}

	@Test
	@Tag("integration")
	public void testCreateShouldNotWork() throws SQLException {
		assertThrows(ServerErrorException.class, () -> daoInvocation
				.invokeDaoCreate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
	}

	@Test
	@Tag("integration")
	public void testUpdateShouldWork() throws SQLException {
		daoInvocation.invokeDaoUpdate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
	}

	@Test
	@Tag("integration")
	public void testDeleteShouldWork() throws SQLException {
		daoInvocation.invokeDaoDelete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
	}

}
