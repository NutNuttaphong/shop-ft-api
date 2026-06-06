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

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().data(data).error(null).status(200).build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder().data(data).error(null).status(201).build();
    }

    public static <T> ApiResponse<T> error(String error, int status) {
        return ApiResponse.<T>builder().data(null).error(error).status(status).build();
    }
}
