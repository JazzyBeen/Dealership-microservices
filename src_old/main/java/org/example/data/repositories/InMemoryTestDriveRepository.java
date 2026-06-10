package org.example.data.repositories;

import org.example.domain.entity.TestDriveRequest;
import org.example.domain.repositories.TestDriveRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryTestDriveRepository implements TestDriveRepository {
    private final Map<UUID, TestDriveRequest> storage = new HashMap<>();

    @Override
    public void save(TestDriveRequest request) {
        if (request.getId() == null) {
            request.setId(UUID.randomUUID());
        }
        storage.put(request.getId(), request);
    }

    @Override
    public List<TestDriveRequest> findAll() {
        return new ArrayList<>(storage.values());
    }
}