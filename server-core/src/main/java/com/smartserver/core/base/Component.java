package com.smartserver.core.base;

import android.os.Bundle;
import android.util.ArrayMap;

import com.smartserver.core.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Component {

    private Map<String, List<Handler>> handlerRegistry = new HashMap<>();
    private Map<String, Behavior> behaviorRegistry;

    public Map<String, Behavior> behaviors() {
        return null;
    }

    public Behavior getBehavior(String name) {
        ensureBehaviors();
        return behaviorRegistry.get(name);
    }

    private Behavior attachBehaviorInternal(String name, Behavior behavior) {
        if (behaviorRegistry.containsKey(name)) {
            behaviorRegistry.get(name).detach();
        }
        behavior.attach(this);
        behaviorRegistry.put(name, behavior);
        return behavior;
    }

    private void ensureBehaviors() {
        if (behaviorRegistry == null) {
            behaviorRegistry = new HashMap<>();
            Map<String, Behavior> behaviors = behaviors();
            if (behaviors != null) {
                for (String name : behaviors.keySet()) {
                    attachBehaviorInternal(name, behaviors.get(name));
                }
            }
        }
    }

    public void on(String name, Handler handler) {
        on(name, handler, null);
    }

    public void on(String name, Handler handler, Bundle data) {
        on(name, handler, data, true);
    }

    public void on(String name, Handler handler, Bundle data, boolean append) {
        ensureBehaviors();
        if (append || !handlerRegistry.containsKey(name)) {
            List<Handler> list = new ArrayList<>();
            handler.data(data);
            list.add(handler);
            handlerRegistry.put(name, list);
        } else {
            List<Handler> list = handlerRegistry.get(name);
            handler.data(data);
            list.add(0, handler);
            handlerRegistry.put(name, list);
        }
    }

    public boolean off(String name) {
        return off(name, null);
    }

    public boolean off(String name, Handler handler) {
        ensureBehaviors();
        if (!handlerRegistry.containsKey(name)) {
            return false;
        }
        if (handler == null) {
            handlerRegistry.remove(name);
            return true;
        } else {
            boolean removed = false;
            List<Handler> list = handlerRegistry.get(name);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).handler() == handler) {
                    list.remove(i);
                    removed = true;
                }
            }
            return removed;
        }
    }

    public void trigger(String name) {
        trigger(name, null);
    }

    public void trigger(String name, Event event) {
        ensureBehaviors();
        if (handlerRegistry.containsKey(name)) {
            if (event == null) {
                event = new Event();
            }
            if (event.sender() == null) {
                event.sender(this);
            }
            event.handled(false);
            event.name(name);
            for (Handler handler : handlerRegistry.get(name)) {
                event.data(handler.data());
                handler.run(event);
                if (event.handled()) {
                    return;
                }
            }
        }
    }
}
