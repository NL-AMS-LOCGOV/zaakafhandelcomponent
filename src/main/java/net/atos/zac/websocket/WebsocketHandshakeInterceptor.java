package net.atos.zac.websocket;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import net.atos.zac.authentication.SecurityUtil;

/* This interceptor is only needed to access the httpSession when opening a websocket.
 */
public class WebsocketHandshakeInterceptor extends ServerEndpointConfig.Configurator {

    private static final Logger LOG = Logger.getLogger(WebsocketHandshakeInterceptor.class.getName());

    public static final String HTTP_SESSION = "httpSession";

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        final HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession == null || SecurityUtil.getLoggedInUser(httpSession) == null) {
            LOG.info(SecurityUtil.log("modifyHandshake", httpSession));
        }
        config.getUserProperties().put(HTTP_SESSION, httpSession);
    }
}
