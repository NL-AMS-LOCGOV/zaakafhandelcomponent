/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeration of the type of objects that can be referenced by a {@link CacheEvent} event.
 */
public enum CacheEventType {
    ZAAKROL {
        @Override
        public final CacheEvent event(final Rol<?> rol) {
            return instance(this, rol);
        }
    },
    ZAAKSTATUS {
        @Override
        public final CacheEvent event(final Status status) {
            return instance(this, status);
        }
    },
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

    // These methods determine what is used as an id, so that it is the same everywhere
    private static CacheEvent instance(final CacheEventType objectType, final Rol<?> rol) {
        return instance(objectType, rol.getZaak());
    }

    private static CacheEvent instance(final CacheEventType objectType, final Status status) {
        return instance(objectType, status.getUrl());
    }

    private static CacheEvent instance(final CacheEventType objectType, final Zaaktype zaaktype) {
        return instance(objectType, zaaktype.getUrl());
    }

    // These methods determine on which object types the different arguments are allowed
    private CacheEvent event(final Notificatie.ResourceInfo resource) {
        return instance(this, resource.getUrl()); // Allowed with all object types
    }

    public CacheEvent event(final Rol<?> rol) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    public CacheEvent event(final Status status) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
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
                    case STATUS:
                        switch (resource.getAction()) {
                            case UPDATE:
                            case DELETE:
                                events.add(CacheEventType.ZAAKSTATUS.event(resource));
                                break;
                            default:
                                break;
                        }
                    default:
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
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                }
            default:
                break;
        }
        return events;
    }
}
