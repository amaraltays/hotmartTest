package com.hotmart.ex.errorhandling;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapeia exceções retornadas pela aplicação em status HTTP/ mensagens que o
 * cliente da aplicação receberão
 * 
 * @author Tays
 *
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

	public Response toResponse(Exception ex) {
		if (ex instanceof WebApplicationException) {
			return Response.status(((WebApplicationException) ex).getResponse().getStatus()).entity(ex.getMessage())
					.type("text/plain").build();
		}
		if (ex instanceof RequiredParameterException) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();

		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				.entity("Something bad happened. Please try again !!").type("text/plain").build();
	}
}