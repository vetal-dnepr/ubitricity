package com.vetal.ubitricity.carpark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChargeService {

	@Autowired 
	CarParkService carParkService;
	
	@Transactional(readOnly = true)
    public CarPark changeChargingPoint(int carParkId, int cargingPointId, Boolean isActive) {
        try {
        	return carParkService.updateChargingPoint(carParkId, cargingPointId, isActive);
        } catch (ObjectOptimisticLockingFailureException e) {
        	return carParkService.updateChargingPoint(carParkId, cargingPointId, isActive);
        }
    }
}