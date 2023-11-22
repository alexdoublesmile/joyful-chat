package org.example.joyfulchat.repository.impl;

import org.example.joyfulchat.config.ChatRoomInitializer;
import org.example.joyfulchat.entity.ChatRoom;
import org.example.joyfulchat.repository.ChatRoomRepository;

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

    @Override
    public void update(ChatRoom room) {
        chatRoomsMap.put(room.getName(), room);
    }
}
