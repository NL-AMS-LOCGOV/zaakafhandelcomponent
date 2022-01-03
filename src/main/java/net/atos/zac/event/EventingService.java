/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import net.atos.client.zgw.shared.cache.event.CacheEvent;
import net.atos.zac.flowable.cmmn.event.CmmnEvent;
import net.atos.zac.websocket.event.ScreenEvent;

@ApplicationScoped
public class EventingService {

    @Inject
    private Event<ScreenEvent> screenUpdateEvent;

    @Inject
    private Event<CacheEvent> cacheUpdateEvent;

    @Inject
    private Event<CmmnEvent> cmmnUpdateEvent;

    /**
     * Send {@link package net.atos.zac.websocket.event.CacheEvent}s to Observer(s),
     * die ze vervolgens gebruiken om caches bij te werken.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.CacheEventType} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void send(final CacheEvent event) {
        cacheUpdateEvent.fire(event);
    }

    /**
     * Send {@link package net.atos.zac.websocket.event.CmmnEvent}s to Observer(s),
     * die ze vervolgens gebruiken om flowable bij te werken.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.CmmnEventType} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void send(final CmmnEvent event) {
        cmmnUpdateEvent.fireAsync(event);
    }

    /**
     * Send {@link package net.atos.zac.websocket.event.SchermEvent}s to Observer(s),
     * die ze vervolgens weer afleveren bij de juiste websocket clients.
     * <p>
     * Gebruik vooral de factory methods op {@link package net.atos.zac.websocket.event.ScreenEventType} om het event aan te maken!
     *
     * @param event het te versturen event
     */
    public void send(final ScreenEvent event) {
        screenUpdateEvent.fireAsync(event);
    }

    private void test(Integer i) {
        i += 2;
    }
}
