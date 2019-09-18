package org.jumahuaca.examples.dao;

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

import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVALoanFee;
import org.jumahuaca.examples.model.UVALoanFeeId;

public class UvaLoanFeeDaoImpl implements UvaLoanFeeDao {
	
	private DataSource ds;

	public UvaLoanFeeDaoImpl(DataSource ds) {
		this.ds = ds;
	}

	@Override
	public List<UVALoanFee> findByLoanId(Integer loanId) {
		List<UVALoanFee> result = new ArrayList<UVALoanFee>();
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("SELECT f.loan_id, f.fee_number, f.fee_date, f.initial_capital, f.initial_interest, "
					+ "f.initial_total, f.final_capital, f.final_interest, f.final_total, u.loan_date FROM uva_loan_fee f inner join uva_loan u on f.loan_id = u.id where f.loan_id = ?");
			pst.setInt(1, loanId);

			ResultSet rs = pst.executeQuery();
			Integer feeNumber = null;
			LocalDate feeDate = null;
			BigDecimal initialCapital = null;
			BigDecimal initialInterest = null;
			BigDecimal initialTotal = null;
			BigDecimal finalCapital = null;
			BigDecimal finalInterest = null;
			BigDecimal finalTotal = null;
			LocalDate loanDate = null;
			while (rs.next()) {
				feeNumber = rs.getInt("fee_number");
				feeDate = LocalDate.parse(rs.getString("fee_date"));
				initialCapital = BigDecimal.valueOf(rs.getDouble("initial_capital"));
				initialInterest = BigDecimal.valueOf(rs.getDouble("initial_interest"));
				initialTotal = BigDecimal.valueOf(rs.getDouble("initial_total"));
				Double finalCapitalAux = rs.getDouble("final_capital");
				Double finalInterestAux = rs.getDouble("final_interest");
				Double finalTotalAux = rs.getDouble("final_total");
				finalCapital = finalCapitalAux!=null?BigDecimal.valueOf(finalCapitalAux):null;
				finalInterest = finalInterestAux!=null?BigDecimal.valueOf(finalInterestAux):null;
				finalTotal = finalTotalAux!=null?BigDecimal.valueOf(finalTotalAux):null;
				loanDate = LocalDate.parse(rs.getString("loan_date"));
				UVALoanFeeId id = new UVALoanFeeId();
				id.setLoanId(loanId);
				id.setFeeNumber(feeNumber);
				UVALoanFee fee = new UVALoanFee();
				fee.setId(id);
				fee.setFeeDate(feeDate);
				fee.setInitialCapital(initialCapital);
				fee.setInitialInterest(initialInterest);
				fee.setInitialTotal(initialTotal);
				fee.setFinalCapital(finalCapital);
				fee.setFinalInterest(finalInterest);
				fee.setFinalTotal(finalTotal);				
				fee.setLoanDate(loanDate);
				result.add(fee);
			}
		} catch (SQLException e) {
			throw new ServerErrorException("SQL error");
		} finally {
			closeResources(conn, pst);
		}
		return result;
	}
	
	@Override
	public void update(UVALoanFee fee) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ds.getConnection();
			pst = conn.prepareStatement("UPDATE uva_loan_fee set fee_date = ?, initial_capital = ?, initial_interest = ?, "
					+ "initial_total = ?, final_capital = ?, final_interest = ?, final_total = ? "
					+ "WHERE loan_id = ? and fee_number = ?");
			pst.setDate(1,  new java.sql.Date(Date.from(fee.getFeeDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
			pst.setDouble(2, fee.getInitialCapital().doubleValue());
			pst.setDouble(3, fee.getInitialInterest().doubleValue());
			pst.setDouble(4, fee.getInitialTotal().doubleValue());
			pst.setDouble(5, fee.getFinalCapital().doubleValue());
			pst.setDouble(6, fee.getFinalInterest().doubleValue());
			pst.setDouble(7, fee.getFinalTotal().doubleValue());
			pst.setInt(8, fee.getId().getLoanId());
			pst.setInt(9, fee.getId().getFeeNumber());
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
