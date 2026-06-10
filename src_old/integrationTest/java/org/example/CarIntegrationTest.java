package org.example;

import org.example.infrastructure.web.dto.CarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CarIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnCarsFromSeedData() {
        ResponseEntity<CarDto[]> response = restTemplate.getForEntity("/api/v1/cars", CarDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CarDto[] cars = response.getBody();
        assertNotNull(cars);

        assertTrue(cars.length >= 1, "В базе должна быть как минимум одна машина из миграций");
        assertEquals("Tesla", cars[0].getBrand());
        assertEquals("Model 3", cars[0].getModel());
        assertNotNull(cars[0].getId());
    }

    @Test
    void shouldReturnForbiddenWhenDeletingCarWithoutAuth() {
        UUID randomCarId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", UUID.randomUUID().toString());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/cars/" + randomCarId,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Пользователь не авторизован"));
    }
}