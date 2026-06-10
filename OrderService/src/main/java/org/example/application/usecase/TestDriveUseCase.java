package org.example.application.usecase;

import org.example.domain.entity.TestDriveRequest;
import org.example.domain.entity.User;
import org.example.domain.entity.UserRole;
import org.example.domain.exception.DomainValidationException;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.TestDriveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TestDriveUseCase {
    private final TestDriveRepository testDriveRepository;
    private final Set<UUID> availableForTestDriveCarIds;

    public TestDriveUseCase(TestDriveRepository testDriveRepository) {
        this.testDriveRepository = testDriveRepository;
        this.availableForTestDriveCarIds = new HashSet<>();
    }

    public void addCarToTestDrive(User initiator, UUID carId) {
        if (initiator.getRole() != UserRole.MANAGER && initiator.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new UnauthorizedAccessException("Нет прав");
        }
        availableForTestDriveCarIds.add(carId);
    }

    public void removeCarFromTestDrive(User initiator, UUID carId) {
        if (initiator.getRole() != UserRole.MANAGER && initiator.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new UnauthorizedAccessException("Нет прав");
        }
        availableForTestDriveCarIds.remove(carId);
    }

    public TestDriveRequest bookTestDrive(User client, UUID carId, LocalDateTime dateTime) {
        if (client.getRole() != UserRole.CLIENT) {
            throw new UnauthorizedAccessException("Нет прав");
        }
        if (!availableForTestDriveCarIds.contains(carId)) {
            throw new DomainValidationException("Машина недоступна для тест-драйва");
        }
        TestDriveRequest request = new TestDriveRequest(null, client, carId, dateTime);
        testDriveRepository.save(request);
        return request;
    }

    public List<TestDriveRequest> getAllRequests(User initiator) {
        if (initiator.getRole() != UserRole.MANAGER && initiator.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new UnauthorizedAccessException("Нет прав");
        }
        return testDriveRepository.findAll();
    }
}