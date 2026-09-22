package com.svenson95.trackthat_api.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;
import com.svenson95.trackthat_api.database.model.User;
import com.svenson95.trackthat_api.database.repository.UserRepository;

@Service
public class DatabaseService {

  private final UserRepository userRepository;

  public DatabaseService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<User> findByUserId(String googleId) {
    if (googleId == null || googleId.isBlank()) {
      return Optional.empty();
    }

    return userRepository.findByGoogleId(googleId);
  }

  public User findOrCreateUser(GoogleUserInfoDTO userInfo) {
    return userRepository
        .findByGoogleId(userInfo.userId())
        .orElseGet(() -> createNewUser(userInfo));
  }

  private User createNewUser(GoogleUserInfoDTO userInfo) {
    User newUser = new User();

    newUser.setGoogleId(userInfo.userId());
    newUser.setEmail(userInfo.email());
    newUser.setName(userInfo.name());
    newUser.setPicture(userInfo.picture());
    newUser.setWeight(0);
    newUser.setHeight(0);

    return userRepository.save(newUser);
  }

  public static class UserNotFoundException extends RuntimeException {
  }
}
