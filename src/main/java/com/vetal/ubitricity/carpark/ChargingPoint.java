package com.vetal.ubitricity.carpark;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vetal.ubitricity.enums.ChargerStatus;

public class ChargingPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer number;

	private ChargerStatus typeCharger = ChargerStatus.OFF;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS")
	private Date conectionTime;

	public ChargingPoint(int number) {
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public ChargerStatus getTypeCharger() {
		return typeCharger;
	}

	public void setTypeCharger(ChargerStatus typeCharger) {
		this.typeCharger = typeCharger;
	}

	public Date getConectionTime() {
		return conectionTime;
	}

	public void setConectionTime(Date conectionTime) {
		this.conectionTime = conectionTime;
	}

	public ChargingPoint() {
	}

	public void changeStatus(ChargerStatus typeCharger) {
		if (typeCharger.getValue() > 0) {
			if(this.typeCharger.getValue() == 0)
			    this.conectionTime = new Date();
		}
		else this.conectionTime = null;
		this.typeCharger = typeCharger;
	}
}