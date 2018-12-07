package org.jumahuaca.examples.exceptions;

public abstract class GenericException extends RuntimeException{

	private static final long serialVersionUID = 2530957273964698660L;

	public static final Integer NOT_FOUND_CODE = 1;

	public static final Integer SERVER_ERROR_CODE = 2;

	public static final Integer MORE_THAN_ONE_CODE = 3;

	private Integer code;

	private String message;

	public GenericException(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
