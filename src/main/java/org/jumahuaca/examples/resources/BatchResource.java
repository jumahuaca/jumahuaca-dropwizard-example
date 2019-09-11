package org.jumahuaca.examples.resources;

import static org.jumahuaca.examples.resources.PathConstants.RESOURCE_VERSION;
import static org.jumahuaca.examples.resources.PathConstants.UVA_EXCHANGE_ROOT_PATH;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(RESOURCE_VERSION+UVA_EXCHANGE_ROOT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class BatchResource {

}
