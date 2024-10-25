package com.av2dac.services;

import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByEmail_UserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void findByEmail_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void saveUser_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);  // Mockando o retorno do getId

        doNothing().when(userRepository).delete(user);

        // Act
        userService.deleteUser(user);

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

}
