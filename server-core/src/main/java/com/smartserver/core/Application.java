package com.smartserver.core;

import com.smartserver.core.base.Config;
import com.smartserver.core.base.Controller;
import com.smartserver.core.base.Component;
import com.smartserver.core.exception.HttpException;
import com.smartserver.core.exception.InvalidRouteException;
import com.smartserver.core.http.HttpRequest;
import com.smartserver.core.http.HttpResponse;
import com.smartserver.core.url.Route;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import java.util.HashMap;
import java.util.Map;


public class Application extends Component {

    private static final String TAG = Application.class.getSimpleName();

    private HttpRequest request;
    private HttpResponse response;

    private final Config config;

    private Map<String, Controller> controllerMap;

    private static Application sInstance;

    public static Application init(Config config) {
        sInstance = new Application(config);
        return sInstance;
    }

    public static Application getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("you must call Application.init() first");
        }
        return sInstance;
    }

    private Application(Config config) {
        this.config = config;
        this.controllerMap = new HashMap<>();
    }

    public void run(Request rawRequest, Response rawResponse) {
        this.request = new HttpRequest(rawRequest);
        this.response = new HttpResponse(rawResponse);

        HttpResponse httpResponse = null;
        try {
            httpResponse = handleRequest(request);
            httpResponse.send();
        } catch (Exception e) {
            e.printStackTrace();

            if (httpResponse == null) {
                httpResponse = response;
            }

            if (e instanceof HttpException) {
                httpResponse.setStatus(((HttpException) e).getStatus());
            } else {
                httpResponse.setStatus(500);
            }
            httpResponse.setStatusText(e.getMessage());
            httpResponse.setContent(e.getMessage());
            httpResponse.send();
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response = getHttpResponse();
        Route router = request.resolve();
        if (router.isFile()) {
            Config config = Application.getInstance().getConfig();
            response.setStream(config.getAssetReader().getInputStream(router.getPath()));
        } else {
            Object result = runAction(router);
            if (result instanceof HttpResponse) {
                return (HttpResponse) result;
            } else {
                if (result != null) {
                    response.setData(result);
                }
            }
        }

        return response;
    }

    private Object runAction(Route route) {
        Controller controller = createController(route);
        if (controller != null) {
            return controller.runAction(route);
        } else {
            throw new InvalidRouteException("Unable to resolve the request " + route.getPath());
        }
    }

    private Controller createController(Route route) {
        String name = route.getControllerName();
        if (name == null) {
            return null;
        }
        Controller controller = controllerMap.get(name);
        if (controller == null) {
            try {
                Class clazz = Class.forName(name);
                controller = new Controller(clazz.newInstance());
                controllerMap.put(name, controller);
                return controller;
            } catch (Exception e) {
                throw new InvalidRouteException("invalid Route");
            }
        }

        return controller;
    }

    public HttpRequest getHttpRequest() {
        return request;
    }

    public HttpResponse getHttpResponse() {
        return response;
    }

    public Config getConfig() {
        return config;
    }
}
