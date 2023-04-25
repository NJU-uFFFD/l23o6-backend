package org.fffd.l23o6.controller;

import cn.dev33.satoken.exception.NotLoginException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    public CommonResponse<?> handleAuthorizeException(NotLoginException e) {
        log.error("Not Login Exception", e);
        return CommonResponse.error(CommonErrorType.UNAUTHORIZED, e.getMessage());
    }
}
