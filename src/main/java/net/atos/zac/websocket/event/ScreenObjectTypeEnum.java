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
 * Enumeration of the types of objects that can be changed by a {@link ScreenUpdateEvent}.
 * Maps to object-type.ts
 */
public enum ScreenObjectTypeEnum {

    ENKELVOUDIG_INFORMATIEOBJECT {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
            return instance(opcode, this, enkelvoudigInformatieobject);
        }
    },

    TAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final TaskInfo taak) {
            return instance(opcode, this, taak);
        }
    },

    ZAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_INFORMATIEOBJECTEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_ROLLEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    },

    ZAAK_TAKEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum opcode, final Zaak zaak) {
            return instance(opcode, this, zaak);
        }
    };

    private static final Logger LOG = Logger.getLogger(ScreenObjectTypeEnum.class.getName());

    // This is the factory method.
    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type, final String id) {
        return new ScreenUpdateEvent(opcode, type, id);
    }

    // In these methods you determine what is used as an id, make sure that this is consistent with the other methods
    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type, final UUID uuid) {
        return instance(opcode, type, uuid.toString());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type, final URI url) {
        return instance(opcode, type, URIUtil.parseUUIDFromResourceURI(url));
    }

    // These methods determine what is used as an id, so that it is the same everywhere
    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type, final Zaak zaak) {
        return instance(opcode, type, zaak.getUuid());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(opcode, type, enkelvoudigInformatieobject.getUrl());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum opcode, final ScreenObjectTypeEnum type, final TaskInfo taak) {
        return instance(opcode, type, taak.getId());
    }

    // These methods determine on which object types the different arguments are allowed
    private ScreenUpdateEvent event(final OpcodeEnum opcode, final UUID uuid) {
        return instance(opcode, this, uuid); // Allowed with all object types
    }

    private ScreenUpdateEvent event(final OpcodeEnum opcode, final URI url) {
        return instance(opcode, this, url); // Allowed with all object types
    }

    private ScreenUpdateEvent event(final OpcodeEnum opcode, final Notificatie.Resource resource) {
        return instance(opcode, this, resource.getUrl()); // Allowed with all object types
    }

    protected ScreenUpdateEvent event(final OpcodeEnum opcode, final Zaak zaak) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    protected ScreenUpdateEvent event(final OpcodeEnum opcode, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    protected ScreenUpdateEvent event(final OpcodeEnum opcode, final TaskInfo taak) {
        throw new IllegalArgumentException(); // Not allowed except for object types where this method has an override
    }

    // These are factory methods to create handy and unambiguous ScreenUpdateEvents for an object type

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other creation methods.
     *
     * @param uuid indentificatie of the created object
     * @return instance of the event
     */
    public final ScreenUpdateEvent created(final UUID uuid) {
        return event(CREATED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other creation methods.
     *
     * @param url indentificatie of the created object
     * @return instance of the event
     */
    public final ScreenUpdateEvent created(final URI url) {
        return event(CREATED, url);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak created zaak.
     * @return instance of the event
     */
    public final ScreenUpdateEvent created(final Zaak zaak) {
        return event(CREATED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (identifying a single Information object).
     *
     * @param enkelvoudigInformatieobject created enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenUpdateEvent created(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(CREATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taak created task.
     * @return instance of the event
     */
    public final ScreenUpdateEvent created(final TaskInfo taak) {
        return event(CREATED, taak);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other modification methods.
     *
     * @param uuid identification of the modified object.
     * @return instance of the event
     */
    public final ScreenUpdateEvent updated(final UUID uuid) {
        return event(UPDATED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct URI. Preferably use the other creation methods.
     *
     * @param url identification of the modified object.
     * @return instance of the event
     */
    public final ScreenUpdateEvent updated(final URI url) {
        return event(UPDATED, url);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak modified zaak.
     * @return instance of the event
     */
    public final ScreenUpdateEvent updated(final Zaak zaak) {
        return event(UPDATED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (identifying a single Information object).
     *
     * @param enkelvoudigInformatieobject modified enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenUpdateEvent updated(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(UPDATED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taak modifierd task
     * @return instance of the event
     */
    public final ScreenUpdateEvent updated(final TaskInfo taak) {
        return event(UPDATED, taak);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct UUID. Preferably use the other deletion methods.
     *
     * @param uuid identificatioon of the deleted object.
     * @return instance of the event
     */
    public final ScreenUpdateEvent deleted(final UUID uuid) {
        return event(DELETED, uuid);
    }

    /**
     * Pay attention! If you use this method, you are responsible for providing the correct URI. Preferably use the other creation methods.
     *
     * @param url identificatioon of the deleted object.
     * @return instance of the event
     */
    public final ScreenUpdateEvent deleted(final URI url) {
        return event(DELETED, url);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param zaak deleted zaak.
     * @return instance of the event
     */
    public final ScreenUpdateEvent deleted(final Zaak zaak) {
        return event(DELETED, zaak);
    }

    /**
     * Factory method for ScreenUpdateEvent (with case identification).
     *
     * @param enkelvoudigInformatieobject deleted enkelvoudigInformatieobject.
     * @return instance of the event
     */
    public final ScreenUpdateEvent deleted(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(DELETED, enkelvoudigInformatieobject);
    }

    /**
     * Factory method for ScreenUpdateEvent (with identification of a task).
     *
     * @param taak deleted task.
     * @return instance of the event
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
                        events.add(ScreenObjectTypeEnum.ZAAK_INFORMATIEOBJECTEN.event(mainResource));
                        break;
                    case ZAAKEIGENSCHAP:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case KLANTCONTACT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ROL:
                        events.add(ScreenObjectTypeEnum.ZAAK_ROLLEN.event(mainResource));
                        break;
                    case RESULTAAT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                    case ZAAKBESLUIT:
                        events.add(ScreenObjectTypeEnum.ZAAK.event(mainResource));
                        break;
                }
                break;
        }
        return events;
    }
}
