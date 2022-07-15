package com.vetal.ubitricity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.vetal.ubitricity.carpark.CarPark;
import com.vetal.ubitricity.carpark.CarParkRepository;
import com.vetal.ubitricity.carpark.CarParkService;
import com.vetal.ubitricity.carpark.ChargeService;
import com.vetal.ubitricity.enums.ChargerStatus;

@SpringBootTest
class ChargeServiceTest {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private CarParkRepository carParkRepository;
    
	@Value("${carpark.capacity}")
	private int capacity;

    @SpyBean
    private CarParkService carParkService;

    private final HashMap<Integer, Boolean> chargingPointsStatuses = new HashMap<>();
    
    private CarPark carPark;

    private static final int INIT_VERSION = 0;
    
	@BeforeEach
	void setUp() {	
		carPark = carParkRepository.findById(1).get();
	
		chargingPointsStatuses.put(9, true);
		chargingPointsStatuses.put(10, true);
	}
	
    @Test
    void shouldChangeChargingPointsWithoutConcurrency() {
    	assertAll(
    			() -> assertEquals(INIT_VERSION, carPark.getVersion()),
    			() -> assertEquals(capacity, carPark.getCurrentCapacity()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(1).getTypeCharger()),  
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(2).getTypeCharger()),  
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(3).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(4).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(5).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(6).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(7).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(8).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(9).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(10).getTypeCharger())
    	);
    	
    	//switch on 9th and 10th charging points
    	chargingPointsStatuses.forEach((number, isActive) ->{
    		chargeService.changeChargingPoint(1, number, isActive);
    	});

        carPark = carParkRepository.findById(1).get();
        assertAll(
                () -> assertEquals(2, carPark.getVersion()),
    			() -> assertEquals(capacity, carPark.getCurrentCapacity()),
    			//1,5,6,8 become LOW when 9th and 10th turned on with FAST
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(1).getTypeCharger()),  
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(2).getTypeCharger()),  
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(3).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(4).getTypeCharger()),
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(5).getTypeCharger()),
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(6).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(7).getTypeCharger()),
                () -> assertEquals(ChargerStatus.LOW, carPark.getChargingPoint(8).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(9).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(10).getTypeCharger()),
                () -> verify(carParkService, times(2)).updateChargingPoint(anyInt(), anyInt(), anyBoolean())
        );
    }
    
    @Test
    void shouldChangeChargingPointsWithConcurrency() throws InterruptedException {
    	assertAll(
    			() -> assertEquals(2, carPark.getVersion()),
    			() -> assertEquals(capacity, carPark.getCurrentCapacity()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(9).getTypeCharger()),
                () -> assertEquals(ChargerStatus.FAST, carPark.getChargingPoint(10).getTypeCharger())
    	);
    	
    	//switch off simultaneously the 9th and 10th charging points
        ExecutorService executor = Executors.newFixedThreadPool(chargingPointsStatuses.size());
    	chargingPointsStatuses.forEach((number, isActive) ->{
    		executor.execute(() -> chargeService.changeChargingPoint(1, number, !isActive));
	    });
    
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        carPark = carParkRepository.findById(1).get();
        assertAll(
                () -> assertEquals(4, carPark.getVersion()),
    			() -> assertEquals(capacity, carPark.getCurrentCapacity()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(9).getTypeCharger()),
                () -> assertEquals(ChargerStatus.OFF, carPark.getChargingPoint(10).getTypeCharger()),
                () -> verify(carParkService, times(3)).updateChargingPoint(anyInt(), anyInt(), anyBoolean())
        );
    }
}
