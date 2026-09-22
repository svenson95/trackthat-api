package com.svenson95.trackthat_api.auth.dto;

import com.svenson95.trackthat_api.database.model.User;

public record AuthDTO(String token, User user) {
}
