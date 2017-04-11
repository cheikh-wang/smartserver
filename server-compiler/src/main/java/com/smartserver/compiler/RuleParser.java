package com.smartserver.compiler;

import com.smartserver.annotation.Controller;
import com.smartserver.annotation.RequestMapping;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class RuleParser {
    private final String RULE_TEMPLATE = "{METHOD} {PATH} {CONTROLLER} {ACTION} {PARAM_TYPES} {PARAM_NAMES}";

    private final String method;
    private final String path;
    private final String controllerName;
    private final String actionName;
    private final String[] parameterTypes;
    private final String[] parameterNames;

    private RuleParser(Builder builder) {
        this.method = builder.method;
        this.path = builder.path;
        this.controllerName = builder.controllerName;
        this.actionName = builder.actionName;
        this.parameterTypes = builder.parameterTypes;
        this.parameterNames = builder.parameterNames;
    }

    public static class Builder {

        private final TypeElement controllerElement;
        private final ExecutableElement actionElement;

        private String method;
        private String path;
        private String controllerName;
        private String actionName;
        private String[] parameterTypes;
        private String[] parameterNames;

        public Builder(TypeElement controllerElement, ExecutableElement actionElement) {
            this.controllerElement = controllerElement;
            this.actionElement = actionElement;
        }

        public RuleParser build() {
            Controller controller = controllerElement.getAnnotation(Controller.class);
            RequestMapping requestMapping = actionElement.getAnnotation(RequestMapping.class);
            method = requestMapping.method().name();
            path = controller.value() + requestMapping.value();
            controllerName = controllerElement.asType().toString();
            actionName = actionElement.getSimpleName().toString();

            List<? extends VariableElement> parameterElements = actionElement.getParameters();
            int size = parameterElements != null ? parameterElements.size() : 0;
            parameterTypes = new String[size];
            parameterNames = new String[size];
            if (size > 0) {
                VariableElement element;
                for (int i = 0; i < size; i++) {
                    element = parameterElements.get(i);
                    parameterTypes[i] = element.asType().toString();
                    parameterNames[i] = element.getSimpleName().toString();
                }
            }

            return new RuleParser(this);
        }
    }

    private String implode(String[] pieces, String glue) {
        StringBuilder sb = new StringBuilder();
        if (pieces != null && pieces.length > 0) {
            for (int i = 0; i < pieces.length; i++) {
                if (i != 0) {
                    sb.append(glue);
                }
                sb.append(pieces[i]);
            }
        }
        return sb.toString();
    }

    public String parse() {
        return RULE_TEMPLATE.replace("{METHOD}", method)
                .replace("{PATH}", path)
                .replace("{CONTROLLER}", controllerName)
                .replace("{ACTION}", actionName)
                .replace("{PARAM_TYPES}", implode(parameterTypes, ";"))
                .replace("{PARAM_NAMES}", implode(parameterNames, ";"));
    }
}
