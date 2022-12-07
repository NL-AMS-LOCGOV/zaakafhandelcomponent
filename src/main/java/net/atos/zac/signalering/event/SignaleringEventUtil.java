/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.identity.model.User;
import net.atos.zac.notificaties.Channel;
import net.atos.zac.notificaties.Notificatie;
import net.atos.zac.signalering.model.SignaleringType;

/**
 * There is no SignaleringEvenType. SignaleringType is used for that instead (i.e. the Type enum in it).
 * <p>
 * This util provides the mapping- and factory-methods that would have been in SignaleringEvenType.
 */
public class SignaleringEventUtil {

    private static <ID> SignaleringEvent<ID> instance(final SignaleringType.Type signaleringType, final ID id,
            final ID detail, final User actor) {
        return new SignaleringEvent<>(signaleringType, new SignaleringEventId<ID>(id, detail), actor);
    }

    public static SignaleringEvent<URI> event(final SignaleringType.Type signaleringType, final Zaak zaak,
            final User actor) {
        return instance(signaleringType, zaak.getUrl(), null, actor);
    }

    public static SignaleringEvent<URI> event(final SignaleringType.Type signaleringType,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final User actor) {
        return instance(signaleringType, enkelvoudigInformatieobject.getUrl(), null, actor);
    }

    public static SignaleringEvent<String> event(final SignaleringType.Type signaleringType, final TaskInfo taak,
            final User actor) {
        return instance(signaleringType, taak.getId(), null, actor);
    }

    private static SignaleringEvent<URI> event(final SignaleringType.Type signaleringType,
            final Notificatie.ResourceInfo resource, final Notificatie.ResourceInfo detail) {
        // There is no actor information in notifications
        return instance(signaleringType, resource.getUrl(), detail != null ? detail.getUrl() : null, null);
    }

    /**
     * This is the mapping.
     *
     * @param channel      the channel the notification came in on
     * @param mainResource the involved main resource (may be equal to the resource)
     * @param resource     the actually modified resource
     * @return the set of events that the parameters map to
     */
    public static Set<SignaleringEvent<URI>> getEvents(final Channel channel,
            final Notificatie.ResourceInfo mainResource,
            final Notificatie.ResourceInfo resource) {
        final Set<SignaleringEvent<URI>> events = new HashSet<>();
        switch (channel) {
            case ZAKEN:
                switch (resource.getType()) {
                    case ZAAKINFORMATIEOBJECT:
                        switch (resource.getAction()) {
                            case CREATE:
                                events.add(
                                        event(SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD, mainResource, resource));
                                break;
                        }
                        break;
                    case ROL:
                        switch (resource.getAction()) {
                            case CREATE:
                                events.add(event(SignaleringType.Type.ZAAK_OP_NAAM, resource, null));
                                break;
                        }
                        break;
                }
                break;
        }
        return events;
    }
}
