/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;


import java.net.URI;
import java.util.Collections;
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
        switch (channel) {
            case ZAKEN:
                return getChannelZakenEvents(mainResource, resource);
            case ZAAKTYPEN:
                return getChannelZaaktypenEvents(resource);
            default:
                return Collections.emptySet();
        }
    }

    private static Set<CacheEvent> getChannelZakenEvents(final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        switch (resource.getType()) {
            case ROL:
                return getChannelZakenResourceRolEvents(mainResource, resource);
            case STATUS:
                return getChannelZakenResourceStatusEvents(resource);
            default:
                return Collections.emptySet();
        }
    }

    private static Set<CacheEvent> getChannelZakenResourceRolEvents(final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        switch (resource.getAction()) {
            case CREATE:
            case UPDATE:
            case DELETE:
                final Set<CacheEvent> events = new HashSet<>();
                events.add(CacheEventType.ZAAKROL.event(mainResource));
                return events;
            default:
                return Collections.emptySet();
        }
    }

    private static Set<CacheEvent> getChannelZakenResourceStatusEvents(final Notificatie.Resource resource) {
        switch (resource.getAction()) {
            case UPDATE:
            case DELETE:
                final Set<CacheEvent> events = new HashSet<>();
                events.add(CacheEventType.ZAAKSTATUS.event(resource));
                return events;
            default:
                return Collections.emptySet();
        }
    }

    private static Set<CacheEvent> getChannelZaaktypenEvents(final Notificatie.Resource resource) {
        switch (resource.getType()) {
            case ZAAKTYPE:
                return getChannelZaaktypenResourceZaaktypeEvents(resource);
            default:
                return Collections.emptySet();
        }
    }

    private static Set<CacheEvent> getChannelZaaktypenResourceZaaktypeEvents(final Notificatie.Resource resource) {
        switch (resource.getAction()) {
            case UPDATE:
            case DELETE:
                final Set<CacheEvent> events = new HashSet<>();
                events.add(CacheEventType.ZAAKTYPE.event(resource));
                return events;
            default:
                return Collections.emptySet();
        }
    }
}
