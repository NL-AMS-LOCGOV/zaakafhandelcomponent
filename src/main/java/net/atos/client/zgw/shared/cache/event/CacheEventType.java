/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;
import net.atos.zac.notificaties.Resource;

/**
 * Enumeration of the type of objects that can be referenced by a {@link CacheEvent} event.
 */
public enum CacheEventType {
    ZAAKTYPE {
        @Override
        public final CacheEvent event(final Zaaktype zaaktype) {
            return instance(this, zaaktype);
        }
    };

    // This is the factory method
    private static CacheEvent instance(final CacheEventType objectType, final URI url) {
        return new CacheEvent(objectType, url);
    }

    private static CacheEvent instance(final CacheEventType objectType, final Zaaktype zaaktype) {
        return instance(objectType, zaaktype.getUrl());
    }

    // These methods determine on which object types the different arguments are allowed
    private CacheEvent event(final Notificatie.ResourceInfo resource) {
        return instance(this, resource.getUrl()); // Allowed with all object types
    }

    public CacheEvent event(final Zaaktype zaaktype) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
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
    public static Set<CacheEvent> getEvents(final Channel channel, final Notificatie.ResourceInfo mainResource, final Notificatie.ResourceInfo resource) {
        final Set<CacheEvent> events = new HashSet<>();

        if (channel == Channel.ZAAKTYPEN && resource.getType() == Resource.ZAAKTYPE) {
            switch (resource.getAction()) {
                case UPDATE, DELETE -> events.add(CacheEventType.ZAAKTYPE.event(resource));
            }
        }

        return events;
    }
}
