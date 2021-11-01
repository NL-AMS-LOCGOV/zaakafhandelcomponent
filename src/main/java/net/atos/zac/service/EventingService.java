/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import net.atos.zac.websocket.event.SchermUpdateEvent;

@Stateless
public class EventingService {

    @Inject
    private Event<SchermUpdateEvent> schermUpdateEvent;

    /**
     * Verstuurt {@link package net.atos.zac.websocket.event.SchermeUpdateEvent}s naar de Observer(s),
     * die ze vervolgens weer afleveren bij de juiste websocket clients.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.ObjectTypeEvent} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void versturen(final SchermUpdateEvent event) {
        schermUpdateEvent.fireAsync(event);
    }
}
