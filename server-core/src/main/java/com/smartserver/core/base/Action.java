package com.smartserver.core.base;

import com.smartserver.annotation.ResponseBody;
import com.smartserver.core.Application;
import com.smartserver.core.http.HttpResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action {

    private final Controller controller;
    private final Method method;
    private final String[] paramNames;

    public Action(Controller controller, Method method, String[] paramNames) {
        this.controller = controller;
        this.method = method;
        this.paramNames = paramNames;
    }

    protected boolean beforeRun() {
        return true;
    }

    private void afterRun() {
        // 如果加了ResponseBody注解则将结果序列化为json
        if (method.getAnnotation(ResponseBody.class) != null) {
            Application.getInstance().getHttpResponse().setFormat(HttpResponse.FORMAT_JSON);
        }
    }

    public Object run(Object[] params) {
        Object result = null;
        try {
            result = method.invoke(controller.getInstance(), params);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public final Object runWithParams() {
        Object[] args = controller.bindActionParams(this);
        if (beforeRun()) {
            try {
                Object result = run(args);
                afterRun();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getParamNames() {
        return paramNames;
    }
}
