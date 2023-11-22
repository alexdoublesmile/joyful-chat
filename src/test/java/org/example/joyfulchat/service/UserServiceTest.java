package org.example.joyfulchat.service;

import org.example.joyfulchat.entity.User;
import org.example.joyfulchat.repository.UserRepository;
import org.example.joyfulchat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final User IVAN = User.builder().name("Ivan").password("Ivan123").build();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void userReturnsByNameIfExists() {
        doReturn(IVAN).when(userRepository).findByName(IVAN.getName());

        final User actualUser = userService.findByName(IVAN.getName()).orElseThrow();

        verify(userRepository, times(1)).findByName(IVAN.getName());
        assertThat(actualUser).isEqualTo(IVAN);
    }

    @Test
    void userDoesntReturnByNameIfNotExists() {
        doReturn(null).when(userRepository).findByName(IVAN.getName());

        final Optional<User> maybeUser = userService.findByName(IVAN.getName());

        verify(userRepository, times(1)).findByName(IVAN.getName());
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void userReturnsIfSaveSuccess() {
        doReturn(IVAN).when(userRepository).save(IVAN);

        final User actualUser = userService.save(IVAN);

        verify(userRepository, times(1)).save(IVAN);
        assertThat(actualUser).isEqualTo(IVAN);
    }

    @Test
    void userUpdatesIfUpdateSuccess() {
        userService.update(IVAN);

        verify(userRepository, times(1)).update(IVAN);
    }
}