package com.smartserver.core.http;

import android.text.TextUtils;
import com.smartserver.core.base.Component;
import com.smartserver.core.exception.NotFoundHttpException;
import com.smartserver.core.url.Route;
import com.smartserver.core.url.UrlManager;
import org.simpleframework.http.Request;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest extends Component {

    private Request rawRequest;

    private String path;
    private String method;
    private Map<String, String> params;

    public HttpRequest(Request rawRequest) {
        this.rawRequest = rawRequest;
    }

    public Route resolve() {
        Route result = UrlManager.getInstance().parseRequest(this);
        if (result != null) {
            return result;
        } else {
            throw new NotFoundHttpException("Page not found.");
        }
    }

    public Request getRawRequest() {
        return rawRequest;
    }

    public String getParameter(String name) {
        return rawRequest.getParameter(name);
    }

    public String getPath() {
        if (path == null) {
            path = rawRequest.getPath().toString();
            path = path.startsWith("/") ? path.substring(1) : path;
        }
        return path;
    }

    public String getMethod() {
        if (method == null) {
            method = rawRequest.getMethod();
        }
        return method;
    }

    /**
     * 从字符串中解析出参数对
     * @param form 字符串
     * @return 解析后的参数对
     */
    private Map<String, String> decodeForm(String form) {
        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(form)) {
            return params;
        }
        for (String nvp : form.split("\\&")) {
            int equals = nvp.indexOf('=');
            String name;
            String value;
            if (equals < 0) {
                name = nvp;
                value = null;
            } else {
                name = nvp.substring(0, equals);
                value = nvp.substring(equals + 1);
            }
            params.put(name, value);
        }
        return params;
    }

    /**
     * 从输入流中解析出参数对
     * @param inputStream 输入流
     * @return 解析后的参数对
     * @throws IOException
     */
    private Map<String, String> decodeForm(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        return decodeForm(sb.toString());
    }
}
