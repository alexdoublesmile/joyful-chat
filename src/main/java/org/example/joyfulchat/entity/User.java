package org.example.joyfulchat.entity;

import io.netty.channel.Channel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Builder
@EqualsAndHashCode(of = "name")
public class User {
    private UUID id;
    private String name;
    private String password;
    private ChatRoom lastRoom;
    @Builder.Default
    private List<Channel> devices = new ArrayList<>();

    public List<Channel> getDevices() {
        return new ArrayList<>(devices);
    }

    public void addDevice(Channel device) {
        if (nonNull(device) && !devices.contains(device)) {
            devices.add(device);
        }
    }

    public void removeDevice(Channel device) {
        devices.remove(device);
    }

    public boolean isConnectedToAnyRoom() {
        return nonNull(lastRoom) && lastRoom.getUsers().contains(this);
    }

    public boolean isConnectedToRoom(ChatRoom room) {
        return room.equals(lastRoom) && lastRoom.getUsers().contains(this);
    }

    public ChatRoom leaveRoom() {
        ChatRoom abandonedRoom = lastRoom;
        if (nonNull(lastRoom)) {
            lastRoom.removeUser(this);
            lastRoom = null;
        }
        return abandonedRoom;
    }

    public void leaveServer(Channel channel) {
        removeDevice(channel);
        if (nonNull(lastRoom) && devices.isEmpty()) {
            lastRoom.removeUser(this);
        }
    }
}
