package com.svenson95.trackthat_api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;
import com.svenson95.trackthat_api.database.model.User;
import com.svenson95.trackthat_api.database.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth database service")
class DatabaseServiceTest {

    @Mock
    private UserRepository userRepository;

    private DatabaseService databaseService;

    @BeforeEach
    void setUp() {
        databaseService = new DatabaseService(userRepository);
    }

    @Test
    @DisplayName("finds a user by Google ID")
    void findsUserByGoogleId() {
        User user = new User();
        user.setGoogleId("google-123");

        when(userRepository.findByGoogleId("google-123"))
                .thenReturn(Optional.of(user));

        Optional<User> result = databaseService.findByUserId("google-123");

        assertThat(result).containsSame(user);

        verify(userRepository).findByGoogleId("google-123");
    }

    @Test
    @DisplayName("does not query the repository for a missing Google ID")
    void doesNotQueryRepositoryForMissingGoogleId() {
        Optional<User> result = databaseService.findByUserId(null);

        assertThat(result).isEmpty();

        verify(userRepository, never())
                .findByGoogleId(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("does not query the repository for a blank Google ID")
    void doesNotQueryRepositoryForBlankGoogleId() {
        Optional<User> result = databaseService.findByUserId(" ");

        assertThat(result).isEmpty();

        verify(userRepository, never())
                .findByGoogleId(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("returns an existing user")
    void returnsExistingUser() {
        GoogleUserInfoDTO userInfo = new GoogleUserInfoDTO(
                "google-123",
                "user@example.com",
                "Test User",
                "picture");

        User existingUser = new User();
        existingUser.setGoogleId("google-123");

        when(userRepository.findByGoogleId("google-123"))
                .thenReturn(Optional.of(existingUser));

        User result = databaseService.findOrCreateUser(userInfo);

        assertThat(result).isSameAs(existingUser);

        verify(userRepository, never())
                .save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("creates a user when none exists")
    void createsUserWhenNoneExists() {
        GoogleUserInfoDTO userInfo = new GoogleUserInfoDTO(
                "google-123",
                "user@example.com",
                "Test User",
                "https://example.com/picture.jpg");

        when(userRepository.findByGoogleId("google-123"))
                .thenReturn(Optional.empty());

        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = databaseService.findOrCreateUser(userInfo);

        assertThat(result.getGoogleId()).isEqualTo("google-123");
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getPicture())
                .isEqualTo("https://example.com/picture.jpg");
        assertThat(result.getWeight()).isEqualTo(0);
        assertThat(result.getHeight()).isEqualTo(0);

        verify(userRepository).save(result);
    }
}