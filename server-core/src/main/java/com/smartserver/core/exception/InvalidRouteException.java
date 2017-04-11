package com.smartserver.core.exception;

/**
 * author: cheikh.wang on 17/4/10
 * email: wanghonghi@126.com
 */
public class InvalidRouteException extends HttpException {

    public InvalidRouteException() {
        super(404, "Invalid Route");
    }

    public InvalidRouteException(String message) {
        super(404, message);
    }

    public InvalidRouteException(int status, String message) {
        super(status, message);
    }
}
