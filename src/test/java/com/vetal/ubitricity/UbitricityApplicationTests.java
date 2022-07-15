package com.vetal.ubitricity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.vetal.ubitricity.carpark.CarParkService;
import com.vetal.ubitricity.payroll.CarParkIdException;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class UbitricityApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	CarParkService carParkService;
	
	private static final int TEST_CAR_PARK_ID = 1;
	
	@Test
	void contextLoads() {
		assertThat(mockMvc).isNotNull();
		assertThat(carParkService).isNotNull();
	}
	
	@Test 
	@DisplayName("Get all CarParks")
	void getAllCarParks() {
		try {
			this.mockMvc.perform(get("/carparks")).andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(1) 
	@DisplayName("Get CarPark by Id")
	void getCarParkByNumber() {
		try {
			mockMvc.perform(get("/carparks/{carParkId}", TEST_CAR_PARK_ID))
			.andExpect(jsonPath("$.id").value("1"))
			.andExpect(jsonPath("$.name").value("Sunshine"))
			.andExpect(jsonPath("$.address").value("07545 Germany, Gera"))
			.andExpect(jsonPath("$.chargingPoints.length()", is(10)))
			.andExpect(jsonPath("$.chargingPoints[9].typeCharger").value("OFF"))
			.andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Check CarParkId Exception")
	void getError() {
		try {
			String carParkId = "-1";
			this.mockMvc.perform(get("/carparks/{carParkId}", carParkId)).andExpect(status().isBadRequest())
					.andExpect(result -> assertTrue(result.getResolvedException() instanceof CarParkIdException))
					.andExpect(result -> assertEquals(
							"Wrong carParkId: " + carParkId,
							result.getResolvedException().getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO rework to PUT method
	
	@Test
	@Order(2) 
	@DisplayName("Update Charging Point")
	void persistCharger() {
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("carParkId", "1");
			params.add("chargingPointNumber", "10");
			params.add("isActive", "true");
			
			this.mockMvc.perform(post("/update_charger").params(params))
			.andExpect(jsonPath("$.chargingPoints[9].typeCharger").value("FAST"))
			.andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
