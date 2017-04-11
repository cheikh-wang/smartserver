package com.smartserver.core.url;

import com.smartserver.core.http.HttpRequest;
import com.smartserver.core.util.StringUtil;

public class Rule {

    private static final String RULE_TEMPLATE = "{METHOD} {PATH} {CONTROLLER} {ACTION} {PARAM_TYPES} {PARAM_NAMES}";
    private static final String RULE_TEMPLATE_NO_PARAMS = "{METHOD} {PATH} {CONTROLLER} {ACTION}";
    private static final String RULE_TEMPLATE_NO_ADDRESS = "{METHOD} {PATH}";

    private final String method;
    private final String path;
    private final boolean isFile;
    private final String rawRule;

    public Rule(String method, String path, boolean isFile, String rawRule) {
        this.method = method;
        this.path = path;
        this.isFile = isFile;
        this.rawRule = rawRule;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getRawRule() {
        return rawRule;
    }

    public Route parseRequest(UrlManager urlManager, HttpRequest request) {
        String path = request.getPath();
        if (("").equals(path)) {
            path = "index.html";
        }
        String method = request.getMethod();
        if (this.method.equals(method) && this.path.equals(path)) {
            return Route.fromRule(this);
        }
        return null;
    }

    public static String createRule(String method, String path) {
        return Rule.RULE_TEMPLATE_NO_ADDRESS.replace("{METHOD}", method)
                .replace("{PATH}", path);
    }

    public static String createRule(String method, String path, String controllerName, String actionName) {
        return Rule.RULE_TEMPLATE_NO_PARAMS.replace("{METHOD}", method)
                .replace("{PATH}", path)
                .replace("{CONTROLLER}", controllerName)
                .replace("{ACTION}", actionName);
    }

    public static String createRule(String method, String path, String controllerName, String actionName, String[] paramTypes, String[] paramNames) {
        return Rule.RULE_TEMPLATE.replace("{METHOD}", method)
                .replace("{PATH}", path)
                .replace("{CONTROLLER}", controllerName)
                .replace("{ACTION}", actionName)
                .replace("{PARAM_TYPES}", StringUtil.implode(paramTypes, ";"))
                .replace("{PARAM_NAMES}", StringUtil.implode(paramNames, ";"));
    }
}
