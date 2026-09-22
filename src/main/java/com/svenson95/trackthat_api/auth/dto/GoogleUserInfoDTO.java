package com.svenson95.trackthat_api.auth.dto;

public record GoogleUserInfoDTO(String userId, String email, String name, String picture) {
}
