package org.example.backend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.user.dto.LoginRequest;
import org.example.backend.user.dto.SignUpRequest;
import org.example.backend.user.dto.SignUpResponse;
import org.example.backend.user.dto.TokenResponse;
import org.example.backend.user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
