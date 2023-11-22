package org.example.joyfulchat.repository;

import org.example.joyfulchat.entity.User;

public interface UserRepository {
    User findByName(String name);

    User save(User user);

    void update(User user);
}
