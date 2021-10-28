/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import static javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY;
import static net.atos.zac.authentication.SecurityUtil.INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE;
import static net.atos.zac.websocket.SubscriptionType.VERWIJDER_ALLES;
import static net.atos.zac.websocket.WebsocketHandshakeInterceptor.HTTP_SESSION;

import java.io.IOException;
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

import net.atos.zac.authentication.Medewerker;

@ServerEndpoint(value = "/websocket", configurator = WebsocketHandshakeInterceptor.class, decoders = {WebSocketSubscriptionMessageDecoder.class})

public class WebSocketServerEndPoint {

    private static final Logger LOG = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @Inject
    private SessionRegistry registry;

    @OnOpen
    public void open(final Session session, final EndpointConfig conf) {
        // Controleer dat er een ingelogde medewerker is (en dat authenticatie dus heeft plaatsgevonden).
        final HttpSession httpSession = (HttpSession) conf.getUserProperties().get(HTTP_SESSION);
        final Medewerker ingelogdeMedewerker = (Medewerker) httpSession.getAttribute(INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE);
        if (ingelogdeMedewerker == null) {
            denyAccess(session, "geen ingelogde medewerker");
        } else {
            session.getUserProperties().put(INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE, ingelogdeMedewerker.getGebruikersnaam());
            LOG.fine(() -> String.format("WebSocket geopend voor %s", user(session)));
        }
    }

    @OnMessage
    public void processMessage(final SubscriptionType.SubscriptionMessage message, final Session session) {
        if (message != null) {
            LOG.fine(() -> String.format("WebSocket subscription %s voor %s (%s)", message.getSubscriptionType(), user(session), message.getEvent()));
            message.register(registry, session);
        }
    }

    @OnError
    public void log(final Session session, final Throwable e) {
        final String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        LOG.info(() -> String.format("WebSocket error voor %s (%s)", user(session), message));
    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        LOG.fine(() -> String.format("WebSocket gesloten voor %s (%s)", user(session), CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode())));
        // Voorkom resource lekken door altijd nog even een fictief VERWIJDER_ALLES bericht te verwerken bij het sluiten.
        processMessage(VERWIJDER_ALLES.message(), session);
    }

    private void denyAccess(final Session session, final String reason) {
        LOG.severe(() -> String.format("WebSocket openen geweigerd voor %s (%s)", user(session), reason));
        try {
            // Deze close reason moet volgens de RFC gebruikt worden als de andere redenen niet van toepassing zijn.
            session.close(new CloseReason(VIOLATED_POLICY, reason));
        } catch (IOException e) {
            log(session, e);
        }
    }

    private String user(final Session session) {
        final String medewerker = (String) session.getUserProperties().get(INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE);
        return medewerker != null ? "user " + medewerker : "session " + session.getId();
    }
}
