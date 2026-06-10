package org.example.infrastructure.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.grpc.car.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "grpc.server.port=9099"
})
@Testcontainers
class CarCatalogGrpcServiceIT {

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

    private ManagedChannel channel;
    private CarCatalogServiceGrpc.CarCatalogServiceBlockingStub stub;

    @BeforeEach
    void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9099)
                .usePlaintext()
                .build();
        stub = CarCatalogServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    @Test
    void shouldReturnAvailableCars() {
        CarListResponse response = stub.getAvailableCars(Empty.newBuilder().build());

        assertNotNull(response);
        assertTrue(response.getCarsCount() >= 1);
        assertEquals("Model 3", response.getCars(0).getModel());
        assertEquals("Tesla", response.getCars(0).getBrand());
    }

    @Test
    void shouldReturnCarById() {
        String liquibaseCarId = "44444444-4444-4444-4444-444444444444";

        CarResponse response = stub.getCarById(CarIdRequest.newBuilder().setId(liquibaseCarId).build());

        assertNotNull(response);
        assertEquals("Model 3", response.getModel());
        assertEquals("4000000.00", response.getBasePrice());
    }

    @Test
    void shouldReturnNotFoundForInvalidId() {
        CarIdRequest request = CarIdRequest.newBuilder().setId(UUID.randomUUID().toString()).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> stub.getCarById(request));
        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }
}