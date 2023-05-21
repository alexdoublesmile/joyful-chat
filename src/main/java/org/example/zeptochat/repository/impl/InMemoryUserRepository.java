package org.example.zeptochat.repository.impl;

import org.example.zeptochat.entity.User;
import org.example.zeptochat.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;

public final class InMemoryUserRepository implements UserRepository {
    public static final InMemoryUserRepository INSTANCE = new InMemoryUserRepository();

    private final Map<String, User> usersMap = new HashMap<>();

    private InMemoryUserRepository() {

    }

    @Override
    public User findByName(String name) {
        return usersMap.get(name);
    }

    @Override
    public User save(User user) {
        user.setId(randomUUID());
        usersMap.put(user.getName(), user);
        return user;
    }

    @Override
    public void update(User user) {
        usersMap.put(user.getName(), user);
    }
}
