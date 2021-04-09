package com.weixin.sdk.http.response;

/**
 * @author chenhao
 */
public class Response {
    /**
     * 封装最基础的成功返回，传入返回数据即可正常返回
     * @param data 业务数据
     * @param <T> 泛型数据
     * @return 返回Result 结构
     */
    public static <T> Result<T> success(T data){
        return new Result<>(data, ResultEnum.SUCCESS);
    }

    /**
     * 封装最基础的失败返回，使用默认的失败code，运行传入message信息
     * @param <T> 泛型数据
     * @param message 错误提示信息
     * @return 返回Result 结构
     */
    public static <T> Result<T> fail(String message){
        return new Result<>(ResultEnum.FAIL,message);
    }

    /**
     * 封装最基础的失败返回，允许传入枚举中的错误编码以及自定义错误信息
     * @param <T> 泛型数据
     * @param resultEnum 返回枚举
     * @param message 错误信息
     * @return 返回Result 结构
     */
    public static <T> Result<T> fail(ResultEnum resultEnum, String message){
        return new Result<>(resultEnum,message);
    }

    /**
     * 封装最基础的失败返回，允许使用枚举中的错误
     * @param resultEnum 返回枚举
     * @return 返回Result 结构
     */
    public static <T> Result<T> fail(ResultEnum resultEnum){
        return new Result<>(resultEnum);
    }


    /**
     * 自定义返回Response，带上业务data
     * @param data 自定义的data，可以是接口返回的，也可以是错误时的数据
     * @param resultEnum 错误的枚举，会使用错误枚举中的code
     * @param message 错误信息，可用于展示
     * @param <T> 业务data可为泛型
     * @return 返回Result 结构
     */
    public static <T> Result<T> customRes(T data, ResultEnum resultEnum, String message){
        return new Result<>(data,resultEnum,message);
    }

    /**
     * 自定义返回Response，不带data
     * @param resultEnum 错误的枚举，会使用错误枚举中的code
     * @param message 错误信息，可用于展示
     * @return 返回Result 结构
     */
    public static <T> Result<T> customRes(ResultEnum resultEnum, String message){
        return new Result<>(resultEnum,message);
    }
}
