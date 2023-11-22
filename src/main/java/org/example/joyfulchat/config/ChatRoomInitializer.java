package org.example.joyfulchat.config;

import org.example.joyfulchat.entity.ChatRoom;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;

public final class ChatRoomInitializer {
    public static Map<String, ChatRoom> initDefaultChatRooms() {
        final HashMap<String, ChatRoom> roomsMap = new HashMap<>();
        roomsMap.put("primary", ChatRoom.builder()
                .id(randomUUID())
                .name("primary")
                .build());
        roomsMap.put("secret", ChatRoom.builder()
                .id(randomUUID())
                .name("secret")
                .build());
        roomsMap.put("common", ChatRoom.builder()
                .id(randomUUID())
                .name("common")
                .build());

        return roomsMap;
    }
}
