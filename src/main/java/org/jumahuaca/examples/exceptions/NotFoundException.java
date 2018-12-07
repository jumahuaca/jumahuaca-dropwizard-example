package org.jumahuaca.examples.exceptions;

public class NotFoundException extends GenericException{

	private static final long serialVersionUID = -8460509619223646018L;

	public NotFoundException(String message) {
		super(GenericException.NOT_FOUND_CODE, message);
	}

}
