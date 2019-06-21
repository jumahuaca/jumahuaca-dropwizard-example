package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.extensions.JdbcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class UVAExchangeJdbcDaoImplTests {
	
	@MockedDatasource
	public DataSource ds;
	
	@MockedConnection
	public Connection connection;

	@MockedPreparedStatement
	public PreparedStatement ps;

	@SpiedResultSet
	public ResultSet rs;
	
	@RegisterExtension
	public static final JdbcExtension extension = JdbcExtension.builder().build();
	
	private static final int SIZE_SELECT_ALL = 2;

	private UvaExchangeDaoImpl dao;
	
	private String testDay1 = "2017-01-01";

	private String testDay2 = "2999-01-01";

	private String testDay3 = "2999-02-01";

	private Double testRate1 = 1.5d;

	private Double testRate2 = 1.0d;

	@BeforeEach
	public void setup() throws SQLException {
		extension.stubDatasource(ds,connection);
		dao = new UvaExchangeDaoImpl(ds);
	}	

	@Test
	public void testFindExchangeByDayShouldWork() throws SQLException {
		extension.stubSelectOneQueryOk(ps, rs, connection, stubSelectOneResultSet());
		LocalDate param = LocalDate.parse(testDay1);
		UVAExchange result = dao.findExchangeByDay(param);
		assertThat(result.getDate()).isEqualTo(param);
		assertThat(result.getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testFindExchangeByDayShouldNotFind() throws SQLException {
		extension.stubSelectOneQueryNotFound(ps, connection, rs);
		LocalDate param = LocalDate.parse(testDay2);
		assertThrows(NotFoundException.class, () -> dao.findExchangeByDay(param));
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testFindExchangeByDayShouldNotWork() throws SQLException {
		extension.stubSelectOneQueryError(ps, connection, rs);
		LocalDate param = LocalDate.parse(testDay3);
		assertThrows(ServerErrorException.class, () -> dao.findExchangeByDay(param));
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testSearchAllShouldWork() throws SQLException {
		extension.stubSelectAllQueryOk(ps, connection, rs, stubSelectAllResultSet());
		List<UVAExchange> result = dao.searchAll();
		assertThat(result.size()).isEqualTo(SIZE_SELECT_ALL);
		assertThat(result.get(0).getDate()).isEqualTo(testDay1);
		assertThat(result.get(0).getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		assertThat(result.get(1).getDate()).isEqualTo(testDay2);
		assertThat(result.get(1).getRate()).isEqualTo(BigDecimal.valueOf(testRate2));
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testSearchAllShouldNotFind() throws SQLException {
		extension.stubSelectAllNotFound(ps, connection, rs);
		List<UVAExchange> result = dao.searchAll();
		assertThat(result).isEmpty();
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testSearchAllShouldNotWork() throws SQLException {
		extension.stubConnectionError(connection);
		assertThrows(ServerErrorException.class, () -> dao.searchAll());
		extension.verifyConnectionClose(connection);
	}

	@Test
	public void testCreateShouldWork() throws SQLException {
		extension.stubQueryOk(connection, ps);
		dao.create(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		extension.verifyExecuteUpdate(ps);
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testCreateShouldNotWork() throws SQLException {
		extension.stubConnectionError(connection);
		assertThrows(ServerErrorException.class,
				() -> dao.create(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		extension.verifyConnectionClose(connection);
	}
	
	@Test
	public void testUpdateShouldWork() throws SQLException {
		extension.stubQueryOk(connection, ps);
		dao.update(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		extension.verifyExecuteUpdate(ps);
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testUpdateShouldNotWork() throws SQLException {
		extension.stubConnectionError(connection);
		assertThrows(ServerErrorException.class,
				() -> dao.update(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		extension.verifyConnectionClose(connection);
	}
	
	@Test
	public void testDeleteShouldWork() throws SQLException {
		extension.stubQueryOk(connection, ps);
		dao.delete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		extension.verifyConnectionClose(connection);
		extension.verifyResourceClose(ps, connection);
	}

	@Test
	public void testDeleteShouldNotWork() throws SQLException {
		extension.stubConnectionError(connection);
		assertThrows(ServerErrorException.class,
				() -> dao.delete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		extension.verifyConnectionClose(connection);
	}

	public Map<String, Object> stubSelectOneResultSet() {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("exchange_day", testDay1);
		result.put("rate", testRate1);
		return result;
	}

	public Map<String, List<Object>> stubSelectAllResultSet() {
		List<Object> days = new ArrayList<Object>();
		days.add(testDay1);
		days.add(testDay2);
		List<Object> rates = new ArrayList<Object>();
		rates.add(testRate1);
		rates.add(testRate2);		
		Map<String,List<Object>> result = new HashMap<String,List<Object>>();
		result.put("exchange_day", days);
		result.put("rate", rates);
		return result;
	}
}
