package org.example.zeptochat.repository.impl;

import org.example.zeptochat.entity.ChatRoom;
import org.example.zeptochat.repository.ChatRoomRepository;
import org.example.zeptochat.config.ChatRoomInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class InMemoryChatRoomRepository implements ChatRoomRepository {
    public static final InMemoryChatRoomRepository INSTANCE = new InMemoryChatRoomRepository();

    private final Map<String, ChatRoom> chatRoomsMap = ChatRoomInitializer.initDefaultChatRooms();

    private InMemoryChatRoomRepository() {

    }

    @Override
    public ChatRoom findByName(String name) {
        return chatRoomsMap.get(name);
    }

    @Override
    public List<ChatRoom> findAll() {
        return new ArrayList<>(chatRoomsMap.values());
    }
}
