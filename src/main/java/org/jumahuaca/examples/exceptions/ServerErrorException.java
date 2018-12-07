package org.jumahuaca.examples.exceptions;

public class ServerErrorException extends GenericException {

	private static final long serialVersionUID = 4134823833036114111L;

	public ServerErrorException(String message) {
		super(GenericException.SERVER_ERROR_CODE,message);
	}

}
