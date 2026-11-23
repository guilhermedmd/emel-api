package br.edu.ifps.emel_condominios_api.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
