package com.spring.boot.disk.server.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.spring.boot.disk.server.model.Rs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(AppException.class)
    public Rs handleBusinessException(AppException ex, HttpServletRequest request) {
        log.error("业务异常【{}】, uri:{}", ex.getMessage(), request.getRequestURI(), ex);
        return Rs.fail(ex.getMessage()).code(ex.getStatus());
    }

    @ExceptionHandler(NotLoginException.class)
    public Rs handleNotLoginException(NotLoginException ex, HttpServletRequest request) {
        log.error("授权异常【{}】, uri:{}", ex.getMessage(), request.getRequestURI(), ex);
        return Rs.fail("auth fail").code(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Rs handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("参数校验失败, params:{}, uri:{}", errors, request.getRequestURI(), ex);
        return Rs.fail("参数验证失败:" + errors);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Rs handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("请求的资源不存在, uri:{}", request.getRequestURI(), ex);
        return Rs.fail("not found uri:" + request.getRequestURI()).code(404);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Rs handleGlobalException(Exception ex, HttpServletRequest request) {
        // 可以在这里添加日志记录
        ex.printStackTrace();
        log.error("服务器内部错误, uri:{}", request.getRequestURI(), ex);
        return Rs.fail(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}

