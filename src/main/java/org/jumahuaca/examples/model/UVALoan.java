package org.jumahuaca.examples.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UVALoan implements Serializable {

	private static final long serialVersionUID = 7017329449457166633L;

	private Integer id;

	private LocalDate loanDate;

	private Long holderDNI;

	private Long coholderDNI;

	private BigDecimal pesosValue;

	private BigDecimal uvaValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(LocalDate loanDate) {
		this.loanDate = loanDate;
	}

	public Long getHolderDNI() {
		return holderDNI;
	}

	public void setHolderDNI(Long holderDNI) {
		this.holderDNI = holderDNI;
	}

	public Long getCoholderDNI() {
		return coholderDNI;
	}

	public void setCoholderDNI(Long coholderDNI) {
		this.coholderDNI = coholderDNI;
	}

	public BigDecimal getPesosValue() {
		return pesosValue;
	}

	public void setPesosValue(BigDecimal pesosValue) {
		this.pesosValue = pesosValue;
	}

	public BigDecimal getUvaValue() {
		return uvaValue;
	}

	public void setUvaValue(BigDecimal uvaValue) {
		this.uvaValue = uvaValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		UVALoan other = (UVALoan) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
