/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import static net.atos.zac.event.Opcode.CREATED;

import java.util.HashSet;
import java.util.Set;

import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeration of the type of objects that can be referenced by a {@link CmmnEvent} event.
 * <p>
 * Only one currently, so keeping it simple. But still supporting the same syntax for mapping and construction.
 */
public enum CmmnEventType {
    ZAAK;

    public final CmmnEvent created(final Notificatie.ResourceInfo resource) {
        return new CmmnEvent(CREATED, this, resource.getUrl());
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<CmmnEvent> getEvents(final Channel channel, final Notificatie.ResourceInfo mainResource, final Notificatie.ResourceInfo resource) {
        final Set<CmmnEvent> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        switch (resource.getAction()) {
                            case CREATE:
                                events.add(CmmnEventType.ZAAK.created(resource));
                                break;
                        }
                        break;
                }
                break;
        }
        return events;
    }
}
