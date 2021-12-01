/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static javax.ws.rs.core.Response.noContent;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.atos.client.zgw.shared.cache.event.CacheEventType;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.cmmn.event.CmmnEventType;
import net.atos.zac.websocket.event.ScreenEventType;

/**
 *
 */
@Path("notificaties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificatieReceiver {

    private static final Logger LOG = Logger.getLogger(NotificatieReceiver.class.getName());

    @Inject
    private EventingService eventingService;

    @POST
    public Response notificatieReceive(final Notificatie notificatie) {
        LOG.info(() -> String
                .format("Notificatie ontvangen: kanaal='%s', resource='%s', actie='%s', aanmaakdatum='%s'",
                        notificatie.getChannel(), notificatie.getResourceType(), notificatie.getAction(), notificatie.getCreationDateTime().toString()));
        handleCaches(notificatie);
        handleCmmn(notificatie);
        handleWebsockets(notificatie);
        return noContent().build();
    }

    private void handleCmmn(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResourceType() != null) {
            CmmnEventType.getEvents(notificatie.getChannel(), notificatie.getMainResource(), notificatie.getResource()).forEach(eventingService::send);
        }
    }

    private void handleCaches(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResourceType() != null) {
            CacheEventType.getEvents(notificatie.getChannel(), notificatie.getMainResource(), notificatie.getResource()).forEach(eventingService::send);
        }
    }

    private void handleWebsockets(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResourceType() != null) {
            ScreenEventType.getEvents(notificatie.getChannel(), notificatie.getMainResource(), notificatie.getResource()).forEach(eventingService::send);
        }
    }
}
