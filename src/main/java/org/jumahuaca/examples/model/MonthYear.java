package org.jumahuaca.examples.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonthYear {

	private Integer month;

	private Integer year;
	
	
	public MonthYear() {
		super();
	}

	public MonthYear(Integer month, Integer year) {
		super();
		this.month = month;
		this.year = year;
	}

	@JsonProperty
	public Integer getMonth() {
		return month;
	}

	@JsonProperty
	public Integer getYear() {
		return year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		MonthYear other = (MonthYear) obj;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

}
