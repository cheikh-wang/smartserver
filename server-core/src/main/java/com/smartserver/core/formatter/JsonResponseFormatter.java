package com.smartserver.core.formatter;

import com.google.gson.Gson;
import com.smartserver.core.http.HttpResponse;

public class JsonResponseFormatter implements ResponseFormatter {

    private Gson gson;

    public JsonResponseFormatter() {
        this(new Gson());
    }

    public JsonResponseFormatter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void format(HttpResponse response) {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        if (response.getData() != null) {
            response.setContent(gson.toJson(response.getData()));
        }
    }
}
