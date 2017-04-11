package com.smartserver.core.formatter;

import com.smartserver.core.http.HttpResponse;

public interface ResponseFormatter {

    void format(HttpResponse response);
}
