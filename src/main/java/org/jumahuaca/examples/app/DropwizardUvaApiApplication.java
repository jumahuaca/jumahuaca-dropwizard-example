package org.jumahuaca.examples.app;

import javax.sql.DataSource;

import org.jumahuaca.examples.app.health.ExchangeHealthCheck;
import org.jumahuaca.examples.batch.UVALoanFeeUpdateJobLauncher;
import org.jumahuaca.examples.conf.DropwizardUvaApiConfiguration;
import org.jumahuaca.examples.dao.JdbcPostgreSQLDataSource;
import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.dao.UvaLoanFeeDao;
import org.jumahuaca.examples.dao.UvaLoanFeeDaoImpl;
import org.jumahuaca.examples.dao.UvaScrapingProcessDaoImpl;
import org.jumahuaca.examples.resources.BatchResource;
import org.jumahuaca.examples.resources.UVAExchangeResource;
import org.jumahuaca.examples.resources.UVAScraperResource;
import org.jumahuaca.examples.scraper.UVAScraper;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DropwizardUvaApiApplication extends Application<DropwizardUvaApiConfiguration> {

	@Override
	public void run(DropwizardUvaApiConfiguration configuration, Environment environment) throws Exception {
		final JdbiFactory factory = new JdbiFactory();
		factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
		DataSource ds = buildDatasource(configuration);
		UvaExchangeDao exchangeDao = new UvaExchangeDaoImpl(ds);
		UvaLoanFeeDao feeDao = new UvaLoanFeeDaoImpl(ds);
		UVAExchangeResource exchangeResource = new UVAExchangeResource(exchangeDao);
		UVAScraper scraper = new UVAScraper();
		UVAScraperResource scraperResource = new UVAScraperResource(new UvaScrapingProcessDaoImpl(ds), exchangeDao,scraper);
		UVALoanFeeUpdateJobLauncher launcher = new UVALoanFeeUpdateJobLauncher(exchangeDao, feeDao);
		BatchResource batchResource = new BatchResource(launcher);
		environment.jersey().register(exchangeResource);
		environment.jersey().register(scraperResource);
		environment.jersey().register(batchResource);
		environment.healthChecks().register("Exchange", new ExchangeHealthCheck(exchangeResource));

	}
	

	private DataSource buildDatasource(DropwizardUvaApiConfiguration configuration) {
		String url = configuration.getDataSourceFactory().getUrl();
		String user = configuration.getJdbcUser();
		String password = configuration.getJdbcPassword();
		return new JdbcPostgreSQLDataSource(url, user, password).getDataSource();
	}


	@Override
	public String getName() {
		return "dropwizard-uva-api";
	}

	@Override
	public void initialize(Bootstrap<DropwizardUvaApiConfiguration> bootstrap) {
		bootstrap.addBundle(new MigrationsBundle<DropwizardUvaApiConfiguration>() {
			@Override
			public PooledDataSourceFactory getDataSourceFactory(DropwizardUvaApiConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new DropwizardUvaApiApplication().run(args);
	}

}
