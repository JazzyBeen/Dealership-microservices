package org.example.infrastructure.web.controller;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.grpc.car.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "grpc.client.storage-service.address=static://localhost:9091",
        "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
@Testcontainers
class CarCatalogControllerIT {

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

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MockMvc mockMvc;

    private static Server mockServer;
    private static boolean shouldReturnError = false;

    @BeforeAll
    static void startServer() throws IOException {
        mockServer = ServerBuilder.forPort(9091)
                .addService(new CarCatalogServiceGrpc.CarCatalogServiceImplBase() {
                    @Override
                    public void getAvailableCars(Empty request, StreamObserver<CarListResponse> responseObserver) {
                        if (shouldReturnError) {
                            responseObserver.onError(Status.UNAVAILABLE.asRuntimeException());
                            return;
                        }
                        CarResponse car = CarResponse.newBuilder()
                                .setId("44444444-4444-4444-4444-444444444444")
                                .setBrand("Tesla")
                                .setModel("Model 3")
                                .setBasePrice("4000000")
                                .build();
                        responseObserver.onNext(CarListResponse.newBuilder().addCars(car).build());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void getCarById(CarIdRequest request, StreamObserver<CarResponse> responseObserver) {
                        if (request.getId().equals("00000000-0000-0000-0000-000000000000")) {
                            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
                            return;
                        }
                        CarResponse car = CarResponse.newBuilder()
                                .setId(request.getId())
                                .setBrand("Tesla")
                                .setModel("Model 3")
                                .setBasePrice("4000000")
                                .build();
                        responseObserver.onNext(car);
                        responseObserver.onCompleted();
                    }
                })
                .build()
                .start();
    }

    @AfterAll
    static void stopServer() {
        if (mockServer != null) {
            mockServer.shutdown();
        }
    }

    @AfterEach
    void resetFlag() {
        shouldReturnError = false;
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnCarsFromGrpc() throws Exception {
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Tesla"))
                .andExpect(jsonPath("$[0].model").value("Model 3"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnCarByIdFromGrpc() throws Exception {
        mockMvc.perform(get("/api/v1/cars/44444444-4444-4444-4444-444444444444"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Model 3"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenCarNotFoundOnStorage() throws Exception {
        mockMvc.perform(get("/api/v1/cars/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Автомобиль не найден на складе"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn503WhenStorageServiceIsUnavailable() throws Exception {
        shouldReturnError = true;
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Сервис склада временно недоступен. Попробуйте позже."));
    }
}