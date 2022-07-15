package com.vetal.ubitricity.payroll;

public class CarParkIdException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	CarParkIdException(Integer carParkId) {
		super("Wrong carParkId: " + carParkId);
	}
}