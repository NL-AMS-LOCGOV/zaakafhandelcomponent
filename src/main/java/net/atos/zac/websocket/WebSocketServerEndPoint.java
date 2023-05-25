/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import static javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY;
import static net.atos.zac.authentication.SecurityUtil.LOGGED_IN_USER_SESSION_ATTRIBUTE;
import static net.atos.zac.websocket.SubscriptionType.DELETE_ALL;
import static net.atos.zac.websocket.WebsocketHandshakeInterceptor.HTTP_SESSION;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.atos.zac.authentication.LoggedInUser;

@ServerEndpoint(value = "/websocket", configurator = WebsocketHandshakeInterceptor.class, decoders = {WebSocketSubscriptionMessageDecoder.class})
public class WebSocketServerEndPoint {

    private static final Logger LOG = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @Inject
    private SessionRegistry registry;

    @OnOpen
    public void open(final Session session, final EndpointConfig conf) {
        // Check that there is a logged in employee (and that authentication has taken place).
        final HttpSession httpSession = (HttpSession) conf.getUserProperties().get(HTTP_SESSION);
        final LoggedInUser loggedInUser =
                httpSession != null ? (LoggedInUser) httpSession.getAttribute(LOGGED_IN_USER_SESSION_ATTRIBUTE) : null;
        if (loggedInUser == null) {
            denyAccess(session, "no logged in user");
        } else {
            session.getUserProperties().put(LOGGED_IN_USER_SESSION_ATTRIBUTE, loggedInUser.getId());
            LOG.fine(() -> String.format("WebSocket open for %s", user(session)));
        }
    }

    @OnMessage
    public void processMessage(final SubscriptionType.SubscriptionMessage message, final Session session) {
        if (message != null) {
            LOG.fine(() -> String.format("WebSocket subscription %s for %s (%s)", message.getSubscriptionType(),
                                         user(session), message.getEvent()));
            message.register(registry, session);
        }
    }

    @OnError
    public void log(final Session session, final Throwable e) {
        final String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        LOG.log(Level.INFO, String.format("WebSocket error for %s (%s)", user(session), message), e);
    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        LOG.fine(() -> String.format("WebSocket closed for %s (%s)", user(session),
                                     CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode())));
        // Prevent resource leaks by always processing a fictitious DELETE_ALL message when closing.
        processMessage(DELETE_ALL.message(), session);
    }

    private void denyAccess(final Session session, final String reason) {
        LOG.severe(() -> String.format("Open WebSocket denied for %s (%s)", user(session), reason));
        try {
            // According to the RFC, this close reason should be used if the other reasons are not applicable.
            session.close(new CloseReason(VIOLATED_POLICY, reason));
        } catch (IOException e) {
            log(session, e);
        }
    }

    private String user(final Session session) {
        final String medewerker = (String) session.getUserProperties().get(LOGGED_IN_USER_SESSION_ATTRIBUTE);
        return medewerker != null ? "user " + medewerker : "session " + session.getId();
    }
}
