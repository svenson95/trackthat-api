package com.svenson95.trackthat_api.database.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.svenson95.trackthat_api.database.dto.UserDTO;
import com.svenson95.trackthat_api.database.mapper.UserMapper;
import com.svenson95.trackthat_api.database.model.User;
import com.svenson95.trackthat_api.database.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public List<UserDTO> findAll() {
    return userRepository.findAll().stream().map(userMapper::toDto).toList();
  }

  public UserDTO createUser(UserDTO dto) {
    User user = userMapper.toEntity(dto);
    User saved = userRepository.save(user);
    return userMapper.toDto(saved);
  }
}
