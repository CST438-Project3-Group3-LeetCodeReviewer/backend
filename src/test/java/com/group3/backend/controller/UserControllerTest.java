package com.group3.backend.controller;

import com.group3.backend.dto.UpdateUserProfileRequest;
import com.group3.backend.entity.User;
import com.group3.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserRepository userRepository;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userController = new UserController(userRepository);
    }

    @Test
    void testGetUserProfile() {
        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "mauricio@example.com",
                "Mauricio Reynoso",
                "github",
                OffsetDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserProfile(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mauricio@example.com", response.getBody().getEmail());
        assertEquals("Mauricio Reynoso", response.getBody().getFullName());
        assertEquals("github", response.getBody().getOauthProvider());
    }

    @Test
    void testGetUserProfileNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserProfile(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateUserProfile() {
        UUID userId = UUID.randomUUID();

        User existingUser = new User(
                userId,
                "mauricio@example.com",
                "Old Name",
                "github",
                OffsetDateTime.now()
        );

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<User> response = userController.updateUserProfile(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Name", response.getBody().getFullName());
        assertEquals("mauricio@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateUserProfileNotFound() {
        UUID userId = UUID.randomUUID();

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.updateUserProfile(userId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateUserProfileBadRequest() {
        UUID userId = UUID.randomUUID();

        User existingUser = new User(
                userId,
                "mauricio@example.com",
                "Old Name",
                "github",
                OffsetDateTime.now()
        );

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("   ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ResponseEntity<User> response = userController.updateUserProfile(userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserAccount() {
        UUID userId = UUID.randomUUID();

        User existingUser = new User(
                userId,
                "mauricio@example.com",
                "Mauricio Reynoso",
                "github",
                OffsetDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ResponseEntity<Void> response = userController.deleteUserAccount(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUserAccountNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = userController.deleteUserAccount(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, never()).deleteById(any(UUID.class));
    }
}