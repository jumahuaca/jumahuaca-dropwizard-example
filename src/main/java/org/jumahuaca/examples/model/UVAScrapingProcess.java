package org.jumahuaca.examples.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UVAScrapingProcess implements Cloneable {

	private Integer id;

	private LocalDate fromDate;

	private LocalDate toDate;

	private UVAScrapingProcessStatus status;

	private LocalDateTime processDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public UVAScrapingProcessStatus getStatus() {
		return status;
	}

	public void setStatus(UVAScrapingProcessStatus status) {
		this.status = status;
	}

	public LocalDateTime getProcessDate() {
		return processDate;
	}

	public void setProcessDate(LocalDateTime processDate) {
		this.processDate = processDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
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
		UVAScrapingProcess other = (UVAScrapingProcess) obj;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status != other.status)
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		UVAScrapingProcess result = new UVAScrapingProcess();
		result.setFromDate(this.getFromDate());
		result.setId(this.getId());
		result.setProcessDate(this.getProcessDate());
		result.setStatus(this.getStatus());
		result.setToDate(this.getToDate());
		return result;
	}

	@Override
	public String toString() {
		return "UVAScrapingProcess [id=" + id + ", fromDate=" + fromDate + ", toDate=" + toDate + ", status=" + status
				+ ", processDate=" + processDate + "]";
	}

}
