package org.jumahuaca.examples.app.health;

import javax.ws.rs.core.Response;

import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.resources.UVAExchangeResource;

import com.codahale.metrics.health.HealthCheck;

public class ExchangeHealthCheck extends HealthCheck {

	private static final int HEALTH_DAY = 1;

	private static final int HEALTH_MONTH = 1;

	private static final int HEALTH_YEAR = 2016;

	private UVAExchangeResource resource;

	public ExchangeHealthCheck(UVAExchangeResource resource) {
		super();
		this.resource = resource;
	}

	@Override
	protected Result check() throws Exception {
		Response response =  resource.getExchange(HEALTH_YEAR, HEALTH_MONTH, HEALTH_DAY);
		if(! (response.getEntity() instanceof UVAExchange)) {
			return Result.unhealthy("Error invoking");
		}
		UVAExchange exchangeToCheck = (UVAExchange)response.getEntity();
		if (exchangeToCheck == null) {
			return Result.unhealthy("Null resource");
		}
		if (exchangeToCheck.getDate().getYear() == HEALTH_YEAR
				&& exchangeToCheck.getDate().getMonth().getValue() == HEALTH_MONTH
				&& exchangeToCheck.getDate().getDayOfMonth() == HEALTH_DAY) {
			return Result.healthy();
		}
		return Result.unhealthy("Unknown error");
	}

}
