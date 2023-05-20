package org.example.zeptochat.service;

import org.example.zeptochat.entity.User;
import org.example.zeptochat.repository.UserRepository;
import org.example.zeptochat.repository.impl.InMemoryUserRepository;

import java.util.Optional;

public final class UserService {
    public static final UserService INSTANCE = new UserService(InMemoryUserRepository.INSTANCE);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByName(String username) {
        return Optional.ofNullable(userRepository.findByName(username));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
