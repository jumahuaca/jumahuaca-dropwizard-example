package org.jumahuaca.examples.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

import javax.sql.DataSource;

import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAScrapingProcess;

public class UvaScrapingProcessDaoImpl implements UvaScrapingProcessDao {
	
	private DataSource ds;

	public UvaScrapingProcessDaoImpl(DataSource ds) {
		this.ds = ds;
	}

	@Override
	public Integer createAndReturn(UVAScrapingProcess process) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("INSERT INTO uva_scraping_process (from_date, to_date, status, process_date)" + 
					"VALUES(?,?,?,?) RETURNING id");
			pst.setDate(1, new java.sql.Date(
					Date.from(process.getFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.setDate(2, new java.sql.Date(
					Date.from(process.getToDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.setString(3, process.getStatus().name());
			pst.setDate(4, new java.sql.Date(
					Date.from(process.getProcessDate().atZone(ZoneId.systemDefault()).toInstant()).getTime()));
			
			//pst.executeUpdate();
			ResultSet lastResultSet = pst.executeQuery();
			lastResultSet.next();
			return lastResultSet.getInt(1);
		} catch (SQLException e) {
			throw new ServerErrorException("SQL Insertion error");
		} finally {
			closeResources(conn, pst);
		}
	}

	@Override
	public void update(UVAScrapingProcess process) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("UPDATE uva_scraping_process set status = ? WHERE id = ?");
			pst.setString(1, process.getStatus().name());
			pst.setInt(2, process.getId());
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException("SQL Update error");
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
