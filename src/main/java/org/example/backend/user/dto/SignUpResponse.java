package org.example.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.user.entity.User;

@Getter
@AllArgsConstructor
public class SignUpResponse {
    private final Long userId;
    private final String email;
    private final String nickname;

    public static SignUpResponse from(User user){
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
