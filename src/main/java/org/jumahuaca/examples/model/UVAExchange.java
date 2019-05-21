package org.jumahuaca.examples.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UVAExchange {

	private LocalDate date;

	private BigDecimal rate;

	public UVAExchange() {
		super();
	}

	public UVAExchange(LocalDate date, BigDecimal rate) {
		super();
		this.date = date;
		this.rate = rate;
	}

	@JsonProperty
	public LocalDate getDate() {
		return date;
	}

	@JsonProperty
	public BigDecimal getRate() {
		return rate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		UVAExchange other = (UVAExchange) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

}
