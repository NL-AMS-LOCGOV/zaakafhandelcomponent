/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeratie die de soorten events (objecttypen) bevat zoals die gebruikt worden door het {@link CacheEvent}.
 */
public enum CacheEventType {
    ZAAKROL,
    ZAAKSTATUS,
    ZAAKTYPE;

    private static final Logger LOG = Logger.getLogger(CacheEventType.class.getName());

    // Dit is de uiteindelijke echte factory method
    private static CacheEvent instance(final CacheEventType objectType, final URI url) {
        return new CacheEvent(objectType, url);
    }

    private CacheEvent event(final Notificatie.Resource resource) {
        return instance(this, resource.getUrl());
    }

    public final CacheEvent changed(final URI url) {
        return instance(this, url);
    }

    /**
     * This is the mapping.
     * <p>
     * For now it is complete even where no events are needed.
     * Once it is clear what is needed, it can be simplified and reduced to just that.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<CacheEvent> getEvents(final Channel channel, final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        final Set<CacheEvent> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case ROL:
                        switch (resource.getAction()) {
                            case CREATE:
                            case UPDATE:
                            case DELETE:
                                events.add(CacheEventType.ZAAKROL.event(mainResource));
                                break;
                            default:
                                break;
                        }
                        break;
                    case STATUS:
                        switch (resource.getAction()) {
                            case UPDATE:
                            case DELETE:
                                events.add(CacheEventType.ZAAKSTATUS.event(resource));
                                break;
                            default:
                                break;
                        }
                        break;
                }
                break;
            case ZAAKTYPEN:
                switch (resource.getType()) {
                    case ZAAKTYPE:
                        switch (resource.getAction()) {
                            case UPDATE:
                            case DELETE:
                                events.add(CacheEventType.ZAAKTYPE.event(resource));
                            default:
                                break;
                        }
                        break;
                }
                break;
        }
        return events;
    }
}
