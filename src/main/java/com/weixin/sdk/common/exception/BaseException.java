package com.weixin.sdk.common.exception;


import com.weixin.sdk.http.response.ResultEnum;

/**
 * @author Wenbo
 * @date 2021/1/8 11:09
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
public class BaseException extends RuntimeException {
    int code;
    String description;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BaseException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.description = resultEnum.getMessage();
        this.code = resultEnum.getCode();
    }

    public BaseException(ResultEnum resultEnum,String description){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.description = description;
    }

    public BaseException(String description) {
        super(description);
    }

    public BaseException(String description, Throwable cause) {
        super(description, cause);
    }

}
