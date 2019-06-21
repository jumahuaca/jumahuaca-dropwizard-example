package org.jumahuaca.examples.resources;

import static org.jumahuaca.examples.resources.PathConstants.*;
import java.time.LocalDate;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jumahuaca.examples.dao.UvaExchangeDao;
import org.jumahuaca.examples.exceptions.NotFoundException;
import org.jumahuaca.examples.exceptions.ServerErrorException;
import org.jumahuaca.examples.model.UVAExchange;

@Path(RESOURCE_VERSION+UVA_EXCHANGE_ROOT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UVAExchangeResource {

	private UvaExchangeDao dao;

	public UVAExchangeResource(UvaExchangeDao dao) {
		this.dao = dao;
	}
	
	@GET
	@Path(UVA_EXCHANGE_GET_ALL_PATH)
	public Response getAllExchanges() {
		try {
			return Response.ok(dao.searchAll()).build();			
		} catch (ServerErrorException e) {
			return Response.serverError().entity("Uknown error. Can not recover exchange").build();
		}catch(Exception e) {
			return Response.serverError().entity("Invocation error. Can not recover exchange").build();			
		}
	}

	@GET
	@Path(UVA_EXCHANGE_GET_ONE_PATH+UVA_EXCHANGE_GET_ONE_PARAMS)
	public Response getExchange(@PathParam("year") Integer year, @PathParam("month") Integer month,
			@PathParam("day") Integer day) {
		LocalDate date = LocalDate.of(year, month, day);
		try {
			return Response.ok(dao.findExchangeByDay(date)).build();			
		} catch (ServerErrorException e) {
			return Response.serverError().entity("Uknown error. Can not recover exchange").build();
		}catch (NotFoundException e) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Exchange not found for day " + date.toString()).build();
		}catch(Exception e) {
			return Response.serverError().entity("Invocation error. Can not recover exchange").build();			
		}
	}
	
	@POST
	@Path(UVA_EXCHANGE_POST_PATH)
	public Response post(UVAExchange exchange) {
		try {
			dao.create(exchange);
			return Response.ok().build();
		} catch (ServerErrorException e) {
			return Response.serverError().entity("Uknown error. Can not insert exchange").build();
		}catch(Exception e) {
			return Response.serverError().entity("Invocation error. Can not insert exchange").build();			
		}
	}
	
	@PUT
	@Path(UVA_EXCHANGE_PUT_PATH)
	public Response put(UVAExchange exchange) {
		try {
			dao.update(exchange);
			return Response.ok().build();
		} catch (ServerErrorException e) {
			return Response.serverError().entity("Uknown error. Can not insert exchange").build();
		}catch(Exception e) {
			return Response.serverError().entity("Invocation error. Can not insert exchange").build();			
		}
	}
	
	@DELETE
	@Path(UVA_EXCHANGE_REMOVE_PATH+UVA_EXCHANGE_REMOVE_PARAMS)
	public Response remove(@PathParam("year") Integer year, @PathParam("month") Integer month,
			@PathParam("day") Integer day) 
	{
		try {
			LocalDate date = LocalDate.of(year, month, day);
			UVAExchange toDelete = dao.findExchangeByDay(date);
			dao.delete(toDelete);
			return Response.ok().build();
		} catch (ServerErrorException e) {
			return Response.serverError().entity("Uknown error. Can not remove exchange").build();
		}catch (NotFoundException e) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Exchange not found for deleting ").build();
		}catch(Exception e) {
			return Response.serverError().entity("Invocation error. Can not remove exchange").build();			
		}
	}

}
