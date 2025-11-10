package com.spring.boot.disk.server.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final int status;

    public AppException(String message) {
        super(message);
        this.status = 500;
    }

    public AppException(String message, int status) {
        super(message);
        this.status = status;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.status = 500;
    }

    public AppException(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}

