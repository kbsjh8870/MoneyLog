package org.example.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ApiResponse<T>{
    private boolean success;
    private String message;
    private T data;
    private Object meta; // 목록 조회 시 PageMeta 등을 담음(없으면 미출력)
    private String code; // 실패 시 기계용 에러 코드(1-8 전역 예외에서 채움)

    // 성공 - 데이터만 (기본 메세지)
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true,"요청 성공",data,null,null);
    }

    // 성공 - 메세지 + 데이터
    public static <T> ApiResponse<T> success(String message,T data){
        return new ApiResponse<>(true,message,data,null,null);
    }

    // 성공 - 메세지 + 데이터 + meta (목록 pagination 등)
    public static <T> ApiResponse<T> success(String message, T data, Object meta){
        return new ApiResponse<>(true, message, data, meta,null);
    }

    // 실패 - 전역 예외 처리에서 사용
    public static <T> ApiResponse<T> error(String code, String message){
        return new ApiResponse<>(false,message,null,null,code);
    }
}
