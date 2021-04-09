package com.weixin.sdk.http.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author chenhao
 */
public class Result<T> implements Serializable {
    /** 响应码  */
    private int code;
    /** 响应信息 */
    private String message;
    /** 数据 */
    private T data;
    /** 响应时间戳 秒级*/
    private final Long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Result(T data, ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = data;
    }

    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = null;
    }

    public Result(ResultEnum resultEnum, String message) {
        this.code = resultEnum.getCode();
        this.message = message;
        this.data = null;
    }

    public Result(T data, ResultEnum resultEnum, String message) {
        this.code = resultEnum.getCode();
        this.message = message;
        this.data = data;
    }

    public Result() {
    }

    public Long getTimestamp() {
        return timestamp;
    }
}