package com.cognizant.customerservice.exception;

public class ServiceFailException extends Exception {
	/**
	 * If service fails this exception occurs
	 */
	private static final long serialVersionUID = 3029158143662373079L;

	public ServiceFailException(String message) {
		super(message);
	}

}
