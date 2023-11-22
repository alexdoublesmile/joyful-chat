package org.example.joyfulchat.service;

import org.example.joyfulchat.entity.ChatRoom;
import org.example.joyfulchat.repository.ChatRoomRepository;
import org.example.joyfulchat.repository.impl.InMemoryChatRoomRepository;

import java.util.List;
import java.util.Optional;

public final class ChatRoomService {
    public static final ChatRoomService INSTANCE = new ChatRoomService(InMemoryChatRoomRepository.INSTANCE);

    private final ChatRoomRepository chatRoomRepository;

    private ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public Optional<ChatRoom> findByName(String name) {
        return Optional.ofNullable(chatRoomRepository.findByName(name));
    }

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public void update(ChatRoom room) {
        chatRoomRepository.update(room);
    }
}
