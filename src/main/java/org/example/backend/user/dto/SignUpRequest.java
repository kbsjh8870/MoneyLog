package org.example.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {
    @Email
    @NotBlank
    private final String email;

    @NotBlank
    @Size(min=8,max = 64)
    private final String password;

    @NotBlank
    private final String nickname;
}
