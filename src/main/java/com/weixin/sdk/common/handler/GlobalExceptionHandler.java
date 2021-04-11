package com.weixin.sdk.common.handler;

import com.weixin.sdk.common.exception.BaseException;
import com.weixin.sdk.http.response.Response;
import com.weixin.sdk.http.response.Result;
import com.weixin.sdk.http.response.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

/**
 * @author cuibaoluo
 * @date 2021/1/8
 * @description： 全局异常处理
 */
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 统一处理请求参数校验(实体对象传参-form)
     *
     * @param e BindException
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    public Result<?> validExceptionHandler(BindException e) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return Response.fail(ResultEnum.PARAMETER_ERROR,message.toString());
    }

    /**
     * 统一处理请求参数校验(普通传参)
     *
     * @param e ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), ".");
            message.append(pathArr[1]).append(violation.getMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return Response.fail(ResultEnum.PARAMETER_ERROR,message.toString());
    }

    /**
     * 统一处理请求参数校验(json)
     *
     * @param e ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        log.error(message.toString(), e);
        return Response.fail(ResultEnum.PARAMETER_ERROR,message.toString());
    }
    /**
     * 统一处理请求参数校验(json)
     *
     * @param e ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handlerMethodArgumentNotValidException(Exception e) {

        log.error("", e);
        return Response.fail(ResultEnum.ERROR);
    }

    /**
     * 统一处理请求参数校验(json)
     *
     * @param e ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(BaseException.class)
    public Result<?> handlerMethodArgumentNotValidException(BaseException e) {



        log.error(e.getDescription(),e);
        return Response.fail(ResultEnum.PARAMETER_ERROR,e.getDescription());
    }




    private void logAddErrorStackTrace(Throwable throwable){
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StackTraceElement stackTraceElement;
        for (StackTraceElement s:stackTraceElements){
            String className = s.getClassName();
            if(!className.startsWith("com.liulimi.account.core.exception.asserts")){
                MDC.put("errorClass",s.getClassName());
                MDC.put("errorMethod",s.getMethodName());
                MDC.put("errorLine",String.valueOf(s.getLineNumber()));

                break;
            }
        }
    }

}
