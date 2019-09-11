package org.jumahuaca.examples.dropwizard.jersey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jumahuaca.assertions.ResponseStatusAssert.assertResponse200;
import static org.jumahuaca.assertions.ResponseStatusAssert.assertResponse404;
import static org.jumahuaca.assertions.ResponseStatusAssert.assertResponse500;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_DAY_PARAM;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_GET_ALL_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_GET_ONE_PARAMS;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_GET_ONE_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_MONTH_PARAM;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_POST_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_PUT_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_REMOVE_PARAMS;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_REMOVE_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_ROOT_PATH;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_YEAR_PARAM;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.resources.PathConstants;
import org.jumahuaca.examples.resources.UVAExchangeResource;
import org.jumahuaca.extensions.DropwizardResourceCrudExtension;
import org.jumahuaca.extensions.HttpWebServiceTestDoubleHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardJerseyExchangeResourceTests implements HttpWebServiceTestDoubleHelper<UVAExchange> {
	
	private static final String PREFIX = PathConstants.RESOURCE_VERSION+UVA_EXCHANGE_ROOT_PATH;
	
	public static UvaExchangeDao dao = Mockito.mock(UvaExchangeDaoImpl.class);
	
	private String testDay1 = "2016-03-31";
	
	private String testDay1YearParam = "2016";
	
	private String testDay1MonthParam = "03";
	
	private String testDay1DayParam = "31";

	private String testDay2 = "2016-02-04";

	private String testDay3 = "2016-04-08";

	private Double testRate1 = 14.05;

	private Double testRate2 = 14.06;
	
	private Double testRate3 = 14.14;
	
	@RegisterExtension
	public static final ResourceExtension resources = ResourceExtension.builder()
			.addResource(new UVAExchangeResource(dao)).build();
	
	@RegisterExtension
	public DropwizardResourceCrudExtension<UVAExchange> extension = new DropwizardResourceCrudExtension<UVAExchange>();
	
	
	@Test
	public void requestAllUVAExchangeShouldWork(){
		List<UVAExchange> mockedResult = mockSelectAllResult();
		Response response = extension.requestAllShouldWork(this, PREFIX+UVA_EXCHANGE_GET_ALL_PATH, resources);
		List<UVAExchange> result = response.readEntity(new GenericType<List<UVAExchange>>() {});
		assertResponse200(response);
		assertThat(result).isEqualTo(mockedResult);
		resetMockedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfServerError() {
		Response response = extension.requestAllShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_GET_ALL_PATH, resources);
		assertResponse500(response);
		resetMockedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfUnknownError() throws SQLException {
		Response response = extension.requestAllShouldNotWorkBecauseOfUnknownError(this, PREFIX+UVA_EXCHANGE_GET_ALL_PATH, resources);
		assertResponse500(response);
		resetMockedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldWork() throws SQLException {
		UVAExchange mockedResult = mockOne();
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		
		Response response = extension.requestOneShouldWork(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		UVAExchange result = response.readEntity(UVAExchange.class);
		assertResponse200(response);
		assertThat(result).isEqualTo(mockedResult);
		resetMockedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfServerError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		resetMockedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfNotFoundError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfNotFoundError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse404(response);
		resetMockedDao();		
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfUknownError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		resetMockedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOne();
		Response response = extension.postShouldWork(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse200(response);
		verify(dao).create(mockedResult);		
		resetMockedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.postShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetMockedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.postShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetMockedDao();
	}
	
	@Test
	public void putUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOne();
		Response response = extension.putShouldWork(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse200(response);
		verify(dao).update(mockedResult);		
		resetMockedDao();	
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.putShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse500(response);
		verify(dao).update(mockedResult);		
		resetMockedDao();		
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.putShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse500(response);
		verify(dao).update(mockedResult);		
		resetMockedDao();
	}
	
	@Test
	public void deleteUVAExchangeShouldWork() throws SQLException {
		UVAExchange mockedResult = mockOne();
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response =  extension.deleteShouldWork(this, PREFIX+UVA_EXCHANGE_REMOVE_PATH + UVA_EXCHANGE_REMOVE_PARAMS, resources, templateResolver);
		assertResponse200(response);
		verify(dao).delete(mockedResult);		
		resetMockedDao();
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfServerError() throws SQLException {
		UVAExchange mockedResult = mockOne();
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response =  extension.deleteShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_REMOVE_PATH + UVA_EXCHANGE_REMOVE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		verify(dao).delete(mockedResult);		
		resetMockedDao();
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfUknownError() throws SQLException {
		UVAExchange mockedResult = mockOne();
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response =  extension.deleteShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_REMOVE_PATH + UVA_EXCHANGE_REMOVE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		verify(dao).delete(mockedResult);		
		resetMockedDao();
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfNotFoundError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response =  extension.deleteShouldNotWorkBecauseOfNotFoundError(this, PREFIX+UVA_EXCHANGE_REMOVE_PATH + UVA_EXCHANGE_REMOVE_PARAMS, resources, templateResolver);
		assertResponse404(response);
		resetMockedDao();
	}

	private void resetMockedDao() {
		Mockito.reset(dao);
	}

	@Override
	public List<UVAExchange> mockSelectAllResult() {
		List<UVAExchange> result = new ArrayList<UVAExchange>();
		result.add(new UVAExchange(LocalDate.parse(testDay1),BigDecimal.valueOf(testRate1)));
		result.add(new UVAExchange(LocalDate.parse(testDay2),BigDecimal.valueOf(testRate2)));
		result.add(new UVAExchange(LocalDate.parse(testDay3),BigDecimal.valueOf(testRate3)));
		return result;
	}

	@Override
	public void mockRepositoryFindAllOk(List<UVAExchange> mockedResult) {
		when(dao.searchAll()).thenReturn(mockedResult);
	}

	@Override
	public void mockRepositoryFindAllNotFound() {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void mockRepositoryFindAllError() {
		when(dao.searchAll()).thenThrow(ServerErrorException.class);		
	}

	@Override
	public UVAExchange mockOne() {
		return new UVAExchange(LocalDate.parse(testDay1),BigDecimal.valueOf(testRate1));
	}

	@Override
	public void mockRepositoryFindByIdOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
	}

	@Override
	public void mockRepositoryFindByIdNotFound(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(NotFoundException.class);		
	}

	@Override
	public void mockRepositoryUpdateOk(UVAExchange mockedResult) {
		doNothing().when(dao).update(mockedResult);
	}

	@Override
	public void mockRepositoryUpdateError(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).update(mockedResult);	
	}

	@Override
	public void mockRepositoryDeleteOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doNothing().when(dao).delete(mockedResult);		
	}

	@Override
	public void mockRepositoryDeleteError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(ServerErrorException.class).when(dao).delete(mockedResult);			
	}

	@Override
	public void mockRepositoryFindByIdError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(RuntimeException.class);		
	}

	@Override
	public void mockRepositoryCreateOk(UVAExchange mockedResult) {
		doNothing().when(dao).create(mockedResult);
	}

	@Override
	public void mockRepositoryCreateServerError(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).create(mockedResult);	
	}

	@Override
	public void mockRepositoryCreateUknownError(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).create(mockedResult);	
	}

	@Override
	public void mockRepositoryUpdateUknownError(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).update(mockedResult);	
	}

	@Override
	public void mockRepositoryDeleteUknownError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(RuntimeException.class).when(dao).delete(mockedResult);			
	}

	@Override
	public void mockRepositoryDeleteNotFoundError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(NotFoundException.class);		
	}
}
