/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import static net.atos.zac.event.Opcode.CREATED;
import static net.atos.zac.event.Opcode.DELETED;
import static net.atos.zac.event.Opcode.UPDATED;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.Opcode;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;

/**
 * Enumeration of the events (objecttypes) that can be changed by a {@link ScreenEvent}.
 * Maps to object-type.ts
 */
public enum ScreenEventType {

    ENKELVOUDIG_INFORMATIEOBJECT {
        @Override
        protected ScreenEvent event(final Opcode opcode, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
            return instance(opcode, this, enkelvoudigInformatieobject);
        }
    },

    TAAK {
        @Override
        protected ScreenEvent event(final Opcode opcode, final TaskInfo taskInfo) {
            return instance(opcode, this, taskInfo);
        }
    },

    ZAAK {
        @Override
        protected ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_INFORMATIEOBJECTEN {
        @Override
        protected ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_ROLLEN {
        @Override
        protected ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_TAKEN {
        @Override
        protected ScreenEvent event(final Opcode opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    };

    private static final Logger LOG = Logger.getLogger(ScreenEventType.class.getName());

    // This is the factory method.
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final String id) {
        return new ScreenEvent(opcode, type, id);
    }

    // In these methods you determine what is used as an id, make sure that this is consistent with the other methods
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final UUID uuid) {
        return instance(opcode, type, uuid.toString());
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final URI url) {
        return instance(opcode, type, URIUtil.parseUUIDFromResourceURI(url));
    }

    // These methods determine what is used as an id, so that it is the same everywhere
    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final Zaak zaak) {
        return instance(opcode, type, zaak.getUuid());
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(opcode, type, enkelvoudigInformatieobject.getUrl());
    }

    private static ScreenEvent instance(final Opcode opcode, final ScreenEventType type, final TaskInfo taak) {
        return instance(opcode, type, taak.getId());
    }

    // These methods determine on which object types the different arguments are allowed
    private ScreenEvent event(final Opcode opcode, final UUID uuid) {
        return instance(opcode, this, uuid); // Allowed with all object types
    }

    private ScreenEvent event(final Opcode opcode, final URI url) {
        return instance(opcode, this, url); // Allowed with all object types
    }

    private ScreenEvent event(final Opcode opcode, final Notificatie.Resource resource) {
        return instance(opcode, this, resource.getUrl()); // Allowed with all object types
    }

    protected ScreenEvent event(final Opcode opcode, final Zaak zaak) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    protected ScreenEvent event(final Opcode opcode, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    protected ScreenEvent event(final Opcode opcode, final TaskInfo taskInfo) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    // These are factory methods to create handy and unambiguous ScreenUpdateEvents for an object type

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other creation methods.
     *
     * @param uuid indentificatie of the created object
     * @return instance of the event
     */
    public final ScreenEvent created(final UUID uuid) {
        return event(CREATED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other creation methods.
     *
     * @param url indentificatie of the created object
     * @return instance of the event
     */
    public final ScreenEvent created(final URI url) {
        return event(CREATED, url);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak created zaak.
     * @return instance of the event
     */
    public final ScreenEvent created(final Zaak zaak) {
        return event(CREATED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (identifying a single Information object).
     *
     * @param enkelvoudigInformatieobject created enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenEvent created(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(CREATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taak created task.
     * @return instance of the event
     */
    public final ScreenEvent created(final TaskInfo taak) {
        return event(CREATED, taak);
    }

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
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak modified zaak.
     * @return instance of the event
     */
    public final ScreenEvent updated(final Zaak zaak) {
        return event(UPDATED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (identifying a single Information object).
     *
     * @param enkelvoudigInformatieobject modified enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenEvent updated(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(UPDATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taskInfo modifierd task
     * @return instance of the event
     */
    public final ScreenEvent updated(final TaskInfo taskInfo) {
        return event(UPDATED, taskInfo);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other deletion methods.
     *
     * @param uuid identificatioon of the deleted object.
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
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak deleted zaak.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final Zaak zaak) {
        return event(DELETED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param enkelvoudigInformatieobject deleted enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(DELETED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taak deleted task.
     * @return instance of the event
     */
    public final ScreenEvent deleted(final TaskInfo taak) {
        return event(DELETED, taak);
    }

    private ScreenEvent event(final Notificatie.Resource resource) {
        switch (resource.getAction()) {
            case CREATE:
                return event(CREATED, resource);
            case UPDATE:
                return event(UPDATED, resource);
            case DELETE:
                return event(DELETED, resource);
            default:
                return null;
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
    public static Set<ScreenEvent> getEvents(final Channel channel, final Notificatie.Resource mainResource, final Notificatie.Resource resource) {
        final Set<ScreenEvent> events = new HashSet<>();
        switch (channel) {
            case INFORMATIEOBJECTEN:
                switch (resource.getType()) {
                    case INFORMATIEOBJECT:
                        events.add(ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT.event(resource));
                        break;
                    case GEBRUIKSRECHTEN:
                        events.add(ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT.event(mainResource));
                        break;
                }
                break;
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAK:
                        events.add(ScreenEventType.ZAAK.event(resource));
                        break;
                    case STATUS:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                    case ZAAKOBJECT:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                    case ZAAKINFORMATIEOBJECT:
                        events.add(ScreenEventType.ZAAK_INFORMATIEOBJECTEN.event(mainResource));
                        break;
                    case ZAAKEIGENSCHAP:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                    case KLANTCONTACT:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                    case ROL:
                        events.add(ScreenEventType.ZAAK_ROLLEN.event(mainResource));
                        break;
                    case RESULTAAT:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                    case ZAAKBESLUIT:
                        events.add(ScreenEventType.ZAAK.event(mainResource));
                        break;
                }
                break;
        }
        return events;
    }
}
