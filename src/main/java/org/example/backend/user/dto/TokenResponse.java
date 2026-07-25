package org.example.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String tokenType;
    private final String nickname;

    public static TokenResponse of(String accessToken, String nickname){
        return new TokenResponse(accessToken,"Bearer",nickname);
    }
}
