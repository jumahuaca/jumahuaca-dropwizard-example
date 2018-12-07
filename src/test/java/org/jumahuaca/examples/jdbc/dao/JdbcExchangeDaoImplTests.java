package org.jumahuaca.examples.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.jdbc.model.UVAExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class JdbcExchangeDaoImplTests {

	private static final int SIZE_SELECT_ALL = 2;

	@Mock
	private DataSource ds;

	@Mock
	private Connection connection;

	@Mock
	private PreparedStatement ps;

	@Spy
	private ResultSet rs;

	private UvaExchangeDaoImpl dao;

	private String testDay1 = "2017-01-01";

	private String testDay2 = "2999-01-01";

	private String testDay3 = "2999-02-01";

	private Double testRate1 = 1.5d;

	private Double testRate2 = 1.0d;

	@BeforeEach
	public void setup() throws SQLException {
		when(ds.getConnection()).thenReturn(connection);
		dao = new UvaExchangeDaoImpl(ds);
	}

	@Test
	public void testFindExchangeByDayShouldWork() throws SQLException {
		stubQueryOk();
		LocalDate param = LocalDate.parse(testDay1);
		UVAExchange result = dao.findExchangeByDay(param);
		assertThat(result.getDate()).isEqualTo(param);
		assertThat(result.getRate()).isEqualTo(BigDecimal.valueOf(testRate1));
		verifyResourceClose();
	}

	@Test
	public void testFindExchangeByDayShouldNotFind() throws SQLException {
		stubQueryNotFound();
		LocalDate param = LocalDate.parse(testDay2);
		assertThrows(NotFoundException.class, () -> dao.findExchangeByDay(param));
		verifyResourceClose();
	}

	@Test
	public void testFindExchangeByDayShouldNotWork() throws SQLException {
		stubQueryError();
		LocalDate param = LocalDate.parse(testDay3);
		assertThrows(ServerErrorException.class, () -> dao.findExchangeByDay(param));
		verifyResourceClose();
	}

	@Test
	public void testSearchAllShouldWork() throws SQLException {
		stubSelectAllOk();
		List<UVAExchange> result = dao.searchAll();
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
		List<UVAExchange> result = dao.searchAll();
		assertThat(result).isEmpty();
		verifyResourceClose();
	}

	@Test
	public void testSearchAllShouldNotWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
		assertThrows(ServerErrorException.class, () -> dao.searchAll());
		verify(connection).close();
	}

	@Test
	public void testCreateShouldWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(ps);
		dao.create(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verify(ps).executeUpdate();
		verifyResourceClose();
	}

	@Test
	public void testCreateShouldNotWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
		assertThrows(ServerErrorException.class,
				() -> dao.create(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verify(connection).close();
	}
	
	@Test
	public void testUpdateShouldWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(ps);
		dao.update(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verify(ps).executeUpdate();
		verifyResourceClose();
	}

	@Test
	public void testUpdateShouldNotWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
		assertThrows(ServerErrorException.class,
				() -> dao.update(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verify(connection).close();
	}
	
	@Test
	public void testDeleteShouldWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(ps);
		dao.delete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1)));
		verify(ps).executeUpdate();
		verifyResourceClose();
	}

	@Test
	public void testDeleteShouldNotWork() throws SQLException {
		when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
		assertThrows(ServerErrorException.class,
				() -> dao.delete(new UVAExchange(LocalDate.parse(testDay1), BigDecimal.valueOf(testRate1))));
		verify(connection).close();
	}

	private void verifyResourceClose() throws SQLException {
		verify(connection).close();
		verify(ps).close();
	}

	private void stubSelectAllNotFound() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false);
	}

	private void stubSelectAllOk() throws SQLException {
		when(rs.next()).thenReturn(true, true, false);
		when(ps.executeQuery()).thenReturn(rs);
		doAnswer(new Answer<String>() {
			private int count = 0;

			public String answer(InvocationOnMock invocation) {
				if (count == 0) {
					count++;
					return testDay1;
				}
				return testDay2;
			}
		}).when(rs).getString("exchange_day");
		doAnswer(new Answer<Double>() {
			private int count = 0;

			public Double answer(InvocationOnMock invocation) {
				if (count == 0) {
					count++;
					return testRate1;
				}
				return testRate2;
			}
		}).when(rs).getDouble("rate");
		when(connection.prepareStatement(anyString())).thenReturn(ps);
	}

	private void stubQueryOk() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true, false);
		when(rs.getString("exchange_day")).thenReturn(testDay1);
		when(rs.getDouble("rate")).thenReturn(testRate1);
		when(connection.prepareStatement(anyString())).thenReturn(ps);
	}

	private void stubQueryNotFound() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false, false);
		when(connection.prepareStatement(anyString())).thenReturn(ps);
	}

	private void stubQueryError() throws SQLException {
		when(ps.executeQuery()).thenThrow(SQLException.class);
		when(connection.prepareStatement(anyString())).thenReturn(ps);
	}

}
