/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.atos.zac.event.AbstractUpdateObserver;
import net.atos.zac.websocket.SessionRegistry;

/**
 * Deze bean luistert naar SchermUpdateEvents, zet ze om naar een Websockets event en stuurt deze dan door naar de browsers die zich erop geabonneerd hebben.
 */
@Stateless
public class SchermUpdateObserver extends AbstractUpdateObserver<SchermUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(SchermUpdateObserver.class.getName());

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @EJB
    private SessionRegistry sessionRegistry;

    public void onFire(final @ObservesAsync SchermUpdateEvent event) {
        verstuurNaarWebsocketSubscribers(event);
    }

    private void verstuurNaarWebsocketSubscribers(final SchermUpdateEvent event) {
        try {
            final Set<Session> subscribers = sessionRegistry.getSessions(event);
            if (!subscribers.isEmpty()) {
                final String json = JSON_MAPPER.writeValueAsString(event);
                subscribers.forEach(session -> session.getAsyncRemote().sendText(json));
            }
        } catch (final JsonProcessingException e) {
            LOG.log(Level.SEVERE, "Het omzetten van het SchermUpdateEvent naar JSON is mislukt.", e);
        }
    }
}
