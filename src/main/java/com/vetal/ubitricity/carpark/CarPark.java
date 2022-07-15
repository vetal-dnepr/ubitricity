package com.vetal.ubitricity.carpark;

import java.util.Comparator;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vetal.ubitricity.enums.ChargerStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@Entity
@Table(name = "carparks")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CarPark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name = "";

	private String address = "";

	@Type(type = "jsonb")
	@Column(name = "charging_points", columnDefinition = "jsonb")
	private List<ChargingPoint> chargingPoints;

	@Version
	private int version = 0;

	public CarPark() {
	}

	public CarPark(String name, String address, List<ChargingPoint> chargingPoints) {
		this.name = name;
		this.address = address;
		this.chargingPoints = chargingPoints;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<ChargingPoint> getChargingPoints() {
		return chargingPoints;
	}

	public void setChargingPoints(List<ChargingPoint> chargingPoints) {
		this.chargingPoints = chargingPoints;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ChargingPoint getChargingPoint(int chargingPointNumber) {
		return chargingPoints.stream().filter(point -> point.getNumber() == chargingPointNumber).findFirst()
				.orElseThrow(EntityNotFoundException::new);
	}

	public int getCurrentCapacity() {
		return chargingPoints.stream().mapToInt(point -> point.getTypeCharger().getValue()).sum();
	}

	public long getActivePoints() {
		return chargingPoints.stream().filter(point -> point.getTypeCharger().getValue() != 0).count();
	}

	public void setLowChargeForEarliestConnection() {
		Comparator<ChargingPoint> comparator = Comparator.comparing(ChargingPoint::getConectionTime);
		ChargingPoint chargingPoint = chargingPoints.stream().filter(point -> point.getTypeCharger().getValue() == 20)
				.filter(point -> point.getConectionTime() != null).min(comparator)
				.orElseThrow(EntityNotFoundException::new);
		chargingPoint.changeStatus(ChargerStatus.LOW);
	}

	public void setFastChargeForLastConnection() {
		Comparator<ChargingPoint> comparator = Comparator.comparing(ChargingPoint::getConectionTime);
		ChargingPoint chargingPoint = chargingPoints.stream().filter(point -> point.getTypeCharger().getValue() == 10)
				.filter(point -> point.getConectionTime() != null).max(comparator)
				.orElseThrow(EntityNotFoundException::new);
		chargingPoint.changeStatus(ChargerStatus.FAST);
	}
}
