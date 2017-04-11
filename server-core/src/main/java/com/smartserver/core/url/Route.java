package com.smartserver.core.url;

public class Route {

    private String method;
    private String path;
    private boolean isFile;
    private String controllerName;
    private String actionName;
    private Class[] parameterTypes;
    private String[] parameterNames;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public static Route fromRule(Rule rule) {
        Route route = new Route();
        route.setMethod(rule.getMethod());
        route.setPath(rule.getPath());
        if (rule.isFile()) {
            route.setFile(true);
        } else {
            String[] arr = rule.getRawRule().split(" ");
            if (arr.length >= 4) {
                String controllerName = arr[2];
                String actionName = arr[3];
                String[] paramNames = null;
                Class[] paramClasses = null;
                if (arr.length == 6) {
                    paramNames = arr[5].split(";");
                    String[] paramTypes = arr[4].split(";");
                    paramClasses = new Class[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        try {
                            paramClasses[i] = Class.forName(paramTypes[i]);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                route.setFile(false);
                route.setControllerName(controllerName);
                route.setActionName(actionName);
                route.setParameterNames(paramNames);
                route.setParameterTypes(paramClasses);
            }
        }

        return route;
    }
}
