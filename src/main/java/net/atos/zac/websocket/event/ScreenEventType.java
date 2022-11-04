/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import static net.atos.zac.event.Opcode.DELETED;
import static net.atos.zac.event.Opcode.UPDATED;

import java.net.URI;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.Opcode;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;
import net.atos.zac.signalering.model.Signalering;

/**
 * Enumeration of the type of objects that can be referenced by a {@link ScreenEvent} event.
 * <p>
 * Maps to object-type.ts
 */
public enum ScreenEventType {

    ENKELVOUDIG_INFORMATIEOBJECT {
        @Override
        public ScreenEvent event(final Opcode opcode,
                final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
            return instance(opcode, this, enkelvoudigInformatieobject);
        }
    },

    SIGNALERINGEN {
        @Override
        public ScreenEvent event(final Opcode opcode,
                final Signalering signalering) {
            return instance(opcode, this, signalering);
        }
    },

    TAAK {
        @Override
        public ScreenEvent event(final Opcode opcode, final TaskInfo taskInfo) {
            return instance(opcode, this, taskInfo);
        }
    },

    ZAAK {
        @Override
        public ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_INFORMATIEOBJECTEN {
        @Override
        public ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_ROLLEN {
        @Override
        public ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_TAKEN {
        @Override
        public ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ANY;

    public static Set<ScreenEventType> any() {
        return EnumSet.complementOf(EnumSet.of(ANY));
    }

    // This is the factory method.
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final String id,
            final String detail) {
        return new ScreenEvent(opcode, type, new ScreenEventId(id, detail));
    }

    // In these methods you determine what is used as an id, make sure that this is consistent with the other methods
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final UUID uuid,
            final UUID detail) {
        return instance(opcode, type,
                        uuid.toString(),
                        detail != null ? detail.toString() : null);
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final URI url,
            final URI detail) {
        return instance(opcode, type,
                        URIUtil.parseUUIDFromResourceURI(url),
                        detail != null ? URIUtil.parseUUIDFromResourceURI(detail) : null);
    }

    // These methods determine what is used as an id, so that it is the same everywhere
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final Zaak zaak) {
        return instance(opcode, type, zaak.getUuid(), null);
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(opcode, type, enkelvoudigInformatieobject.getUrl(), null);
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final TaskInfo taak) {
        return instance(opcode, type, taak.getId(), null);
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type,
            final Signalering signalering) {
        return instance(opcode, type, signalering.getTarget(), null);
    }

    // These methods determine on which object types the different arguments are allowed
    private ScreenEvent event(final Opcode opcode, final UUID uuid) {
        return instance(opcode, this, uuid, null); // Allowed with all object types
    }

    private ScreenEvent event(final Opcode opcode, final URI url) {
        return instance(opcode, this, url, null); // Allowed with all object types
    }

    private ScreenEvent event(final Opcode opcode,
            final Notificatie.ResourceInfo resource,
            final Notificatie.ResourceInfo detail) {
        return instance(opcode, this,
                        resource.getUrl(),
                        detail != null ? detail.getUrl() : null); // Allowed with all object types
    }

    public ScreenEvent event(final Opcode opcode, final Zaak zaak) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    public ScreenEvent event(final Opcode opcode, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    public ScreenEvent event(final Opcode opcode, final Signalering signalalering) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    public ScreenEvent event(final Opcode opcode, final TaskInfo taskInfo) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    // These are factory methods to create handy and unambiguous ScreenEvents for an object type
    // Note that there are no "created" factory methods as there will never be a listener for those (the new objectId is unknown client side).

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other modification methods.
     *
     * @param uuid identification of the modified object.
     * @return instance of the event
     */
    public final ScreenEvent updated(final UUID uuid) {
        return event(UPDATED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct URI. Preferably use the other creation methods.
     *
     * @param url identification of the modified object.
     * @return instance of the event
     */
    public final ScreenEvent updated(final URI url) {
        return event(UPDATED, url);
    }

    /**
     * Factory method for ScreenEvent (with case identification).
     *
     * @param zaak modified zaak.
     * @return instance of the event
     */
    public final ScreenEvent updated(final Zaak zaak) {
        return event(UPDATED, zaak);
    }

    /**
     * Factory method for ScreenEvent (identifying a single Information object).
     *
     * @param enkelvoudigInformatieobject modified enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenEvent updated(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(UPDATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenEvent (with identification of a signalering target).
     *
     * @param signalering a created or deleted signalering
     * @return instance of the event
     */
    public final ScreenEvent updated(final Signalering signalering) {
        return event(UPDATED, signalering);
    }

    /**
     * Factory method for ScreenEvent (with identification of a task).
     *
     * @param taskInfo modified task
     * @return instance of the event
     */
    public final ScreenEvent updated(final TaskInfo taskInfo) {
        return event(UPDATED, taskInfo);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other deletion methods.
     *
     * @param uuid identification of the deleted object.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final UUID uuid) {
        return event(DELETED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct URI. Preferably use the other creation methods.
     *
     * @param url identificatioon of the deleted object.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final URI url) {
        return event(DELETED, url);
    }

    /**
     * Factory method for ScreenEvent (with case identification).
     *
     * @param zaak deleted zaak.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final Zaak zaak) {
        return event(DELETED, zaak);
    }

    /**
     * Factory method for ScreenEvent (with case identification).
     *
     * @param enkelvoudigInformatieobject deleted enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(DELETED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenEvent (with identification of a task).
     *
     * @param taak deleted task.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final TaskInfo taak) {
        return event(DELETED, taak);
    }

    private void addEvent(final Set<ScreenEvent> events, final Notificatie.ResourceInfo resource,
            final Notificatie.ResourceInfo detail) {
        switch (resource.getAction()) {
            case CREATE:
                // There cannot be any websockets listeners for Opcode.CREATED, so don't send the event.
                // (The new objectId would have to be known client side before it exists to subscribe to it. ;-)
                break;
            case UPDATE:
                events.add(event(UPDATED, resource, detail));
                break;
            case DELETE:
                events.add(event(DELETED, resource, detail));
                break;
            default:
                break;
        }
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<ScreenEvent> getEvents(final Channel channel, final Notificatie.ResourceInfo mainResource,
            final Notificatie.ResourceInfo resource) {
        final Set<ScreenEvent> events = new HashSet<>();
        switch (channel) {
            case INFORMATIEOBJECTEN:
                switch (resource.getType()) {
                    case INFORMATIEOBJECT:
                        ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT.addEvent(events, resource, null);
                        break;
                    case GEBRUIKSRECHTEN:
                        ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT.addEvent(events, mainResource, resource);
                        break;
                }
                break;
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        ScreenEventType.ZAAK.addEvent(events, resource, null);
                        break;
                    case STATUS:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                    case ZAAKOBJECT:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                    case ZAAKINFORMATIEOBJECT:
                        ScreenEventType.ZAAK_INFORMATIEOBJECTEN.addEvent(events, mainResource, resource);
                        break;
                    case ZAAKEIGENSCHAP:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                    case KLANTCONTACT:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                    case ROL:
                        ScreenEventType.ZAAK_ROLLEN.addEvent(events, mainResource, resource);
                        break;
                    case RESULTAAT:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                    case ZAAKBESLUIT:
                        ScreenEventType.ZAAK.addEvent(events, mainResource, resource);
                        break;
                }
                break;
        }
        return events;
    }
}
