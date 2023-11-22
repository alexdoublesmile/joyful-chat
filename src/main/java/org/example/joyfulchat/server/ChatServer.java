package org.example.joyfulchat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.joyfulchat.util.PropertiesUtil;

import java.net.InetSocketAddress;

import static org.example.joyfulchat.util.PropertiesConstants.*;

// TODO: 18.05.2023 add logger
// TODO: 18.05.2023 add exception handling
public class ChatServer {
    private final String host;
    private final Integer port;

    public ChatServer(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, PropertiesUtil.getInt(SERVER_BACKLOG_PROPERTY))
                    .childOption(ChannelOption.SO_KEEPALIVE, PropertiesUtil.getBoolean(CLIENT_KEEPALIVE_PROPERTY));

            final ChannelFuture future = bootstrap.bind(
                    new InetSocketAddress(host, port)).sync();
            System.out.println("[SYSTEM] - Server started at port " + port + "...");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("[SYSTEM] - Event loops closed successfully.");
        }
    }
}
