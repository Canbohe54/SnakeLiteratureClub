package com.snach.literatureclub.common.param.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一返回数据结构
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getStatusCode(), ResultEnum.SUCCESS.getStatusMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultEnum.SUCCESS.getStatusCode(), message, data);
    }

    public static Result<?> failed() {
        return new Result<>(ResultEnum.COMMON_FAILED.getStatusCode(), ResultEnum.COMMON_FAILED.getStatusMessage(), null);
    }

    public static Result<?> failed(String message) {
        return new Result<>(ResultEnum.COMMON_FAILED.getStatusCode(), message, null);
    }

    public static Result<?> failed(IResult errorResult) {
        return new Result<>(errorResult.getStatusCode(), errorResult.getStatusMessage(), null);
    }
    public static Result<?> failed(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    public static <T> Result<T> instance(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}