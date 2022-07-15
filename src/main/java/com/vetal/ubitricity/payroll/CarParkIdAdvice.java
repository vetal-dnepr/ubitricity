package com.vetal.ubitricity.payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CarParkIdAdvice {

	@ResponseBody
	@ExceptionHandler(CarParkIdException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String carParkIdHandler(CarParkIdException e) {
		return e.getMessage();
	}
}
