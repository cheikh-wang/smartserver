package com.smartserver.core.event;

import android.os.Bundle;

import com.smartserver.core.base.Component;

public class Event {

    private String name;
    private Component sender;
    private boolean handled;
    private Bundle data;

    public void name(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Component sender() {
        return sender;
    }

    public void sender(Component sender) {
        this.sender = sender;
    }

    public boolean handled() {
        return handled;
    }

    public void handled(boolean handled) {
        this.handled = handled;
    }

    public Bundle data() {
        return data;
    }

    public void data(Bundle data) {
        this.data = data;
    }
}
