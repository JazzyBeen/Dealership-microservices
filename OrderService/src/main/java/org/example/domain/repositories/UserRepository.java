package org.example.domain.repositories;

import org.example.domain.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    List<User> findAll();
    User findByUsername(String username);
    void save(User user);
    void delete(UUID id);
}
