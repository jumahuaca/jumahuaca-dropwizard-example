package org.jumahuaca.examples.exceptions;

public class MoreThanOneResultException extends GenericException {

	private static final long serialVersionUID = -8834117245429158373L;

	public MoreThanOneResultException(String message) {
		super(GenericException.MORE_THAN_ONE_CODE, message);
	}

}
