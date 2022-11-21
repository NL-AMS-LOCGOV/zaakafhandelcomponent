/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import net.atos.zac.signalering.event.SignaleringEvent;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.util.event.JobEvent;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventType;

@ApplicationScoped
public class EventingService {

    @Inject
    private Event<ScreenEvent> screenUpdateEvent;

    @Inject
    private Event<SignaleringEvent<?>> signaleringEvent;

    @Inject
    private Event<JobEvent> signaleringJobEvent;

    /**
     * Send {@link ScreenEvent}s to Observer(s),
     * These will be passed on to the subscribed websocket clients.
     * <p>
     * Prefer using the factory methods on {@link ScreenEventType} to create these event!
     *
     * @param event the event that will be sent.
     */
    public void send(final ScreenEvent event) {
        screenUpdateEvent.fireAsync(event);
    }

    /**
     * Send {@link SignaleringEvent}s to Observer(s),
     * These will be used to create and/or send signaleringen.
     * <p>
     * Prefer using the factory methods on {@link SignaleringEventUtil} to create these event!
     *
     * @param event the event that will be sent.
     */
    public void send(final SignaleringEvent<?> event) {
        signaleringEvent.fireAsync(event);
    }

    /**
     * Send {@link JobEvent}s to Observer(s),
     * These will be used to start a background job of the correct type.
     *
     * @param event the event that will be sent.
     */
    public void send(final JobEvent event) {
        signaleringJobEvent.fireAsync(event);
    }
}
