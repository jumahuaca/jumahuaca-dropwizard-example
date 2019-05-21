package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JdbcExchangeDaoImplTests extends JdbcCrudDaoTests<UVAExchange, LocalDate>{

	private static final int SIZE_SELECT_ALL = 2;

	private UvaExchangeDaoImpl dao;
	
	private DaoInvocation<UVAExchange, LocalDate> daoInvocation;

	private String testDay1 = "2017-01-01";

	private String testDay2 = "2999-01-01";

	private String testDay3 = "2999-02-01";

	private Double testRate1 = 1.5d;

	private Double testRate2 = 1.0d;

	@Override
	@BeforeEach
	public void setup() throws SQLException {
		stubDatasource();
		dao = new UvaExchangeDaoImpl(ds);
		daoInvocation = buildDaoInvocation();
	}	

	@Test
	public void testFindExchangeByDayShouldWork() throws SQLException {
		stubSelectOneQueryOk();
		LocalDate param = LocalDate.parse(testDay1);
		UVAExchange result = daoInvocation.invokeDaoFindById(param);
		assertThat(result.getDate()).isEqualTo(param);
		assertThat(result.getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		verifyResourceClose();
	}

	@Test
	public void testFindExchangeByDayShouldNotFind() throws SQLException {
		stubSelectOneQueryNotFound();
		LocalDate param = LocalDate.parse(testDay2);
		assertThrows(NotFoundException.class, () -> daoInvocation.invokeDaoFindById(param));
		verifyResourceClose();
	}

	@Test
	public void testFindExchangeByDayShouldNotWork() throws SQLException {
		stubSelectOneQueryError();
		LocalDate param = LocalDate.parse(testDay3);
		assertThrows(ServerErrorException.class, () -> daoInvocation.invokeDaoFindById(param));
		verifyResourceClose();
	}

	@Test
	public void testSearchAllShouldWork() throws SQLException {
		stubSelectAllQueryOk();
		List<UVAExchange> result = daoInvocation.invokeDaoSearchAll();
		assertThat(result.size()).isEqualTo(SIZE_SELECT_ALL);
		assertThat(result.get(0).getDate()).isEqualTo(testDay1);
		assertThat(result.get(0).getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		assertThat(result.get(1).getDate()).isEqualTo(testDay2);
		assertThat(result.get(1).getRate()).isEqualTo(BigDecimal.valueOf(testRate2));
		verifyResourceClose();
	}

	@Test
	public void testSearchAllShouldNotFind() throws SQLException {
		stubSelectAllNotFound();
		List<UVAExchange> result = daoInvocation.invokeDaoSearchAll();
		assertThat(result).isEmpty();
		verifyResourceClose();
	}

	@Test
	public void testSearchAllShouldNotWork() throws SQLException {
		stubConnectionError();
		assertThrows(ServerErrorException.class, () -> daoInvocation.invokeDaoSearchAll());
		verifyConnectionClose();
	}

	@Test
	public void testCreateShouldWork() throws SQLException {
		stubQueryOk();
		daoInvocation.invokeDaoCreate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verifyExecuteUpdate();
		verifyResourceClose();
	}

	@Test
	public void testCreateShouldNotWork() throws SQLException {
		stubConnectionError();
		assertThrows(ServerErrorException.class,
				() -> daoInvocation.invokeDaoCreate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verifyConnectionClose();
	}
	
	@Test
	public void testUpdateShouldWork() throws SQLException {
		stubQueryOk();
		daoInvocation.invokeDaoUpdate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verifyExecuteUpdate();
		verifyResourceClose();
	}

	@Test
	public void testUpdateShouldNotWork() throws SQLException {
		stubConnectionError();
		assertThrows(ServerErrorException.class,
				() -> daoInvocation.invokeDaoUpdate(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verifyConnectionClose();
	}
	
	@Test
	public void testDeleteShouldWork() throws SQLException {
		stubQueryOk();
		daoInvocation.invokeDaoDelete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verifyConnectionClose();
		verifyResourceClose();
	}

	@Test
	public void testDeleteShouldNotWork() throws SQLException {
		stubConnectionError();
		assertThrows(ServerErrorException.class,
				() -> daoInvocation.invokeDaoDelete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verifyConnectionClose();
	}

	@Override
	public Map<String, Object> stubSelectOneResultSet() {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("exchange_day", testDay1);
		result.put("rate", testRate1);
		return result;
	}

	@Override
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

	@Override
	public DaoInvocation<UVAExchange, LocalDate> buildDaoInvocation() {
		return new JdbcExchangeDaoInvocation(dao);		
	}
}
