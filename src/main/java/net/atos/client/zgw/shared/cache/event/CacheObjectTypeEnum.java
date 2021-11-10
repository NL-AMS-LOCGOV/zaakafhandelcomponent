/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.atos.zac.notificaties.ChannelEnum;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link CacheUpdateEvent}.
 */
public enum CacheObjectTypeEnum {
    ZAAKROL,
    ZAAKSTATUS,
    ZAAKTYPE;

    private static final Logger LOG = Logger.getLogger(CacheObjectTypeEnum.class.getName());

    // Dit is de uiteindelijke echte factory method
    private static CacheUpdateEvent instance(final CacheObjectTypeEnum objectType, final URI url) {
        return new CacheUpdateEvent(objectType, url);
    }

    private CacheUpdateEvent event(final Notificatie.Resource resource) {
        return instance(this, resource.getUrl());
    }

    public final CacheUpdateEvent changed(final URI url) {
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
    public static Set<CacheUpdateEvent> getEvents(final ChannelEnum channel, final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        final Set<CacheUpdateEvent> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case STATUS:
                        events.add(CacheObjectTypeEnum.ZAAKSTATUS.event(resource));
                        break;
                    case ROL:
                        events.add(CacheObjectTypeEnum.ZAAKROL.event(resource));
                        break;
                }
                break;
            case ZAAKTYPEN:
                switch (resource.getType()) {
                    case ZAAKTYPE:
                        events.add(CacheObjectTypeEnum.ZAAKTYPE.event(resource));
                        break;
                }
                break;
        }
        return events;
    }
}
