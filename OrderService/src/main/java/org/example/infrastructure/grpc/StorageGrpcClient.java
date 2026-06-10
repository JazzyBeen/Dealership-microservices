package org.example.infrastructure.grpc;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.exception.StorageServiceUnavailableException;
import org.example.grpc.car.CarCatalogServiceGrpc;
import org.example.grpc.car.CarIdRequest;
import org.example.grpc.car.CarResponse;
import org.example.grpc.car.Empty;
import org.example.infrastructure.web.dto.GrpcCarDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private CarCatalogServiceGrpc.CarCatalogServiceBlockingStub carCatalogStub;

    public List<GrpcCarDto> getAvailableCars() {
        try {
            return carCatalogStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getAvailableCars(Empty.newBuilder().build())
                    .getCarsList().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            throw handleGrpcError(e);
        }
    }

    public GrpcCarDto getCarById(String carId) {
        try {
            CarResponse response = carCatalogStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getCarById(CarIdRequest.newBuilder().setId(carId).build());
            return mapToDto(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcError(e);
        }
    }

    private RuntimeException handleGrpcError(StatusRuntimeException e) {
        if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
            return new EntityNotFoundException("Автомобиль не найден на складе");
        }
        return new StorageServiceUnavailableException("Сервис склада временно недоступен. Попробуйте позже.");
    }

    private GrpcCarDto mapToDto(CarResponse response) {
        return GrpcCarDto.builder()
                .id(response.getId())
                .brand(response.getBrand())
                .model(response.getModel())
                .basePrice(response.getBasePrice())
                .enginePower(response.getEnginePower())
                .transmissionName(response.getTransmissionName())
                .build();
    }
}