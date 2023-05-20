package org.example.zeptochat;

import org.example.zeptochat.server.ChatServer;
import org.example.zeptochat.util.PropertiesUtil;

import static org.example.zeptochat.util.PropertiesConstants.SERVER_HOST_PROPERTY;
import static org.example.zeptochat.util.PropertiesConstants.SERVER_PORT_PROPERTY;

public class Launcher {

    public static void main(String[] args) {
        new ChatServer(
                PropertiesUtil.get(SERVER_HOST_PROPERTY),
                PropertiesUtil.getInt(SERVER_PORT_PROPERTY)
        ).run();
    }
}
