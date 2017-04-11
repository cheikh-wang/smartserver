package com.smartserver.core.formatter;

import com.smartserver.core.http.HttpResponse;

public class HtmlResponseFormatter implements ResponseFormatter {

    private String contentType = "text/html";

    @Override
    public void format(HttpResponse response) {
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        if (response.getData() != null) {
            response.setContent(response.getData());
        }
    }
}
