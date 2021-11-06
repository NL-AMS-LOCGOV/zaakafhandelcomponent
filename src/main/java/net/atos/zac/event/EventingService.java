/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import net.atos.zac.flowable.cmmn.event.CmmnUpdateEvent;
import net.atos.zac.websocket.event.SchermUpdateEvent;

@ApplicationScoped
public class EventingService {

    @Inject
    private Event<SchermUpdateEvent> schermUpdateEvent;

    @Inject
    private Event<CacheUpdateEvent> cacheUpdateEvent;

    @Inject
    private Event<CmmnUpdateEvent> cmmnUpdateEvent;

    /**
     * Send {@link package net.atos.zac.websocket.event.SchermeUpdateEvent}s to Observer(s),
     * die ze vervolgens weer afleveren bij de juiste websocket clients.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.ObjectTypeEvent} om het event aan te maken!
     *
     * @param event
     */
    public void send(final SchermUpdateEvent event) {
        schermUpdateEvent.fireAsync(event);
    }

    /**
     * Send {@link package net.atos.zac.websocket.event.CacheUpdateEvent}s to Observer(s),
     * die ze vervolgens gebruiken om caches bij te werken.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.CacheFlushEvent} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void send(final CacheUpdateEvent event) {
        cacheUpdateEvent.fireAsync(event);
    }

    /**
     * Send {@link package net.atos.zac.websocket.event.CmmnUpdateEvent}s to Observer(s),
     * die ze vervolgens gebruiken om flowable bij te werken.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.CacheFlushEvent} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void send(final CmmnUpdateEvent event) {
        cmmnUpdateEvent.fireAsync(event);
    }
}
