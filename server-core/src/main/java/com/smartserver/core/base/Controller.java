package com.smartserver.core.base;

import com.smartserver.core.Application;
import com.smartserver.core.event.ActionEvent;
import com.smartserver.core.exception.BadRequestException;
import com.smartserver.core.exception.InvalidRouteException;
import com.smartserver.core.http.HttpRequest;
import com.smartserver.core.url.Route;
import com.smartserver.core.util.StringUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller extends Component {

    public static final String EVENT_BEFORE_ACTION = "beforeAction";
    public static final String EVENT_AFTER_ACTION = "afterAction";

    private final Object instance;
    private Map<String, Action> actionCache;
    private Map<String, String> actionParams;

    public Controller(Object instance) {
        this.instance = instance;
        this.actionCache = new HashMap<>();
        this.actionParams = new HashMap<>();
    }

    public Object runAction(Route route) {
        String actionId = route.getActionName();
        Action action = createAction(route);
        if (action == null) {
            throw new InvalidRouteException("invalid action id: " + route.getActionName());
        }

        Object result = null;
        if (beforeAction(actionId)) {
            result = action.runWithParams();
            result = afterAction(actionId, result);
        }

        return result;
    }

    public Action createAction(Route route) {
        String actionId = route.getActionName();
        Action action = actionCache.get(actionId);
        if (action == null) {
            try {
                Method method = instance.getClass().getMethod(actionId, route.getParameterTypes());
                action = new Action(this, method, route.getParameterNames());
                actionCache.put(actionId, action);
            } catch (NoSuchMethodException e) {
                // ignore this exception.
            }
        }

        return action;
    }

    public boolean beforeAction(String actionId) {
        ActionEvent event = new ActionEvent(actionId);
        trigger(EVENT_BEFORE_ACTION, event);
        return event.isValid;
    }

    public Object afterAction(String actionId, Object result) {
        ActionEvent event = new ActionEvent(actionId);
        event.result = result;
        trigger(EVENT_AFTER_ACTION, event);
        return event.result;
    }

    public Object[] bindActionParams(Action action) {
        HttpRequest request = Application.getInstance().getHttpRequest();

        List<String> missingArgs = new ArrayList<>();
        String[] names = action.getParamNames();
        if (names == null || names.length == 0) {
            return null;
        }

        String name;
        String value;
        String[] values = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            name = names[i];
            value = request.getParameter(name);
            if (value != null) {
                values[i] = value;
                actionParams.put(name, value);
            } else {
                missingArgs.add(name);
            }
        }

        if (!missingArgs.isEmpty()) {
            throw new BadRequestException("Missing required parameters: " + StringUtil.implode(missingArgs, ","));
        }

        return values;
    }

    public Object getInstance() {
        return instance;
    }
}
