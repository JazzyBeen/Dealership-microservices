package org.example.infrastructure.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.application.usecase.CarQueryService;
import org.example.domain.entity.Car;
import org.example.grpc.car.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class CarCatalogGrpcService extends CarCatalogServiceGrpc.CarCatalogServiceImplBase {

    private final CarQueryService carQueryService;

    @Override
    public void getAvailableCars(Empty request, StreamObserver<CarListResponse> responseObserver) {
        List<Car> cars = carQueryService.getAllCars();

        List<CarResponse> grpcCars = cars.stream()
                .map(this::mapToGrpcResponse)
                .collect(Collectors.toList());

        CarListResponse response = CarListResponse.newBuilder()
                .addAllCars(grpcCars)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCarById(CarIdRequest request, StreamObserver<CarResponse> responseObserver) {
        try {
            Car car = carQueryService.getCarById(UUID.fromString(request.getId()));
            responseObserver.onNext(mapToGrpcResponse(car));
            responseObserver.onCompleted();
        } catch (org.example.domain.exception.EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Машина не найдена на складе")
                    .asRuntimeException());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Неверный формат UUID")
                    .asRuntimeException());
        }
    }

    private CarResponse mapToGrpcResponse(Car car) {
        return CarResponse.newBuilder()
                .setId(car.getId().toString())
                .setBrand(car.getBrand())
                .setModel(car.getModel())
                .setBasePrice(car.getBasePrice() != null ? car.getBasePrice().toString() : "0")
                .setEnginePower(car.getEnginePower() != null ? car.getEnginePower() : 0)
                .setTransmissionName(car.getTransmission() != null ? car.getTransmission().getName() : "Unknown")
                .build();
    }
}