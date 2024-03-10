package com.snach.literatureclub.common.param.response;

import lombok.Getter;

//常用结果的枚举
@Getter
public enum ResultEnum implements IResult {
    SUCCESS(2001, "Success."),
    VALIDATE_FAILED(2002, "Invalid Parameter."),
    COMMON_FAILED(2003, "Api call failed. Server Error."),
    FORBIDDEN(2004, "Insufficient permission.");
    private Integer statusCode;
    private String statusMessage;
    ResultEnum(Integer code, String message) {
        this.statusCode = code;
        this.statusMessage = message;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
