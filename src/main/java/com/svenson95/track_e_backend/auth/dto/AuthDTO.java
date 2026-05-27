package com.svenson95.track_e_backend.auth.dto;

import com.svenson95.track_e_backend.database.model.User;

public record AuthDTO(String token, User user) {}
