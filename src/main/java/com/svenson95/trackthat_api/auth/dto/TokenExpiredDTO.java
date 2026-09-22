package com.svenson95.trackthat_api.auth.dto;

public record TokenExpiredDTO(boolean valid, String error, String action) {
}
