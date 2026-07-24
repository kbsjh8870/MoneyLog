package org.example.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 - 검증 실패
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    CATEGORY_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "선택한 카테고리와 유형이 다릅니다."),
    // 401 - 로그인 자격증명 불일치
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    // 401 - 토큰 없음/위조/만료
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    // 403 - 인가 실패(본인 리소스 아님)
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    // 409 - 리소스 중복
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT,"이미 사용 중인 리소스입니다."),
    // 404 - 리소스 없음
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    NOT_FOUND_TRANSACTION(HttpStatus.NOT_FOUND, "거래내역을 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
