package org.example.backend.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.backend.category.service.CategoryService;
import org.example.backend.common.exception.CustomException;
import org.example.backend.common.exception.ErrorCode;
import org.example.backend.user.dto.LoginRequest;
import org.example.backend.user.dto.SignUpRequest;
import org.example.backend.user.dto.SignUpResponse;
import org.example.backend.user.dto.TokenResponse;
import org.example.backend.security.jwt.JwtProvider;
import org.example.backend.user.entity.RefreshToken;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.RefreshRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final RefreshRepository refreshRepository;

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
    @Transactional
    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshRepository.deleteByUserId(user.getId());

        refreshRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtProvider.getRefreshTokenExpirationMs()/1000)) // ms
                .build());

        return TokenResponse.of(accessToken,refreshToken, user.getNickname());
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    @Transactional
    public TokenResponse reissue(String refreshToken){
        try{
            // 리프레시 토큰 만료 검증
            jwtProvider.parseRefreshToken(refreshToken);

            // db에 있는가? (로그아웃/재발급 등으로 교체되었는가 확인)
            RefreshToken dbRefreshToken = refreshRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

            if(dbRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
                refreshRepository.delete(dbRefreshToken);
                throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }

            User user = userRepository.findById(dbRefreshToken.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());

            return TokenResponse.of(newAccessToken, refreshToken, user.getNickname());
        }
        catch (JwtException je){
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Transactional
    public void logout(Long userId){
        refreshRepository.deleteByUserId(userId);
    }
}
