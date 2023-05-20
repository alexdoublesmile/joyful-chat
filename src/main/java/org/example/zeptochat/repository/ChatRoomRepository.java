package org.example.zeptochat.repository;

import org.example.zeptochat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository {
    ChatRoom findByName(String name);

    List<ChatRoom> findAll();
}
