package com.smartserver.core.exception;

/**
 * author: cheikh.wang on 17/4/11
 * email: wanghonghi@126.com
 */
public class SystemException extends HttpException {

    public SystemException(String message) {
        super(500, message);
    }

    public SystemException(int status, String message) {
        super(status, message);
    }
}
