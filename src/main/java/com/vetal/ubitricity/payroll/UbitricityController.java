package com.vetal.ubitricity.payroll;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vetal.ubitricity.carpark.CarPark;
import com.vetal.ubitricity.carpark.CarParkService;
import com.vetal.ubitricity.carpark.ChargeService;

@RestController
public class UbitricityController {
	
	@Autowired
	CarParkService carParkService;
	
    @Autowired
    private ChargeService chargeService;
	
    @GetMapping("/carparks")
    public List<CarPark> listCaparks() {
        return carParkService.getAllCarParks();
    }
    
    @GetMapping("/carparks/{carParkId}")
	public CarPark getCarPark(@PathVariable("carParkId") Integer carParkId) {
    	if(carParkId == null || carParkId <= 0) throw new CarParkIdException(carParkId);
    	return carParkService.getCarPark(carParkId);
	}
    
  //TODO rework to PUT method
	@PostMapping("/update_charger")
	@ResponseBody
	public CarPark updateCarPark(@RequestParam Integer carParkId, @RequestParam Integer chargingPointNumber, @RequestParam Boolean isActive) {
		return chargeService.changeChargingPoint(carParkId, chargingPointNumber, isActive);
	}
}
