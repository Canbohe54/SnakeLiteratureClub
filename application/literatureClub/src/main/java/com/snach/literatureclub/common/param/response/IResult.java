package com.snach.literatureclub.common.param.response;

//定义返回数据结构
public interface IResult {
    Integer getStatusCode();
    String getStatusMessage();
}