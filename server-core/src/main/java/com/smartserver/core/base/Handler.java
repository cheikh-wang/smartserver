package com.smartserver.core.base;

import android.os.Bundle;
import com.smartserver.core.event.Event;

/**
 * author: cheikh.wang on 17/3/30
 * email: wanghonghi@126.com
 */
public abstract class Handler {

    private Handler handler;
    private Bundle data;

    public Handler handler() {
        return handler;
    }

    public void handler(Handler handler) {
        this.handler = handler;
    }

    public Bundle data() {
        return data;
    }

    public void data(Bundle data) {
        this.data = data;
    }

    public abstract void run(Event event);
}
