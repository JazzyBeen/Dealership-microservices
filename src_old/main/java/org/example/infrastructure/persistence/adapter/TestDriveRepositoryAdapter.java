package org.example.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.TestDriveRequest;
import org.example.domain.repositories.TestDriveRepository;
import org.example.infrastructure.persistence.mapper.TestDriveJpaMapper;
import org.example.infrastructure.persistence.repository.SpringDataTestDriveRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestDriveRepositoryAdapter implements TestDriveRepository {

    private final SpringDataTestDriveRepository repository;
    private final TestDriveJpaMapper mapper;

    @Override
    public void save(TestDriveRequest request) {
        repository.save(mapper.toJpa(request));
    }

    @Override
    public List<TestDriveRequest> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}