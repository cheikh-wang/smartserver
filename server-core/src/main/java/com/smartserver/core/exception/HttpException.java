package com.smartserver.core.exception;

/**
 * author: cheikh.wang on 17/4/10
 * email: wanghonghi@126.com
 */

public class HttpException extends RuntimeException {

    private int status;

    public HttpException(String message) {
        this(500, message);
    }

    public HttpException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
