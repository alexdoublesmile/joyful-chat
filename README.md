# ZeptoChat - A Java simple chat based on Netty framework that allows users to communicate with each other in real time (v.0.0.1 - MVP/PoC)
                     
- Our chat supports multiple channels for users communications
- For communication user should sign in by unique name and password
- If user is new, his unique name and encrypted password will be saving to system (while the server is going only, because no persistence in MVP)
- Logged in user can use out of channel commands for getting channels list (with current online users number) or joining to any channel
- User can only join one channel at a time
- User can at the same time connect from any number of devices
- Any attempt to join full channel (manually or auto) will return error message to user (if user is not in this channel already by another device)
- Everybody from the channel gets system message about users connection
- User joined to channel gets all users messages history of current channel (according to the history limit)
- User joined to channel can use as well join and list commands and several more commands (get number of current users, leave the current channel or disconnect from the server)
- User joined to channel and trying to log in from another device by his credentials, will be automatically redirect to his joined channel (even if this channel is full)
- All user messages are public and sent to all users from current channel
- All commands are private, only current user can see them in current device
- When user joins the other channel he automatically leaves current channel
- When user joins/leaves channel, he joins/leaves it from all his devices at one time
- When user disconnects from the server, he disconnects only from the current device but still online in current channel by his other connected devices
- When user disconnects from all devices or leaves channel everybody from this channel gets message about his disconnection
- When user is not new and logged in he is automatically redirected to his channel (last channel he was disconnected from but not left it)

## Available Commands:

|Command|Action|
|---|---|
|/login [name] [password]|sign up or try to sign in if exists
|/list|get list of channels
|/join [channel]|try to join a channel
|/users|get list of unique users in current channel
|/leave|leave channel
|/disconnect|leave server

## Compiling from source

ZeptoChat uses [Apache Maven](https://maven.apache.org/) v3.5 or higher to build.
ZeptoChat is compatible with Java 8+, and we test on LTS versions of Java along
with the latest release. To build, simply run `mvn clean package`. All ZeptoChat's
dependencies will be available on Maven Central soon. Please file an issue for
build-related issues if you're having trouble (though do check if you're
missing proxy settings for Maven first, as that's a common cause of build
failures, and out of our control).

## Running

### Get started

To get started with the project, you will need to have the following installed:
- `Java 8+`

Once you have the necessary software installed, you can clone the project from GitHub:
```
git clone https://github.com/alexdoublesmile/zeptochat.git
```
To run the project, open the project in your IDE and run the Launcher class, or you can use terminal. 

### Environment variables

While running, you can set some environment variables for additional chat server config:

|Env name|Description|Default values|
|---|---|---|
|SERVER_HOST|server host value|localhost
|SERVER_PORT|server port value|8080
|SERVER_BACKLOG|the number of connections that can be queued up before a server refuses new connections|512
|KEEP_ALIVE|send a periodic probe to the other end of the connection to ensure that the connection is still alive. If the other end of the connection does not respond to the probe, the connection is considered to be dead and can be closed|true
|ROOM_SIZE|users limit for every channel|10
|HISTORY_SIZE|last messages limit for history|20

### Usage

Once the application is running, you can connect to the server by any `client` (telnet, desktop, web client, etc.) by properly `host` and `port`.

Then create new account or log in with an existing account. Once you are logged in, you can join an existing chat room. 

### Troubleshooting

If you encounter any problems running the project, please check the following:
- Make sure you have the `Java 8+` installed.
- Make sure you are running the project from the `root` directory of the project.
- Make sure you connected to the server by properly `host` and `port`.
- If you are still having problems, please open an issue on GitHub.

## Contributing

If you would like to contribute to the project, please fork the project on GitHub and submit a pull request.

## License

ZeptoChat is licensed under the [Apache 2.0 License](./LICENSE.txt).

## Release Notes:

- v1.0.0 - Initial internal release. This release only supports basic functionality
