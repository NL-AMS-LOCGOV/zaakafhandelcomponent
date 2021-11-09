/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import static net.atos.zac.event.OpcodeEnum.CREATED;
import static net.atos.zac.event.OpcodeEnum.DELETED;
import static net.atos.zac.event.OpcodeEnum.UPDATED;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.OpcodeEnum;
import net.atos.zac.notificaties.ChannelEnum;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link ScreenUpdateEvent}.
 */
public enum ScreenObjectTypeEnum {

    ENKELVOUDIG_INFORMATIEOBJECT {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
            return instance(action, this, enkelvoudigInformatieobject);
        }
    },

    TAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final TaskInfo taak) {
            return instance(action, this, taak);
        }
    },

    ZAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_INFORMATIEOBJECT {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_TAKEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_BETROKKENEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_ZAKEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    };

    private static final Logger LOG = Logger.getLogger(ScreenObjectTypeEnum.class.getName());

    // Dit is de uiteindelijke echte factory method
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final String id) {
        return new ScreenUpdateEvent(action, type, id);
    }

    // Bij deze methods bepaal je zelf wat er als id gebruikt wordt, let er op dat dit consistent is met de andere methods
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final UUID uuid) {
        return instance(action, type, uuid.toString());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final URI url) {
        return instance(action, type, URIUtil.parseUUIDFromResourceURI(url));
    }

    // Deze methods bepalen wat er als id gebruikt wordt, zodat dit overal hetzelfde is
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final Zaak zaak) {
        return instance(action, type, zaak.getUuid());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(action, type, enkelvoudigInformatieobject.getUrl());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final TaskInfo taak) {
        return instance(action, type, taak.getId());
    }

    // Deze methods bepalen op welke object types de verschillende argumenten zijn toegestaan
    private ScreenUpdateEvent event(final OpcodeEnum action, final UUID uuid) {
        return instance(action, this, uuid); // Toegestaan bij alle objecttypes
    }

    private ScreenUpdateEvent event(final OpcodeEnum action, final URI url) {
        return instance(action, this, url); // Toegestaan bij alle objecttypes
    }

    private ScreenUpdateEvent event(final OpcodeEnum action, final Notificatie.Resource resource) {
        return instance(action, this, resource.getUrl()); // Toegestaan bij alle objecttypes
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final TaskInfo taak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    // Dit zijn factory methods om handig en eenduidig SchermUpdateEvents te maken voor een objecttype

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param uuid de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent created(final UUID uuid) {
        return event(CREATED, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent created(final URI url) {
        return event(CREATED, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de toegevoegde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent created(final Zaak zaak) {
        return event(CREATED, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het toegevoegde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent created(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(CREATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de toegevoegde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent created(final TaskInfo taak) {
        return event(CREATED, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander wijziging methods.
     *
     * @param uuid de indentificatie van het gewijzigde object.
     * @return eem instance van het event
     */
    public final ScreenUpdateEvent updated(final UUID uuid) {
        return event(UPDATED, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het gewijzigde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent updated(final URI url) {
        return event(UPDATED, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de gewijzigde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent updated(final Zaak zaak) {
        return event(UPDATED, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het gewijzigde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent updated(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(UPDATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de gewijzigde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent updated(final TaskInfo taak) {
        return event(UPDATED, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander deletion methods.
     *
     * @param uuid de indentificatie van het verwijderde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deleted(final UUID uuid) {
        return event(DELETED, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het verwijderd object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deleted(final URI url) {
        return event(DELETED, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de verwijderde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deleted(final Zaak zaak) {
        return event(DELETED, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het verwijderde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deleted(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(DELETED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de verwijderde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deleted(final TaskInfo taak) {
        return event(DELETED, taak);
    }

    private ScreenUpdateEvent event(final Notificatie.Resource resource) {
        switch (resource.getAction()) {
            case CREATE:
                return event(CREATED, resource);
            case UPDATE:
                return event(UPDATED, resource);
            case DELETE:
                return event(DELETED, resource);
        }
        return null;
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<ScreenUpdateEvent> getEvents(final ChannelEnum channel, final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        final Set<ScreenUpdateEvent> events = new HashSet<>();
        switch (channel) {
            case INFORMATIEOBJECTEN:
                switch (resource.getType()) {
                    case INFORMATIEOBJECT:
                        events.add(ScreenObjectTypeEnum.ENKELVOUDIG_INFORMATIEOBJECT.event(resource));
                        break;
                    case GEBRUIKSRECHTEN:
                        events.add(ScreenObjectTypeEnum.ENKELVOUDIG_INFORMATIEOBJECT.event(mainResource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(resource));
                        break;
                    case STATUS:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKOBJECT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKINFORMATIEOBJECT:
                        events.add(ScreenObjectTypeEnum.ZAAK_INFORMATIEOBJECT.event(mainResource));
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKEIGENSCHAP:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case KLANTCONTACT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ROL:
                        events.add(ScreenObjectTypeEnum.ZAAK_BETROKKENEN.event(mainResource));
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case RESULTAAT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKBESLUIT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    default:
                        unexpectedResource(channel, resource);
                        break;
                }
                break;
        }
        return events;
    }

    private static void unexpectedResource(final ChannelEnum channel, final Notificatie.Resource resource) {
        LOG.warning(String.format("resource %s not implemented on channel %s", resource.getType(), channel));
    }
}
