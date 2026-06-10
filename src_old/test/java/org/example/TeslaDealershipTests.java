package org.example;

import org.example.application.usecase.CarManagementUseCase;
import org.example.application.usecase.CarQueryService;
import org.example.application.usecase.OrderRegistrationUseCase;
import org.example.data.repositories.InMemoryCarRepository;
import org.example.data.repositories.InMemoryOrderRepository;
import org.example.domain.entity.*;
import org.example.domain.exception.DomainValidationException;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.exception.IncompatibleComponentException;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.CarRepository;
import org.example.domain.repositories.OrderRepository;
import org.example.domain.service.CarConfiguratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeslaDealershipTests {

    private CarRepository carRepository;
    private OrderRepository orderRepository;
    private CarConfiguratorService configuratorService;
    private CarQueryService queryService;
    private OrderRegistrationUseCase orderUseCase;
    private CarManagementUseCase managementUseCase;

    private User sysAdmin;
    private User warehouseAdmin;
    private User manager;
    private User client;

    private CarPart aeroWheels;
    private CarPart arachnidWheels;
    private CarPart cyberWheels;
    private CarPart blackInterior;
    private CarPart whiteInterior;
    private CarPart yokeSteering;
    private CarPart singleSpeedTransmission;

    @BeforeEach
    void setUp() {
        carRepository = new InMemoryCarRepository();
        orderRepository = new InMemoryOrderRepository();
        configuratorService = new CarConfiguratorService();
        queryService = new CarQueryService(carRepository);
        managementUseCase = new CarManagementUseCase(carRepository);
        orderUseCase = new OrderRegistrationUseCase(orderRepository, configuratorService);

        sysAdmin = new User(1, "admin", UserRole.SYSTEM_ADMIN);
        warehouseAdmin = new User(2, "warehouse", UserRole.WAREHOUSE_ADMIN);
        manager = new User(3, "manager", UserRole.MANAGER);
        client = new User(4, "elon_fan", UserRole.CLIENT);

        aeroWheels = new CarPart("18\" Aero", BigDecimal.ZERO, Arrays.asList("Model 3", "Model Y"));
        arachnidWheels = new CarPart("21\" Arachnid", new BigDecimal("450000"), Arrays.asList("Model S", "Model X"));
        cyberWheels = new CarPart("20\" Cyber", BigDecimal.ZERO, Arrays.asList("Cybertruck"));

        blackInterior = new CarPart("All Black", BigDecimal.ZERO, Arrays.asList("Model 3", "Model Y", "Model S", "Model X", "Cybertruck"));
        whiteInterior = new CarPart("Black and White", new BigDecimal("150000"), Arrays.asList("Model 3", "Model Y", "Model S", "Model X"));

        yokeSteering = new CarPart("Yoke Steering", new BigDecimal("100000"), Arrays.asList("Model S", "Model X"));

        singleSpeedTransmission = new CarPart("Single Speed", BigDecimal.ZERO, Arrays.asList("Model 3", "Model Y", "Model S", "Model X", "Cybertruck"));

        Car model3 = new Car(1, "Tesla", "Model 3", "Sedan", Car.FuelType.ELECTRICITY, aeroWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, blackInterior, 283, 60, Car.CarDrive.REAR, "White", new BigDecimal("4000000"));
        Car modelS = new Car(2, "Tesla", "Model S", "Liftback", Car.FuelType.ELECTRICITY, arachnidWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, whiteInterior, 1020, 100, Car.CarDrive.FULL, "Red", new BigDecimal("10000000"));
        Car cybertruck = new Car(3, "Tesla", "Cybertruck", "Pickup", Car.FuelType.ELECTRICITY, cyberWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, blackInterior, 845, 123, Car.CarDrive.FULL, "Silver", new BigDecimal("12000000"));

        carRepository.save(model3);
        carRepository.save(modelS);
        carRepository.save(cybertruck);
    }

    @Test
    void testSystemAdminCanAddCar() {
        Car modelY = new Car(4, "Tesla", "Model Y", "Crossover", Car.FuelType.ELECTRICITY, aeroWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, blackInterior, 384, 75, Car.CarDrive.FULL, "Blue", new BigDecimal("5500000"));
        assertDoesNotThrow(() -> managementUseCase.addCar(sysAdmin, modelY));
        assertEquals(4, queryService.getAllCars().size());
    }

    @Test
    void testWarehouseAdminCanAddCar() {
        Car modelX = new Car(5, "Tesla", "Model X", "SUV", Car.FuelType.ELECTRICITY, arachnidWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, blackInterior, 1020, 100, Car.CarDrive.FULL, "Black", new BigDecimal("11000000"));
        assertDoesNotThrow(() -> managementUseCase.addCar(warehouseAdmin, modelX));
    }

    @Test
    void testClientCannotAddCar() {
        Car fakeTesla = new Car(6, "Tesla", "Model Z", "Sedan", Car.FuelType.ELECTRICITY, aeroWheels, singleSpeedTransmission, Car.TransmissionType.AUTOMATIC, null, blackInterior, 100, 50, Car.CarDrive.FRONT, "Pink", new BigDecimal("1000000"));
        assertThrows(UnauthorizedAccessException.class, () -> managementUseCase.addCar(client, fakeTesla));
    }

    @Test
    void testManagerCannotDeleteCar() {
        Car carToDelete = queryService.getCarById(1);
        assertThrows(UnauthorizedAccessException.class, () -> managementUseCase.deleteCar(manager, carToDelete));
    }

    @Test
    void testSystemAdminCanDeleteCar() {
        Car carToDelete = queryService.getCarById(1);
        assertDoesNotThrow(() -> managementUseCase.deleteCar(sysAdmin, carToDelete));

        assertThrows(EntityNotFoundException.class, () -> queryService.getCarById(1));
    }

    @Test
    void testFilterCarsByModel() {
        List<Car> cars = queryService.filterCars(null, "Pickup", null);
        assertEquals(1, cars.size());
        assertEquals("Cybertruck", cars.get(0).getModel());
    }

    @Test
    void testFilterCarsByPower() {
        List<Car> cars = queryService.filterCars(null, null, 1000);
        assertEquals(1, cars.size());
        assertEquals("Model S", cars.get(0).getModel());
    }

    @Test
    void testCheckCompatibilityValid() {
        Car modelS = queryService.getCarById(2);
        assertDoesNotThrow(() -> configuratorService.checkCompatibility(modelS, yokeSteering));
    }

    @Test
    void testCheckCompatibilityInvalidThrowsException() {
        Car model3 = queryService.getCarById(1);
        assertThrows(IncompatibleComponentException.class, () -> configuratorService.checkCompatibility(model3, arachnidWheels));
    }

    @Test
    void testValidateConfigurationMissingPartThrowsException() {
        Car model3 = queryService.getCarById(1);
        model3.setInterior(null);
        assertThrows(DomainValidationException.class, () -> configuratorService.validateConfiguration(model3));
    }


    @Test
    void testCreateInStockOrder() {
        Car cybertruck = queryService.getCarById(3);
        InStockOrder order = orderUseCase.createInStockOrder(client, manager, cybertruck);

        assertNotNull(order);
        assertEquals(cybertruck.getBasePrice(), order.getPrice());
        assertEquals(1, orderRepository.findAll().size());
    }

}