package com.svenson95.track_e_backend.auth.dto;

public record TokenExpiredDTO(boolean valid, String error, String action) {}
