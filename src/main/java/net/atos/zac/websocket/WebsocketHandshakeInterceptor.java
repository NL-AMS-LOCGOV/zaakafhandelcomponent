package net.atos.zac.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/* This interceptor is only needed to access the httpSession when opening a websocket.
 */
public class WebsocketHandshakeInterceptor extends ServerEndpointConfig.Configurator {

    public static final String HTTP_SESSION = "httpSession";

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        final HttpSession httpSession = (HttpSession) request.getHttpSession();
        config.getUserProperties().put(HTTP_SESSION, httpSession);
    }
}
