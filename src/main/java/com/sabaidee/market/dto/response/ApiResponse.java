package com.sabaidee.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private T data;
    private String error;
    private int status;
    private boolean success;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .error(null)
                .status(200)
                .success(true)
                .message(null)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .error(null)
                .status(201)
                .success(true)
                .message(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String error, int status) {
        return ApiResponse.<T>builder()
                .data(null)
                .error(error)
                .status(status)
                .success(false)
                .message(error)
                .build();
    }
}
