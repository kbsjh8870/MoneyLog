package org.example.backend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.user.dto.*;
import org.example.backend.user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@Valid @RequestBody SignUpRequest request){
        SignUpResponse response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입 성공",response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request){
        TokenResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success("로그인 성공",response)); // accessToken, Bearer, nickname // 프론트단에서 헤더에 토큰 실어야함.

    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody ReissueRequest request){
        TokenResponse response = authService.reissue(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("재발급 성공",response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        authService.logout(customUserDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공",null));
    }
}
