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
import org.jumahuaca.extensions.HttpWebServiceDoubleHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;



@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardJerseyExchangeResourceTests implements HttpWebServiceDoubleHelper<UVAExchange> {
	
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
		resetStubbedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfServerError() {
		Response response = extension.requestAllShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_GET_ALL_PATH, resources);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfUnknownError() throws SQLException {
		Response response = extension.requestAllShouldNotWorkBecauseOfUnknownError(this, PREFIX+UVA_EXCHANGE_GET_ALL_PATH, resources);
		assertResponse500(response);
		resetStubbedDao();
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
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfServerError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfNotFoundError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfNotFoundError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse404(response);
		resetStubbedDao();		
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfUknownError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response = extension.requestOneShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS, resources, templateResolver);
		assertResponse500(response);
		resetStubbedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOne();
		Response response = extension.postShouldWork(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse200(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.postShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.postShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_POST_PATH, resources);
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();
	}
	
	@Test
	public void putUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOne();
		Response response = extension.putShouldWork(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse200(response);
		verify(dao).update(mockedResult);		
		resetStubbedDao();	
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.putShouldNotWorkBecauseOfServerError(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse500(response);
		verify(dao).update(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOne();
		Response response =  extension.putShouldNotWorkBecauseOfUknownError(this, PREFIX+UVA_EXCHANGE_PUT_PATH, resources);
		assertResponse500(response);
		verify(dao).update(mockedResult);		
		resetStubbedDao();
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
		resetStubbedDao();
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
		resetStubbedDao();
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
		resetStubbedDao();
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfNotFoundError() throws SQLException {
		Map<String,String> templateResolver = new HashMap<String, String>();
		templateResolver.put(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam);
		templateResolver.put(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam);
		templateResolver.put(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam);
		Response response =  extension.deleteShouldNotWorkBecauseOfNotFoundError(this, PREFIX+UVA_EXCHANGE_REMOVE_PATH + UVA_EXCHANGE_REMOVE_PARAMS, resources, templateResolver);
		assertResponse404(response);
		resetStubbedDao();
	}

	private void resetStubbedDao() {
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
	public void stubRepositoryFindAllOk(List<UVAExchange> mockedResult) {
		when(dao.searchAll()).thenReturn(mockedResult);
	}

	@Override
	public void stubRepositoryFindAllNotFound() {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void stubRepositoryFindAllError() {
		when(dao.searchAll()).thenThrow(ServerErrorException.class);		
	}

	@Override
	public UVAExchange mockOne() {
		return new UVAExchange(LocalDate.parse(testDay1),BigDecimal.valueOf(testRate1));
	}

	@Override
	public void stubRepositoryFindByIdOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
	}

	@Override
	public void stubRepositoryFindByIdNotFound(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(NotFoundException.class);		
	}

	@Override
	public void stubRepositoryUpdateOk(UVAExchange mockedResult) {
		doNothing().when(dao).update(mockedResult);
	}

	@Override
	public void stubRepositoryUpdateError(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).update(mockedResult);	
	}

	@Override
	public void stubRepositoryDeleteOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doNothing().when(dao).delete(mockedResult);		
	}

	@Override
	public void stubRepositoryDeleteError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(ServerErrorException.class).when(dao).delete(mockedResult);			
	}

	@Override
	public void stubRepositoryFindByIdError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(RuntimeException.class);		
	}

	@Override
	public void stubRepositoryCreateOk(UVAExchange mockedResult) {
		doNothing().when(dao).create(mockedResult);
	}

	@Override
	public void stubRepositoryCreateServerError(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).create(mockedResult);	
	}

	@Override
	public void stubRepositoryCreateUknownError(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).create(mockedResult);	
	}

	@Override
	public void stubRepositoryUpdateUknownError(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).update(mockedResult);	
	}

	@Override
	public void stubRepositoryDeleteUknownError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(RuntimeException.class).when(dao).delete(mockedResult);			
	}

	@Override
	public void stubRepositoryDeleteNotFoundError(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(NotFoundException.class);		
	}
}
