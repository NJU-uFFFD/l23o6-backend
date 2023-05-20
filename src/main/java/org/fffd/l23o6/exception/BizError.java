package org.fffd.l23o6.exception;

import io.github.lyc8503.spring.starter.incantation.exception.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizError implements ErrorType {

    USERNAME_EXISTS(200001, "用户名已存在", 400),
    INVALID_CREDENTIAL(200002, "用户名或密码错误", 400),
    STATIONNAME_EXISTS(200003, "同名站点已存在", 400);


    final int code;
    final String message;
    final int httpCode;
}
