package org.example.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.User;
import org.example.domain.repositories.UserRepository;
import org.example.infrastructure.persistence.mapper.UserJpaMapper;
import org.example.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserJpaMapper mapper;

    @Override
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public void save(User user) {
        repository.save(mapper.toJpa(user));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}