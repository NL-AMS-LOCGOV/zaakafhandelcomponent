/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import static net.atos.zac.websocket.SubscriptionType.VERWIJDER_ALLES;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket", decoders = {WebSocketSubscriptionMessageDecoder.class})

public class WebSocketServerEndPoint {

    private static final Logger LOG = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @Inject
    private SessionRegistry registry;

    @OnOpen
    public void open(final Session session, final EndpointConfig conf) {
        LOG.fine(() -> String.format("WebSocket geopend voor %s", user(session)));
    }

    @OnMessage
    public void processMessage(final SubscriptionType.SubscriptionMessage message, final Session session) {
        if (message != null) {
            LOG.fine(() -> String.format("WebSocket subscription %s %s", message.getSubscriptionType(), message.getEvent()));
            message.register(registry, session);
        }
    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        LOG.fine(() -> String.format("WebSocket gesloten voor %s (%d)", user(session), reason.getCloseCode().getCode()));
        // Voorkom resource lekken door nog even een fictief VERWIJDER_ALLES bericht te verwerken.
        processMessage(VERWIJDER_ALLES.message(), session);
    }

    private String user(final Session session) {
        return session.getUserPrincipal() != null ? "user " + session.getUserPrincipal().getName() : "session " + session.getId();
    }
}
