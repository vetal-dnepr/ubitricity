package com.vetal.ubitricity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UbitricityIntegrationTest {

	@LocalServerPort
	int port;
	
	@Autowired
	private RestTemplateBuilder builder;

	@Test
	void testCarParkRequest() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/carparks/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}