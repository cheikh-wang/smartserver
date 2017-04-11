package com.smartserver.core;

import android.util.Log;
import com.smartserver.core.base.Config;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.io.IOException;

public class SmartServer implements Container {

    private static final String TAG = SmartServer.class.getSimpleName();

    private final int port;

    private Application application;
    private Connection connection;

    private SmartServer(Config config) throws IOException {
        port = config.getPort();
        application = Application.init(config);
        Server server = new ContainerServer(this);
        SocketAddress address = new InetSocketAddress(port);
        connection = new SocketConnection(server);
        connection.connect(address);
    }

    @Override
    public void handle(Request request, Response response) {
        application.run(request, response);
    }

    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "server failure to shutdown.");
            e.printStackTrace();
        }
    }

    public static SmartServer start(Config config) {
        try {
            return new SmartServer(config);
        } catch (IOException e) {
            Log.e(TAG, "server failure to start.");
            e.printStackTrace();
        }
        return null;
    }
}
