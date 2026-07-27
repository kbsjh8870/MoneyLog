package org.example.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReissueRequest {
    @NotBlank
    private final String refreshToken;
}
