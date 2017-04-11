package com.smartserver.core.event;

public class ActionEvent extends Event {

    public String action;

    public Object result;

    public boolean isValid = true;

    public ActionEvent(String action) {
        this.action = action;
    }
}
