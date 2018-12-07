package org.jumahuaca.examples.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.jdbc.model.UVAExchange;

public class UvaExchangeDaoImpl implements UvaExchangeDao {

	private DataSource ds;

	public UvaExchangeDaoImpl(DataSource ds) {
		this.ds = ds;
	}

	@Override
	public UVAExchange findExchangeByDay(LocalDate day) {
		UVAExchange result = null;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("SELECT exchange_day, rate FROM uva_exchange WHERE exchange_day = ?");
			pst.setDate(1,
					new java.sql.Date(Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			ResultSet rs = pst.executeQuery();
			String exchangeDay = null;
			Double rate = null;
			while (rs.next()) {
				exchangeDay = rs.getString("exchange_day");
				rate = rs.getDouble("rate");

			}
			if (rate == null || exchangeDay == null) {
				throw new NotFoundException("Exchange not found");
			}
			result = new UVAExchange(LocalDate.parse(exchangeDay), BigDecimal.valueOf(rate));
		} catch (SQLException e) {
			throw new ServerErrorException("SQL error");
		} finally {
			closeResources(conn, pst);
		}
		return result;
	}

	@Override
	public List<UVAExchange> searchAll() {
		List<UVAExchange> result = new ArrayList<UVAExchange>();
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("SELECT exchange_day, rate FROM uva_exchange");
			ResultSet rs = pst.executeQuery();
			String exchangeDay = null;
			Double rate = null;
			while (rs.next()) {
				exchangeDay = rs.getString("exchange_day");
				rate = rs.getDouble("rate");
				result.add(new UVAExchange(LocalDate.parse(exchangeDay), BigDecimal.valueOf(rate)));
			}
		} catch (SQLException e) {
			throw new ServerErrorException("SQL error");
		} finally {
			closeResources(conn, pst);
		}
		return result;
	}

	@Override
	public void create(UVAExchange exchange) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("INSERT INTO uva_exchange (rate, exchange_day) values ( ? , ?)");
			pst.setDouble(1, exchange.getRate().doubleValue());
			pst.setDate(2, new java.sql.Date(
					Date.from(exchange.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException("SQL Insertion error");
		} finally {
			closeResources(conn, pst);
		}

	}

	@Override
	public void update(UVAExchange exchange) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("UPDATE uva_exchange set rate = ? WHERE exchange_day = ?");
			pst.setDouble(1, exchange.getRate().doubleValue());
			pst.setDate(2, new java.sql.Date(
					Date.from(exchange.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException("SQL Update error");
		} finally {
			closeResources(conn, pst);
		}
	}

	@Override
	public void delete(UVAExchange exchange) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("DELETE FROM uva_exchange WHERE exchange_day = ?");
			pst.setDate(1,
					new java.sql.Date(Date.from(exchange.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.executeUpdate();			
		} catch (SQLException e) {
			throw new ServerErrorException("SQL error");
		} finally {
			closeResources(conn, pst);
		}
	}
	
	private void closeResources(Connection connection, PreparedStatement pst) {
		try {
			if (connection != null) {
				connection.close();
			}
			if (pst != null) {
				pst.close();
			}
		} catch (Exception e2) {
			throw new ServerErrorException("Error closing resources");
		}
	}

}
