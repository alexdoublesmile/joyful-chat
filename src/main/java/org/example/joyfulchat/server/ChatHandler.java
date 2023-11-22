package org.example.joyfulchat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.joyfulchat.entity.ChatRoom;
import org.example.joyfulchat.entity.User;
import org.example.joyfulchat.service.ChatRoomService;
import org.example.joyfulchat.service.UserService;
import org.example.joyfulchat.util.PasswordHelper;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

// TODO: 18.05.2023 add logs
public class ChatHandler extends SimpleChannelInboundHandler<String> {
    private final UserService userService = UserService.INSTANCE;
    private final ChatRoomService chatRoomService = ChatRoomService.INSTANCE;

    private User currentUser;
    private boolean isCtxConnected;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        isCtxConnected = true;

        ctx.writeAndFlush("Welcome to ZeptoChat! Please type for sign in (or sign up):\r\n/login <name> <password>\r\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (isCtxConnected) {
            disconnect(ctx);
        }
        ctx.close();
    }

    private void disconnect(ChannelHandlerContext ctx) {
        currentUser.leaveServer(ctx.channel());
        chatRoomService.update(currentUser.getLastRoom());

        if (currentUser.getDevices().isEmpty()) {
            broadcastLeaveMessage();
        }
        ctx.disconnect();
        isCtxConnected = false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String incomingString) {
        // TODO: 20.05.2023 add custom exceptions and handle them
        // TODO: 20.05.2023 refactor to service handling strategies and use interfaces only
        // TODO: 20.05.2023 add all scenario testing
        String message = incomingString.trim();
        if (isNull(currentUser)) {
            if (message.startsWith("/login ")) {
                final String[] authInfo = message.split(" ");
                if (authInfo.length != 3) {
                    ctx.writeAndFlush("Credentials format is invalid! Type correctly:\r\n/login <username> <password>\r\n");
                    return;
                }
                final String username = authInfo[1];
                final String rawPassword = authInfo[2];
                final String encodedPassword = PasswordHelper.encode(rawPassword);
                User maybeUser = userService.findByName(username)
                        .orElseGet(() -> {
                            User newUser = User.builder()
                                    .name(username)
                                    .password(encodedPassword)
                                    .build();

                            return userService.save(newUser);
                        });
                if (!encodedPassword.equals(maybeUser.getPassword())) {
                    ctx.writeAndFlush("Password is incorrect. Try another credentials, please.\r\n");
                    return;
                }
                currentUser = maybeUser;

                currentUser.addDevice(ctx.channel());

                ctx.writeAndFlush(format("Welcome, %s! ", currentUser.getName()));
                if (nonNull(currentUser.getLastRoom())) {
                    tryConnectToRoom(ctx, currentUser.getLastRoom());

                } else {
                    responseOutOfRoom(ctx.channel());
                }
            } else {
                ctx.writeAndFlush("Unknown command. Try again, please.\r\n");
            }
        } else {
            if (currentUser.isConnectedToAnyRoom()) {
                if (message.startsWith("/")) {
                    if (message.equals("/list")) {
                        showRooms(ctx);
                    } else if (message.startsWith("/join ")) {
                        tryJoin(ctx, message);
                    } else if (message.equals("/users")) {
                        showUsers(ctx, currentUser.getLastRoom());
                    } else if (message.equals("/leave")) {
                        broadcastLeaveMessage();
                        final ChatRoom abandonedRoom = currentUser.leaveRoom();
                        userService.update(currentUser);
                        chatRoomService.update(abandonedRoom);

                        currentUser.getDevices().forEach(this::responseOutOfRoom);
                    } else if (message.equals("/disconnect")) {
                        disconnect(ctx);
                    } else {
                        ctx.writeAndFlush("Unknown command. Try again, please.\r\n");
                    }
                } else {
                    broadcastMessage(currentUser.getName(), message);
                    currentUser.getLastRoom().addHistory(formatMessage(currentUser.getName(), message));
                    chatRoomService.update(currentUser.getLastRoom());
                }
            } else {
                if (message.startsWith("/")) {
                    if (message.equals("/list")) {
                        showRooms(ctx);
                    } else if (message.startsWith("/join ")) {
                        tryJoin(ctx, message);
                    } else {
                        ctx.writeAndFlush("Unknown command. Try again, please.\r\n");
                    }
                } else {
                    ctx.writeAndFlush("Unknown command. Try again, please.\r\n");
                }
            }
        }
    }

    private void tryJoin(ChannelHandlerContext ctx, String s) {
        final String[] joinInfo = s.split(" ");
        if (joinInfo.length != 2) {
            ctx.writeAndFlush("Join command format is invalid! Type correctly:\r\n/join <channel>\r\n");
            return;
        }
        final String roomName = joinInfo[1];
        final Optional<ChatRoom> maybeRoom = chatRoomService.findByName(roomName);

        if (maybeRoom.isEmpty()) {
            ctx.writeAndFlush("Channel name is incorrect. Try another channel name, please.\r\n");
            return;
        }
        tryConnectToRoom(ctx, maybeRoom.get());
    }

    private void showUsers(ChannelHandlerContext ctx, ChatRoom room) {
        ctx.writeAndFlush("\r\nCurrent users are:\r\n" + room.getUsers()
                .stream()
                .map(User::getName)
                .collect(joining("\r\n")) + "\r\n");
    }

    private String formatMessage(String sender, String s) {
        return format("[%s]: %s\r\n", sender, s);
    }

    private void showRooms(ChannelHandlerContext ctx) {
        List<ChatRoom> rooms = chatRoomService.findAll();
        ctx.writeAndFlush("\r\nCurrent channels are:\r\n" + rooms
                .stream()
                .map(room -> room.getName() + " (" + room.getUsers().size() + ")")
                .collect(joining("\r\n")) + "\r\n");
    }

    private void responseOutOfRoom(Channel channel) {
        channel.writeAndFlush("\r\nYou can join any channel (if it's not full):\r\n/list -> get list of channels\r\n/join <channel> -> join to the channel\r\n");
    }

    private void tryConnectToRoom(ChannelHandlerContext ctx, ChatRoom room) {
        if (room.isFull() & !currentUser.isConnectedToRoom(room)) {
            ctx.writeAndFlush(format(
                    "You can't connect to channel '%s' - it's full now.\r\nChoose another channel, please, or try connect later\r\n", room.getName()));
        } else {
            if (!currentUser.isConnectedToRoom(room)) {
                if (currentUser.getLastRoom() != null) {
                    broadcastLeaveMessage();
                    currentUser.leaveRoom();
                    userService.update(currentUser);
                }
                room.addUser(currentUser);
                chatRoomService.update(room);

                broadcastMessage(format("%s connected to '%s'", currentUser.getName(), room.getName()));
            }

            currentUser.getDevices().forEach(channel -> channel.writeAndFlush(format(
                    "\r\nWelcome to channel '%s', %s! You can use commands:\r\n/list -> get list of channels\r\n/join <channel> -> join to the channel\r\n/users -> get list of users\r\n/leave -> leave current channel\r\n/disconnect -> leave chat\r\n",
                    room.getName(), currentUser.getName())));
            currentUser.getDevices().forEach(channel -> channel.writeAndFlush(join("", room.getHistory())));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.disconnect();

        // TODO: 19.05.2023 handle
        cause.printStackTrace();
    }

    private void broadcastMessage(String message) {
        broadcastMessage("ZeptoChat", message);
    }

    private void broadcastMessage(String sender, String message) {
        String out = formatMessage(sender, message);
        currentUser.getLastRoom().getUsers().forEach(
                user -> user.getDevices().forEach(
                        channel -> channel.writeAndFlush(out)));
    }

    private void broadcastLeaveMessage() {
        broadcastMessage(format("%s is disconnecting from '%s'...", currentUser.getName(), currentUser.getLastRoom().getName()));
    }
}
