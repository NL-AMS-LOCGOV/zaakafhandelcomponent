/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import static net.atos.zac.event.Opcode.CREATED;
import static net.atos.zac.event.Opcode.DELETED;
import static net.atos.zac.event.Opcode.UPDATED;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.Opcode;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;
import net.atos.zac.signalering.model.SignaleringType;

/**
 * There is no SignaleringEvenType. SignaleringType is used directly for that instead.
 * <p>
 * This util provides the mapping- and factory-methods that would have been in SignaleringEvenType.
 */
public class SignaleringEventUtil {

    private static SignaleringEvent<URI> instance(final Opcode operation, final SignaleringType.Type signaleringType, final URI objectId) {
        return new SignaleringEvent<>(operation, signaleringType, objectId);
    }

    private static SignaleringEvent<String> instance(final Opcode operation, final SignaleringType.Type signaleringType, final String objectId) {
        return new SignaleringEvent<>(operation, signaleringType, objectId);
    }

    private static SignaleringEvent<URI> instance(final Opcode operation, final SignaleringType.Type signaleringType, final Zaak zaak) {
        return instance(operation, signaleringType, zaak.getUrl());
    }

    private static SignaleringEvent<URI> instance(final Opcode operation, final SignaleringType.Type signaleringType,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(operation, signaleringType, enkelvoudigInformatieobject.getUrl());
    }

    private static SignaleringEvent<String> instance(final Opcode operation, final SignaleringType.Type signaleringType, final TaskInfo taak) {
        return instance(operation, signaleringType, taak.getId());
    }

    private static SignaleringEvent<URI> instance(final Opcode operation, final SignaleringType.Type signaleringType, final Notificatie.ResourceInfo resource) {
        return instance(operation, signaleringType, resource.getUrl());
    }

    public static SignaleringEvent<URI> created(final SignaleringType.Type signaleringType, final URI objectId) {
        return instance(CREATED, signaleringType, objectId);
    }

    public static SignaleringEvent<URI> updated(final SignaleringType.Type signaleringType, final URI objectId) {
        return instance(UPDATED, signaleringType, objectId);
    }

    public static SignaleringEvent<URI> deleted(final SignaleringType.Type signaleringType, final URI objectId) {
        return instance(DELETED, signaleringType, objectId);
    }

    public static SignaleringEvent<String> created(final SignaleringType.Type signaleringType, final String objectId) {
        return instance(CREATED, signaleringType, objectId);
    }

    public static SignaleringEvent<String> updated(final SignaleringType.Type signaleringType, final String objectId) {
        return instance(UPDATED, signaleringType, objectId);
    }

    public static SignaleringEvent<String> deleted(final SignaleringType.Type signaleringType, final String objectId) {
        return instance(DELETED, signaleringType, objectId);
    }

    public static SignaleringEvent<URI> created(final SignaleringType.Type signaleringType, final Zaak zaak) {
        return instance(CREATED, signaleringType, zaak);
    }

    public static SignaleringEvent<URI> updated(final SignaleringType.Type signaleringType, final Zaak zaak) {
        return instance(UPDATED, signaleringType, zaak);
    }

    public static SignaleringEvent<URI> deleted(final SignaleringType.Type signaleringType, final Zaak zaak) {
        return instance(DELETED, signaleringType, zaak);
    }

    public static SignaleringEvent<URI> created(final SignaleringType.Type signaleringType, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(CREATED, signaleringType, enkelvoudigInformatieobject);
    }

    public static SignaleringEvent<URI> updated(final SignaleringType.Type signaleringType, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(UPDATED, signaleringType, enkelvoudigInformatieobject);
    }

    public static SignaleringEvent<URI> deleted(final SignaleringType.Type signaleringType, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(DELETED, signaleringType, enkelvoudigInformatieobject);
    }

    public static SignaleringEvent<String> created(final SignaleringType.Type signaleringType, final TaskInfo taak) {
        return instance(CREATED, signaleringType, taak);
    }

    public static SignaleringEvent<String> updated(final SignaleringType.Type signaleringType, final TaskInfo taak) {
        return instance(UPDATED, signaleringType, taak);
    }

    public static SignaleringEvent<String> deleted(final SignaleringType.Type signaleringType, final TaskInfo taak) {
        return instance(DELETED, signaleringType, taak);
    }

    public static SignaleringEvent<URI> created(final SignaleringType.Type signaleringType, final Notificatie.ResourceInfo resource) {
        return instance(CREATED, signaleringType, resource);
    }

    public static SignaleringEvent<URI> updated(final SignaleringType.Type signaleringType, final Notificatie.ResourceInfo resource) {
        return instance(UPDATED, signaleringType, resource);
    }

    public static SignaleringEvent<URI> deleted(final SignaleringType.Type signaleringType, final Notificatie.ResourceInfo resource) {
        return instance(DELETED, signaleringType, resource);
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<SignaleringEvent<URI>> getEvents(final Channel channel, final Notificatie.ResourceInfo mainResource,
            final Notificatie.ResourceInfo resource) {
        final Set<SignaleringEvent<URI>> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case ROL:
                        switch (resource.getAction()) {
                            case CREATE:
                                events.add(created(SignaleringType.Type.ZAAK_OP_NAAM, resource));
                                break;
                        }
                        break;
                }
                break;
        }
        return events;
    }
}
