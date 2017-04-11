package com.smartserver.core.exception;

/**
 * author: cheikh.wang on 17/4/10
 * email: wanghonghi@126.com
 */

public class NotFoundHttpException extends HttpException {

    public NotFoundHttpException(String message) {
        super(404, message);
    }
}
