package org.jumahuaca.examples.jdbc.app;

import javax.sql.DataSource;

import org.jumahuaca.examples.jdbc.app.health.ExchangeHealthCheck;
import org.jumahuaca.examples.jdbc.conf.JdbcUvaApiConfiguration;
import org.jumahuaca.examples.jdbc.dao.JdbcPostgreSQLDataSource;
import org.jumahuaca.examples.jdbc.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.jdbc.resources.UVAExchangeResource;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class JdbcUvaApiApplication extends Application<JdbcUvaApiConfiguration> {

	@Override
	public void run(JdbcUvaApiConfiguration configuration, Environment environment) throws Exception {
		final JdbiFactory factory = new JdbiFactory();
		factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
		DataSource ds = buildDatasource(configuration);
		UVAExchangeResource exchangeResource = new UVAExchangeResource(new UvaExchangeDaoImpl(ds));
		environment.jersey().register(exchangeResource);
		environment.healthChecks().register("Exchange", new ExchangeHealthCheck(exchangeResource));

	}
	

	private DataSource buildDatasource(JdbcUvaApiConfiguration configuration) {
		String url = configuration.getDataSourceFactory().getUrl();
		String user = configuration.getJdbcUser();
		String password = configuration.getJdbcPassword();
		return new JdbcPostgreSQLDataSource(url, user, password).getDataSource();
	}


	@Override
	public String getName() {
		return "jdbc-uva-api";
	}

	@Override
	public void initialize(Bootstrap<JdbcUvaApiConfiguration> bootstrap) {
		bootstrap.addBundle(new MigrationsBundle<JdbcUvaApiConfiguration>() {
			@Override
			public PooledDataSourceFactory getDataSourceFactory(JdbcUvaApiConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new JdbcUvaApiApplication().run(args);
	}

}
