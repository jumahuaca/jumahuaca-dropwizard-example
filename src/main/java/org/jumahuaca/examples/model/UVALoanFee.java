package org.jumahuaca.examples.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UVALoanFee implements Serializable {

	private static final long serialVersionUID = -6326941653251286296L;

	private UVALoanFeeId id;

	private Integer loanId;

	private LocalDate feeDate;

	private BigDecimal initialCapital;

	private BigDecimal initialInterest;

	private BigDecimal initialTotal;

	private BigDecimal finalCapital;

	private BigDecimal finalInterest;

	private BigDecimal finalTotal;

	private UVALoan loan;

	public UVALoanFeeId getId() {
		return id;
	}

	public void setId(UVALoanFeeId id) {
		this.id = id;
	}

	public Integer getLoanId() {
		return loanId;
	}

	public void setLoanId(Integer loanId) {
		this.loanId = loanId;
	}

	public BigDecimal getInitialCapital() {
		return initialCapital;
	}

	public void setInitialCapital(BigDecimal initialCapital) {
		this.initialCapital = initialCapital;
	}

	public BigDecimal getInitialInterest() {
		return initialInterest;
	}

	public void setInitialInterest(BigDecimal initialInterest) {
		this.initialInterest = initialInterest;
	}

	public BigDecimal getInitialTotal() {
		return initialTotal;
	}

	public void setInitialTotal(BigDecimal initialTotal) {
		this.initialTotal = initialTotal;
	}

	public BigDecimal getFinalCapital() {
		return finalCapital;
	}

	public void setFinalCapital(BigDecimal finalCapital) {
		this.finalCapital = finalCapital;
	}

	public BigDecimal getFinalInterest() {
		return finalInterest;
	}

	public void setFinalInterest(BigDecimal finalInterest) {
		this.finalInterest = finalInterest;
	}

	public BigDecimal getFinalTotal() {
		return finalTotal;
	}

	public void setFinalTotal(BigDecimal finalTotal) {
		this.finalTotal = finalTotal;
	}

	public UVALoan getLoan() {
		return loan;
	}

	public void setLoan(UVALoan loan) {
		this.loan = loan;
	}

	public LocalDate getFeeDate() {
		return feeDate;
	}

	public void setFeeDate(LocalDate feeDate) {
		this.feeDate = feeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feeDate == null) ? 0 : feeDate.hashCode());
		result = prime * result + ((finalCapital == null) ? 0 : finalCapital.hashCode());
		result = prime * result + ((finalInterest == null) ? 0 : finalInterest.hashCode());
		result = prime * result + ((finalTotal == null) ? 0 : finalTotal.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((initialCapital == null) ? 0 : initialCapital.hashCode());
		result = prime * result + ((initialInterest == null) ? 0 : initialInterest.hashCode());
		result = prime * result + ((initialTotal == null) ? 0 : initialTotal.hashCode());
		result = prime * result + ((loan == null) ? 0 : loan.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UVALoanFee other = (UVALoanFee) obj;
		if (feeDate == null) {
			if (other.feeDate != null)
				return false;
		} else if (!feeDate.equals(other.feeDate))
			return false;
		if (finalCapital == null) {
			if (other.finalCapital != null)
				return false;
		} else if (!finalCapital.equals(other.finalCapital))
			return false;
		if (finalInterest == null) {
			if (other.finalInterest != null)
				return false;
		} else if (!finalInterest.equals(other.finalInterest))
			return false;
		if (finalTotal == null) {
			if (other.finalTotal != null)
				return false;
		} else if (!finalTotal.equals(other.finalTotal))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (initialCapital == null) {
			if (other.initialCapital != null)
				return false;
		} else if (!initialCapital.equals(other.initialCapital))
			return false;
		if (initialInterest == null) {
			if (other.initialInterest != null)
				return false;
		} else if (!initialInterest.equals(other.initialInterest))
			return false;
		if (initialTotal == null) {
			if (other.initialTotal != null)
				return false;
		} else if (!initialTotal.equals(other.initialTotal))
			return false;
		if (loan == null) {
			if (other.loan != null)
				return false;
		} else if (!loan.equals(other.loan))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UVALoanFee [id=" + id + ", loanId=" + loanId + ", feeDate=" + feeDate + ", initialCapital="
				+ initialCapital + ", initialInterest=" + initialInterest + ", initialTotal=" + initialTotal
				+ ", finalCapital=" + finalCapital + ", finalInterest=" + finalInterest + ", finalTotal=" + finalTotal
				+ ", loan=" + loan + "]";
	}
	
	

}
