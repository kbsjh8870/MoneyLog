package org.example.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.user.entity.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final Long transactionCount;

    public static UserProfileResponse from(User user, Long transactionCount){
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt(),
                transactionCount
        );
    }

}
