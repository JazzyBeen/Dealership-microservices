package org.example.data.repositories;

import org.example.domain.entity.User;
import org.example.domain.repositories.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> storage = new HashMap<>();

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        storage.put(user.getId(), user);
    }

    @Override
    public User findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        storage.remove(id);
    }
}