package com.smartserver.core.http;

import com.smartserver.core.base.Component;
import com.smartserver.core.exception.HttpException;
import com.smartserver.core.exception.SystemException;
import com.smartserver.core.formatter.HtmlResponseFormatter;
import com.smartserver.core.formatter.JsonResponseFormatter;
import com.smartserver.core.formatter.ResponseFormatter;
import org.simpleframework.http.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse extends Component {

    public static final String EVENT_BEFORE_SEND = "beforeSend";
    public static final String EVENT_AFTER_SEND = "afterSend";
    public static final String EVENT_AFTER_PREPARE = "afterPrepare";

    public static final String FORMAT_RAW = "raw";
    public static final String FORMAT_HTML = "html";
    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_JSONP = "jsonp";
    public static final String FORMAT_XML = "xml";

    private static Map<String, Class<? extends ResponseFormatter>> formatters;

    private Response rawResponse;

    private Object data;
    private Object content;
    private InputStream stream;
    private Map<String, String> headers;
    private int status = 200;
    private String statusText = "OK";
    private String format = FORMAT_HTML;

    private boolean isSent;

    static {
        formatters = new HashMap<String, Class<? extends ResponseFormatter>>(){
            {
                put(FORMAT_JSON, JsonResponseFormatter.class);
                put(FORMAT_HTML, HtmlResponseFormatter.class);
            }
        };
    }

    public HttpResponse(Response rawResponse) {
        this.rawResponse = rawResponse;
        this.headers = new HashMap<>();
    }

    public void send() {
        if (this.isSent) {
            return;
        }
        trigger(EVENT_BEFORE_SEND);
        prepare();
        trigger(EVENT_AFTER_PREPARE);
        sendHeaders();
        sendContent();
        trigger(EVENT_AFTER_SEND);
        this.isSent = true;
    }

    public void prepare() {
        if (stream != null) {
            return;
        }
        if (formatters.containsKey(format)) {
            Class<? extends ResponseFormatter> clazz = formatters.get(format);
            try {
                ResponseFormatter formatter = clazz.newInstance();
                formatter.format(this);
            } catch (Exception e) {
                throw new HttpException("The " + format + " response formatter is invalid. It must implement the ResponseFormatterInterface.");
            }
        } else if (format.equals(FORMAT_RAW)) {
            if (data != null) {
                content = data;
            }
        } else {
            throw new SystemException("Unsupported response format:" + format);
        }
    }

    public void sendHeaders() {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entity : headers.entrySet()) {
                rawResponse.setValue(entity.getKey(), entity.getValue());
            }
        }
        rawResponse.setCode(status);
        rawResponse.setDescription(statusText);
    }

    public void sendContent() {
        if (stream != null) {
            OutputStream output = null;
            try {
                output = rawResponse.getOutputStream();

                byte[] buffer = new byte[32 * 1024];

                int bytesRead;
                while ((bytesRead = stream.read(buffer, 0, buffer.length)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != output) {
                        output.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            PrintStream output = null;
            try {
                output = rawResponse.getPrintStream();
                output.print(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != output) {
                        output.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
