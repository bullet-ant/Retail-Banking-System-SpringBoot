package com.cognizant.customerservice.exception;

public class CustomerAlreadyExistException extends RuntimeException {

	/**
	 * Consumer Already Exist Exception
	 */
	private static final long serialVersionUID = -2862505141325062716L;

	public CustomerAlreadyExistException() {
		super();
	}

	public CustomerAlreadyExistException(String message) {
		super(message);
	}

}
