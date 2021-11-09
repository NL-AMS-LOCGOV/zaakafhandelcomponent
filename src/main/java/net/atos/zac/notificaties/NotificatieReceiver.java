/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static javax.ws.rs.core.Response.noContent;
import static net.atos.zac.notificaties.ActionEnum.CREATE;
import static net.atos.zac.notificaties.ChannelEnum.ZAKEN;
import static net.atos.zac.notificaties.ResourceEnum.ZAAK;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.cache.CacheUpdateEvent;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.cmmn.event.CmmnObjectTypeEnum;
import net.atos.zac.websocket.event.ScreenObjectTypeEnum;

/**
 *
 */
@Path("notificaties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificatieReceiver {

    private static final Logger LOG = Logger.getLogger(NotificatieReceiver.class.getName());

    private static final String KANAAL_ZAKEN = "zaken";

    private static final String RESOURCE_ZAAK = "zaak";

    private static final String ACTIE_CREATE = "create";

    @Inject
    private EventingService eventingService;

    @Inject
    private ZRCClientService zrcClientService;

    @POST
    public Response notificatieReceive(final Notificatie notificatie) {
        LOG.info(() -> String
                .format("Notificatie ontvangen: kanaal='%s', resource='%s', actie='%s', aanmaakdatum='%s'",
                        notificatie.getChannel(), notificatie.getResource(), notificatie.getAction(), notificatie.getCreationDateTime().toString()));
        handleCmmn(notificatie);
        handleCaches(notificatie);
        handleWebsockets(notificatie);
        return noContent().build();
    }

    private void handleCmmn(final Notificatie notificatie) {
        if (ZAKEN == notificatie.getChannel() && ZAAK == notificatie.getResource() && CREATE == notificatie.getAction()) {
            eventingService.send(CmmnObjectTypeEnum.ZAAK.creation(notificatie.getResourceUrl()));
        }
    }

    private void handleCaches(final Notificatie notificatie) {
        // TODO ESUITEDEV-25860 conversie
        eventingService.send((CacheUpdateEvent) null);
    }

    private void handleWebsockets(final Notificatie notificatie) {
        // TODO ESUITEDEV-25860 conversie
        // Bij aanmaken van abonnement in open-notificaties stuurt deze een test notificatie naar kanaal "test". Vandaar de test op kanaal.
        if (KANAAL_ZAKEN.equals(notificatie.getChannel()) && RESOURCE_ZAAK.equals(notificatie.getResource()) && ACTIE_CREATE.equals(notificatie.getAction())) {
            final Zaak zaak = zrcClientService.readZaak(notificatie.getResourceUrl());
            eventingService.send(ScreenObjectTypeEnum.ZAAK.update(zaak));
            eventingService.send(ScreenObjectTypeEnum.ZAAK_BETROKKENEN.update(zaak));
        }
    }
}
