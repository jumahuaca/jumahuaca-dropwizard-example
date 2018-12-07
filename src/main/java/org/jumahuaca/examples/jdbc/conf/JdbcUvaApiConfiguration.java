package org.jumahuaca.examples.jdbc.conf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.db.DataSourceFactory;

public class JdbcUvaApiConfiguration extends io.dropwizard.Configuration{
	
	private String quotationLastDay;
	
	private String dateFormat;
	
	private String jdbcUser;
	
	private String jdbcPassword;
	
	@Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
	
	public JdbcUvaApiConfiguration() {
		super();
	}

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

	public JdbcUvaApiConfiguration(String quotationLastDay, String dateFormat) {
		super();
		this.quotationLastDay = quotationLastDay;
		this.dateFormat = dateFormat;
	}

	@JsonProperty
	public String getQuotationLastDay() {
		return quotationLastDay;
	}

	@JsonProperty
	public void setQuotationLastDay(String quotationLastDay) {
		this.quotationLastDay = quotationLastDay;
	}

	@JsonProperty
	public String getDateFormat() {
		return dateFormat;
	}

	@JsonProperty
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@JsonProperty
	public String getJdbcUser() {
		return jdbcUser;
	}

	@JsonProperty
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	@JsonProperty
	public String getJdbcPassword() {
		return jdbcPassword;
	}

	@JsonProperty
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	
	
	
	
}
