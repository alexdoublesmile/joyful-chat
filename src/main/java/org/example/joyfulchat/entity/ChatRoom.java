package org.example.joyfulchat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.joyfulchat.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.nonNull;
import static org.example.joyfulchat.util.PropertiesConstants.HISTORY_SIZE_PROPERTY;
import static org.example.joyfulchat.util.PropertiesConstants.ROOM_SIZE_PROPERTY;

@Data
@Builder
@EqualsAndHashCode(of = "name")
@ToString(exclude = {"users"})
public class ChatRoom {
    private UUID id;
    private String name;
    @Builder.Default
    private List<User> users = new ArrayList<>();
    @Builder.Default
    private LinkedBlockingQueue<String> history = new LinkedBlockingQueue<>(PropertiesUtil.getInt(HISTORY_SIZE_PROPERTY));

    public boolean isFull() {
        return users.size() >= PropertiesUtil.getInt(ROOM_SIZE_PROPERTY);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        if (nonNull(user) && !users.contains(user)) {
            users.add(user);
            user.setLastRoom(this);
        }
    }

    public void removeUser(User user) {
        users.remove(user);
    }
    public LinkedBlockingQueue<String> getHistory() {
        return new LinkedBlockingQueue<>(history);
    }

    public void addHistory(String message) {
        if (!history.offer(message)) {
            history.poll();
            history.offer(message);
        }
    }
}
