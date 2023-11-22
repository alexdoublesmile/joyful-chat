package org.example.zeptochat.service;

import org.example.joyfulchat.entity.ChatRoom;
import org.example.joyfulchat.repository.ChatRoomRepository;
import org.example.joyfulchat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    private static final ChatRoom SECRET_ROOM = ChatRoom.builder().name("secret").build();
    private static final ChatRoom PLAIN_ROOM = ChatRoom.builder().name("plain").build();

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void roomIsReturnedByNameIfExists() {
        doReturn(SECRET_ROOM).when(chatRoomRepository).findByName(SECRET_ROOM.getName());

        final ChatRoom actualRoom = chatRoomService.findByName(SECRET_ROOM.getName()).orElseThrow();

        verify(chatRoomRepository, times(1)).findByName(SECRET_ROOM.getName());
        assertThat(actualRoom).isEqualTo(SECRET_ROOM);
    }

    @Test
    void roomIsNotReturnedByNameIfNotExists() {
        doReturn(null).when(chatRoomRepository).findByName(SECRET_ROOM.getName());

        final Optional<ChatRoom> maybeRoom = chatRoomService.findByName(SECRET_ROOM.getName());

        verify(chatRoomRepository, times(1)).findByName(SECRET_ROOM.getName());
        assertThat(maybeRoom).isEmpty();
    }

    @Test
    void roomListIsReturnedIfExists() {
        doReturn(List.of(SECRET_ROOM, PLAIN_ROOM)).when(chatRoomRepository).findAll();

        final List<ChatRoom> actualRoomList = chatRoomService.findAll();

        verify(chatRoomRepository, times(1)).findAll();
        assertThat(actualRoomList)
                .isNotEmpty()
                .hasSize(2)
                .contains(SECRET_ROOM)
                .contains(PLAIN_ROOM);
    }

    @Test
    void roomUpdatesIfUpdateSuccess() {
        chatRoomService.update(PLAIN_ROOM);

        verify(chatRoomRepository, times(1)).update(PLAIN_ROOM);
    }
}