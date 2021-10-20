package com.philips.rabbitmqproducertest.services.exceptions;

public class OrderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderException(String string) {
		super(string);
	}

	public OrderException(Exception e) {
		super(e);
	}

}
