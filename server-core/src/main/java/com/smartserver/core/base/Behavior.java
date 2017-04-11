package com.smartserver.core.base;

import java.util.HashMap;
import java.util.Map;

public class Behavior {

    protected Component owner;

    public Map<String, Handler> events() {
        return new HashMap<>();
    }

    public void attach(Component owner) {
        this.owner = owner;
        Map<String, Handler> events = events();
        if (events.isEmpty()) {
            for (String name : events.keySet()) {
                owner.on(name, events.get(name));
            }
        }
    }

    public void detach() {
        if (owner != null) {
            Map<String, Handler> events = events();
            if (events.isEmpty()) {
                for (String name : events.keySet()) {
                    owner.off(name, events.get(name));
                }
            }
            owner = null;
        }
    }
}