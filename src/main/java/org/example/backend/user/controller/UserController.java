package org.example.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.category.repository.CategoryRepository;
import org.example.backend.common.exception.CustomException;
import org.example.backend.common.exception.ErrorCode;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.transaction.repository.TransactionRepository;
import org.example.backend.user.dto.UserPWRequest;
import org.example.backend.user.dto.UserProfileResponse;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.RefreshRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshRepository refreshRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> profile(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getUserId()).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse response = UserProfileResponse.from(user, transactionRepository.countByUserId(user.getId()));

        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
    }

    @Transactional
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody UserPWRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getUserId()).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 성공",null));
    }

    @Transactional
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestBody UserPWRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getUserId()).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        refreshRepository.deleteByUserId(user.getId());
        transactionRepository.deleteAllByUserId(user.getId());
        categoryRepository.deleteAllByUserId(user.getId());
        userRepository.delete(user);

        return ResponseEntity.ok(ApiResponse.success("사용자 삭제 성공",null));
    }
}
