package org.example.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPWRequest {
    private final String currentPassword;
    private final String newPassword;
}
