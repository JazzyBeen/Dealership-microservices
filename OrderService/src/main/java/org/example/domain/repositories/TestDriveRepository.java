package org.example.domain.repositories;

import org.example.domain.entity.TestDriveRequest;
import java.util.List;

public interface TestDriveRepository {
    void save(TestDriveRequest request);
    List<TestDriveRequest> findAll();
}