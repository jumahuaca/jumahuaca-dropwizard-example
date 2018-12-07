package org.jumahuaca.examples.jdbc.dao;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class JdbcPostgreSQLDataSource {
	
	private DataSource ds;
	
	public JdbcPostgreSQLDataSource(String url, String user, String password) {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(url);
		ds.setUser(user);
		ds.setPassword(password);
		this.ds = ds;
	}
	
	public DataSource getDataSource() {
		return this.ds;
	}

}
