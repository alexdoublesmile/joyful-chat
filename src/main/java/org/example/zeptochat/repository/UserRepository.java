package org.example.zeptochat.repository;

import org.example.zeptochat.entity.User;

public interface UserRepository {
    User findByName(String name);

    User save(User user);
}
