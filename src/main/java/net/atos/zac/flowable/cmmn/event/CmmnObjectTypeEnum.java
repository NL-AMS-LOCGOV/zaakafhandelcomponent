/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import static net.atos.zac.event.OpcodeEnum.CREATED;

import java.util.HashSet;
import java.util.Set;

import net.atos.zac.notificaties.ChannelEnum;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link CmmnUpdateEvent}.
 * <p>
 * Dat is er op dit moment maar eentje dus zo eenvoudig mogelijk (maar wel met dezelfde syntax als de andere observers).
 */
public enum CmmnObjectTypeEnum {
    ZAAK;

    public final CmmnUpdateEvent created(final Notificatie.Resource resource) {
        return new CmmnUpdateEvent(CREATED, this, resource.getUrl());
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<CmmnUpdateEvent> getEvents(final ChannelEnum channel, final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        final Set<CmmnUpdateEvent> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        switch (resource.getAction()) {
                            case CREATE:
                                events.add(CmmnObjectTypeEnum.ZAAK.created(resource));
                                break;
                        }
                        break;
                }
                break;
        }
        return events;
    }
}
