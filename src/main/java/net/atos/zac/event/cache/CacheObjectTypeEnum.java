/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event.cache;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.zac.notificaties.ChannelEnum;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link CacheUpdateEvent}.
 */
// TODO Verplaatsen naar waar dit gebruikt worden .../event
public enum CacheObjectTypeEnum {
    APPLICATIE,
    BESLUIT,
    BESLUITTYPE,
    INFORMATIEOBJECT,
    INFORMATIEOBJECTTYPE,
    ROL,
    ZAAK,
    ZAAKTYPE;

    private static final Logger LOG = Logger.getLogger(CacheObjectTypeEnum.class.getName());

    // Dit is de uiteindelijke echte factory method
    private static CacheUpdateEvent instance(final CacheObjectTypeEnum objectType, final UUID uuid) {
        return new CacheUpdateEvent(objectType, uuid);
    }

    private static CacheUpdateEvent instance(final CacheObjectTypeEnum objectType, final URI url) {
        return instance(objectType, URIUtil.parseUUIDFromResourceURI(url));
    }

    private CacheUpdateEvent event(final UUID uuid) {
        return instance(this, uuid);
    }

    private CacheUpdateEvent event(final URI url) {
        return instance(this, url);
    }

    private CacheUpdateEvent event(final Notificatie.Resource resource) {
        return instance(this, resource.getUrl());
    }

    public final CacheUpdateEvent changed(final UUID uuid) {
        return event(uuid);
    }

    public final CacheUpdateEvent changed(final URI url) {
        return event(url);
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
            case AUTORISATIES:
                switch (resource.getType()) {
                    case APPLICATIE:
                        events.add(CacheObjectTypeEnum.APPLICATIE.event(resource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case BESLUITEN:
                switch (resource.getType()) {
                    case BESLUIT:
                        events.add(CacheObjectTypeEnum.BESLUIT.event(resource));
                        break;
                    case BESLUITINFORMATIEOBJECT:
                        events.add(CacheObjectTypeEnum.BESLUIT.event(mainResource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case BESLUITTYPEN:
                switch (resource.getType()) {
                    case BESLUITTYPE:
                        events.add(CacheObjectTypeEnum.BESLUITTYPE.event(resource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case INFORMATIEOBJECTEN:
                switch (resource.getType()) {
                    case INFORMATIEOBJECT:
                        events.add(CacheObjectTypeEnum.INFORMATIEOBJECT.event(resource));
                        break;
                    case GEBRUIKSRECHTEN:
                        events.add(CacheObjectTypeEnum.INFORMATIEOBJECT.event(mainResource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case INFORMATIEOBJECTTYPEN:
                switch (resource.getType()) {
                    case INFORMATIEOBJECTTYPE:
                        events.add(CacheObjectTypeEnum.INFORMATIEOBJECTTYPE.event(resource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        events.add(CacheObjectTypeEnum.ZAAK.event(resource));
                        break;
                    case STATUS:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKOBJECT:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKINFORMATIEOBJECT:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKEIGENSCHAP:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case KLANTCONTACT:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ROL:
                        events.add(CacheObjectTypeEnum.ROL.event(resource));
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case RESULTAAT:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKBESLUIT:
                        events.add(CacheObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case ZAAKTYPEN:
                switch (resource.getType()) {
                    case ZAAKTYPE:
                        events.add(CacheObjectTypeEnum.ZAAKTYPE.event(resource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            default:
                unexpectedChannel(channel);
                break;
        }
        return events;
    }

    private static void unexpectedChannel(final ChannelEnum channel) {
        LOG.warning(String.format("channel %s not implemented", channel));
    }

    private static void unexpectedResource(final ChannelEnum channel, final Notificatie.Resource resource) {
        LOG.warning(String.format("resource %s not implemented on channel %s", resource.getType(), channel));
    }
}
