package com.vetal.ubitricity.carpark;

import java.util.List;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vetal.ubitricity.enums.ChargerStatus;

@Service
public class CarParkService {

	@Autowired
	CarParkRepository carParkRepository;

	@Value("${carpark.capacity}")
	private int capacity;

	public List<CarPark> getAllCarParks() {
		return (List<CarPark>) carParkRepository.findAll();
	}
	
	public CarPark getCarPark(int carParkId) {
		return carParkRepository.findById(carParkId).orElseThrow(EntityNotFoundException::new);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateChargingPoints(int id, List<ChargingPoint> chargingPoints) {
		CarPark carPark = carParkRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		carPark.setChargingPoints(chargingPoints);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public CarPark updateChargingPoint(int carParkId, int chargingPointNumber, Boolean isActive) {
		CarPark carPark = carParkRepository.findById(carParkId).orElseThrow(EntityNotFoundException::new);
		if(Boolean.TRUE.equals(isActive)) {
			carPark.getChargingPoint(chargingPointNumber).changeStatus(ChargerStatus.FAST);
			while(carPark.getCurrentCapacity() > capacity) {
				carPark.setLowChargeForEarliestConnection();
			}
		}
		else {
			int freeCapacity = carPark.getChargingPoint(chargingPointNumber).getTypeCharger().getValue();
			carPark.getChargingPoint(chargingPointNumber).changeStatus(ChargerStatus.OFF);
			while(freeCapacity > 0) {
				carPark.setFastChargeForLastConnection();
				freeCapacity = freeCapacity - 10;
			}
		}
		return carPark;
	}
}