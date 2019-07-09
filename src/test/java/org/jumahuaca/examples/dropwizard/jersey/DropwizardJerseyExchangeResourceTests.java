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
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.dao.UvaExchangeDaoImpl;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;
import org.jumahuaca.examples.resources.PathConstants;
import org.jumahuaca.examples.resources.UVAExchangeResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;



@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardJerseyExchangeResourceTests {
	
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
	
	@Test
	public void requestAllUVAExchangeShouldWork(){
		List<UVAExchange> mockedResult = mockSelectAllResult();
		stubSelectAllDaoOk(mockedResult);		
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ALL_PATH).request().get(Response.class);
		List<UVAExchange> result = response.readEntity(new GenericType<List<UVAExchange>>() {});
		assertResponse200(response);
		assertThat(result).isEqualTo(mockedResult);
		resetStubbedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfServerError() {
		stubSelectAllDaoThrowsCustomException();		
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ALL_PATH).request().get(Response.class);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestAllUVAExchangeShouldNotWorkBecauseOfUnknownError() throws SQLException {
		stubSelectAllDaoThrowsException();		
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ALL_PATH).request().get(Response.class);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldWork() throws SQLException {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubSelectOneDaoOk(mockedResult);		
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().get(Response.class);
		UVAExchange result = response.readEntity(UVAExchange.class);
		assertResponse200(response);
		assertThat(result).isEqualTo(mockedResult);
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubSelectOneDaoThrowsCustomException(mockedResult.getDate());	
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().get(Response.class);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfNotFoundError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubSelectOneDaoThrowsNotFoundException(mockedResult.getDate());	
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().get(Response.class);
		assertResponse404(response);
		resetStubbedDao();
	}
	
	@Test
	public void requestOneUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubSelectOneDaoThrowsException(mockedResult.getDate());	
		Response response = resources.target(PREFIX+UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().get(Response.class);
		assertResponse500(response);
		resetStubbedDao();
	}
	
	@Test
	public void postUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubCreateDaoOk(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_POST_PATH).request().post(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertResponse200(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubCreateDaoThrowsCustomException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_POST_PATH).request().post(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void postUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubCreateDaoThrowsException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_POST_PATH).request().post(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertResponse500(response);
		verify(dao).create(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void putUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubUpdateDaoOk(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_PUT_PATH).request().put(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertResponse200(response);
		verify(dao).update(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubUpdateDaoThrowsCustomException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_PUT_PATH).request().put(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus()).isEqualTo(500);
		verify(dao).update(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void putUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubUpdateDaoThrowsException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_PUT_PATH).request().put(Entity.entity(mockedResult, MediaType.APPLICATION_JSON));
		assertResponse500(response);
		verify(dao).update(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void deleteUVAExchangeShouldWork() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubDeleteDaoOk(mockedResult);
		
		Response response = resources.target(PREFIX+UVA_EXCHANGE_REMOVE_PATH+UVA_EXCHANGE_REMOVE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().delete(Response.class);
		assertResponse200(response);
		verify(dao).delete(mockedResult);		
		resetStubbedDao();		
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfServerError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubDeleteDaoThrowsCustomException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_REMOVE_PATH+UVA_EXCHANGE_REMOVE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().delete(Response.class);
		assertResponse500(response);
		verify(dao).delete(mockedResult);		
		resetStubbedDao();			
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfUknownError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubDeleteDaoThrowsException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_REMOVE_PATH+UVA_EXCHANGE_REMOVE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().delete(Response.class);
		assertResponse500(response);
		verify(dao).delete(mockedResult);		
		resetStubbedDao();			
	}
	
	@Test
	public void deleteUVAExchangeShouldNotWorkBecauseOfNotFoundError() {
		UVAExchange mockedResult = mockOneUVAExchange();
		stubDeleteDaoThrowsNotFoundException(mockedResult);
		Response response = resources.target(PREFIX+UVA_EXCHANGE_REMOVE_PATH+UVA_EXCHANGE_REMOVE_PARAMS).
				resolveTemplate(UVA_EXCHANGE_YEAR_PARAM, testDay1YearParam).
				resolveTemplate(UVA_EXCHANGE_MONTH_PARAM, testDay1MonthParam).
				resolveTemplate(UVA_EXCHANGE_DAY_PARAM, testDay1DayParam)				
				.request().delete(Response.class);
		assertResponse404(response);
		resetStubbedDao();			
	}

	private void stubCreateDaoOk(UVAExchange mockedResult) {
		doNothing().when(dao).create(mockedResult);
	}

	private void stubCreateDaoThrowsCustomException(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).create(mockedResult);	
	}
	
	private void stubCreateDaoThrowsException(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).create(mockedResult);	
	}
	
	private void stubUpdateDaoOk(UVAExchange mockedResult) {
		doNothing().when(dao).update(mockedResult);
	}

	private void stubUpdateDaoThrowsCustomException(UVAExchange mockedResult) {
		doThrow(ServerErrorException.class).when(dao).update(mockedResult);	
	}
	
	private void stubUpdateDaoThrowsException(UVAExchange mockedResult) {
		doThrow(RuntimeException.class).when(dao).update(mockedResult);	
	}
	
	private void stubDeleteDaoOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doNothing().when(dao).delete(mockedResult);
	}
	
	private void stubDeleteDaoThrowsCustomException(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(ServerErrorException.class).when(dao).delete(mockedResult);	
	}
	
	private void stubDeleteDaoThrowsException(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
		doThrow(RuntimeException.class).when(dao).delete(mockedResult);	
	}
	
	private void stubDeleteDaoThrowsNotFoundException(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenThrow(NotFoundException.class);
	}
	
	private void stubSelectOneDaoOk(UVAExchange mockedResult) {
		when(dao.findExchangeByDay(mockedResult.getDate())).thenReturn(mockedResult);
	}
	
	

	private UVAExchange mockOneUVAExchange() {
		return new UVAExchange(LocalDate.parse(testDay1),BigDecimal.valueOf(testRate1));
	}

	private List<UVAExchange> mockSelectAllResult() {
		List<UVAExchange> result = new ArrayList<UVAExchange>();
		result.add(new UVAExchange(LocalDate.parse(testDay1),BigDecimal.valueOf(testRate1)));
		result.add(new UVAExchange(LocalDate.parse(testDay2),BigDecimal.valueOf(testRate2)));
		result.add(new UVAExchange(LocalDate.parse(testDay3),BigDecimal.valueOf(testRate3)));
		return result;
	}

	private void stubSelectAllDaoOk(List<UVAExchange> mockedResult) {
		when(dao.searchAll()).thenReturn(mockedResult);
	}
	
	private void stubSelectAllDaoThrowsCustomException() {
		when(dao.searchAll()).thenThrow(ServerErrorException.class);		
	}
	
	private void stubSelectOneDaoThrowsCustomException(LocalDate day) {
		when(dao.findExchangeByDay(day)).thenThrow(ServerErrorException.class);		
	}
	
	private void stubSelectOneDaoThrowsNotFoundException(LocalDate day) {
		when(dao.findExchangeByDay(day)).thenThrow(NotFoundException.class);		
	}
	
	private void stubSelectOneDaoThrowsException(LocalDate day) {
		when(dao.findExchangeByDay(day)).thenThrow(RuntimeException.class);		
	}
	
	private void stubSelectAllDaoThrowsException() {
		when(dao.searchAll()).thenThrow(RuntimeException.class);		
	}
	
	private void resetStubbedDao() {
		Mockito.reset(dao);
	}
}
