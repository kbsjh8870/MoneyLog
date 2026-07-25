package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.category.service.CategoryService;
import org.example.backend.common.exception.CustomException;
import org.example.backend.common.exception.ErrorCode;
import org.example.backend.user.dto.LoginRequest;
import org.example.backend.user.dto.SignUpRequest;
import org.example.backend.user.dto.SignUpResponse;
import org.example.backend.user.dto.TokenResponse;
import org.example.backend.security.jwt.JwtProvider;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;

    // 회원가입
    @Transactional
    public SignUpResponse signUp(SignUpRequest request){

        if(userRepository.existsByEmail(request.getEmail()))
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE," 사용중인 이메일 입니다 - "+ request.getEmail());

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        userRepository.save(user);

        categoryService.seedDefaultCategories(user);

        return SignUpResponse.from(user);
    }

    // 로그인 - 성공 시 토큰 반환
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);

        String token = jwtProvider.createToken(user.getId(), user.getEmail());

        return TokenResponse.of(token, user.getNickname());
    }
}
