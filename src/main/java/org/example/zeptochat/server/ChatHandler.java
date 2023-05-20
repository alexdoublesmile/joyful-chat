package org.example.zeptochat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.zeptochat.entity.ChatRoom;
import org.example.zeptochat.entity.User;
import org.example.zeptochat.service.ChatRoomService;
import org.example.zeptochat.service.UserService;
import org.example.zeptochat.util.PasswordHelper;

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

        ctx.writeAndFlush("Welcome to ZeptoChat! Please type for sign in (or sign up):\n/login <name> <password>\n");
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
        if (currentUser.getDevices().isEmpty()) {
            broadcastLeaveMessage();
        }
        ctx.disconnect();
        isCtxConnected = false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) {
        if (isNull(currentUser)) {
            if (s.startsWith("/login ")) {
                final String[] authInfo = s.split(" ");
                if (authInfo.length != 3) {
                    ctx.writeAndFlush("Credentials format is invalid! Type correctly:\n/login <username> <password>\n");
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
                    ctx.writeAndFlush("Password is incorrect. Try another credentials, please.\n");
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
                ctx.writeAndFlush("Unknown command. Try again, please.\n");
            }
        } else {
            if (currentUser.isConnectedToAnyRoom()) {
                if (s.startsWith("/")) {
                    if (s.equals("/list")) {
                        showRooms(ctx);
                    } else if (s.startsWith("/join ")) {
                        tryJoin(ctx, s);
                    } else if (s.equals("/users")) {
                        showUsers(ctx, currentUser.getLastRoom());
                    } else if (s.equals("/leave")) {
                        broadcastLeaveMessage();
                        currentUser.leaveRoom();
                        currentUser.getDevices().forEach(this::responseOutOfRoom);
                    } else if (s.equals("/disconnect")) {
                        disconnect(ctx);
                    } else {
                        ctx.writeAndFlush("Unknown command. Try again, please.\n");
                    }
                } else {
                    broadcastMessage(currentUser.getName(), s);
                    currentUser.getLastRoom().addHistory(formatMessage(currentUser.getName(), s));
                }
            } else {
                if (s.startsWith("/")) {
                    if (s.equals("/list")) {
                        showRooms(ctx);
                    } else if (s.startsWith("/join ")) {
                        tryJoin(ctx, s);
                    } else {
                        ctx.writeAndFlush("Unknown command. Try again, please.\n");
                    }
                } else {
                    ctx.writeAndFlush("Unknown command. Try again, please.\n");
                }
            }
        }
    }

    private void tryJoin(ChannelHandlerContext ctx, String s) {
        final String[] joinInfo = s.split(" ");
        if (joinInfo.length != 2) {
            ctx.writeAndFlush("Join command format is invalid! Type correctly:\n/join <channel>\n");
            return;
        }
        final String roomName = joinInfo[1];
        final Optional<ChatRoom> maybeRoom = chatRoomService.findByName(roomName);

        if (maybeRoom.isEmpty()) {
            ctx.writeAndFlush("Channel name is incorrect. Try another channel name, please.\n");
            return;
        }
        tryConnectToRoom(ctx, maybeRoom.get());
    }

    private void showUsers(ChannelHandlerContext ctx, ChatRoom room) {
        ctx.writeAndFlush("\nCurrent users are:\n" + room.getUsers()
                .stream()
                .map(User::getName)
                .collect(joining("\n")) + "\n");
    }

    private String formatMessage(String sender, String s) {
        return format("[%s]: %s\n", sender, s);
    }

    private void showRooms(ChannelHandlerContext ctx) {
        List<ChatRoom> rooms = chatRoomService.findAll();
        ctx.writeAndFlush("\nCurrent channels are:\n" + rooms
                .stream()
                .map(room -> room.getName() + " (" + room.getUsers().size() + ")")
                .collect(joining("\n")) + "\n");
    }

    private void responseOutOfRoom(Channel channel) {
        channel.writeAndFlush("\nYou can join any channel (if it's not full):\n/list -> get list of channels\n/join <channel> -> join to the channel\n");
    }

    private void tryConnectToRoom(ChannelHandlerContext ctx, ChatRoom room) {
        if (room.isFull()) {
            ctx.writeAndFlush(format(
                    "You can't connect to channel '%s' - it's full now.\nChoose another channel, please, or try connect later\n", room.getName()));
        } else {
            if (!currentUser.isConnectedToRoom(room)) {
                if (currentUser.getLastRoom() != null) {
                    broadcastLeaveMessage();
                    currentUser.leaveRoom();
                }
                room.addUser(currentUser);
                broadcastMessage(format("%s connected to '%s'", currentUser.getName(), room.getName()));
            }

            currentUser.getDevices().forEach(channel -> channel.writeAndFlush(format(
                    "\nWelcome to channel '%s', %s! You can use commands:\n/list -> get list of channels\n/join <channel> -> join to the channel\n/users -> get list of users\n/leave -> leave current channel\n/disconnect -> leave chat\n",
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
