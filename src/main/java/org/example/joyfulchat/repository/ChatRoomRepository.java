package org.example.joyfulchat.repository;

import org.example.joyfulchat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository {
    ChatRoom findByName(String name);

    List<ChatRoom> findAll();

    void update(ChatRoom room);
}
