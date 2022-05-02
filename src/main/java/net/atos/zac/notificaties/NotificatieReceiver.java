/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static javax.ws.rs.core.Response.noContent;
import static net.atos.zac.notificaties.Action.CREATE;
import static net.atos.zac.notificaties.Action.DELETE;
import static net.atos.zac.notificaties.Action.UPDATE;
import static net.atos.zac.notificaties.Resource.OBJECT;
import static net.atos.zac.notificaties.Resource.STATUS;
import static net.atos.zac.notificaties.Resource.ZAAK;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.atos.client.zgw.shared.cache.event.CacheEventType;
import net.atos.zac.aanvraag.ProductAanvraagService;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.cmmn.event.CmmnEventType;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.websocket.event.ScreenEventType;
import net.atos.zac.zoeken.ZoekenService;

/**
 *
 */
@Path("notificaties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificatieReceiver {

    private static final Logger LOG = Logger.getLogger(NotificatieReceiver.class.getName());

    private static final String PRODUCT_AANVRAAG_KENMERK_KEY = "objectType";

    private static final String PRODUCT_AANVRAAG_KENMERK_VALUE = "objecttypes: ProductAanvraag";

    @Inject
    private EventingService eventingService;

    @Inject
    private ProductAanvraagService productAanvraagService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private ZoekenService zoekenService;

    @POST
    public Response notificatieReceive(final Notificatie notificatie) {
        LOG.info(() -> String
                .format("Notificatie ontvangen: kanaal='%s', resource='%s', actie='%s', aanmaakdatum='%s'",
                        notificatie.getChannel(), notificatie.getResource(), notificatie.getAction(), notificatie.getCreationDateTime().toString()));
        handleCaches(notificatie);
        handleWebsockets(notificatie);
        if (!configuratieService.isLocalDevelopment()) {
            handleSignaleringen(notificatie);
            handleCmmn(notificatie);
            handleProductAanvraag(notificatie);
            handleIndexering(notificatie);
        }
        return noContent().build();
    }

    private void handleCaches(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResource() != null) {
            CacheEventType.getEvents(notificatie.getChannel(), notificatie.getMainResourceInfo(), notificatie.getResourceInfo()).forEach(eventingService::send);
        }
    }

    private void handleWebsockets(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResource() != null) {
            ScreenEventType.getEvents(notificatie.getChannel(), notificatie.getMainResourceInfo(), notificatie.getResourceInfo())
                    .forEach(eventingService::send);
        }
    }

    private void handleSignaleringen(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResource() != null) {
            SignaleringEventUtil.getEvents(notificatie.getChannel(), notificatie.getMainResourceInfo(), notificatie.getResourceInfo())
                    .forEach(eventingService::send);
        }
    }

    private void handleCmmn(final Notificatie notificatie) {
        if (notificatie.getChannel() != null && notificatie.getResource() != null) {
            CmmnEventType.getEvents(notificatie.getChannel(), notificatie.getMainResourceInfo(), notificatie.getResourceInfo()).forEach(eventingService::send);
        }
    }

    private void handleProductAanvraag(final Notificatie notificatie) {
        if (notificatie.getResource() == OBJECT && notificatie.getAction() == CREATE &&
                notificatie.getProperties().containsKey(PRODUCT_AANVRAAG_KENMERK_KEY) &&
                notificatie.getProperties().containsValue(PRODUCT_AANVRAAG_KENMERK_VALUE)) {
            productAanvraagService.createZaak(notificatie.getResourceUrl());
        }
    }

    private void handleIndexering(final Notificatie notificatie) {
        if (notificatie.getChannel() == Channel.ZAKEN) {
            if (notificatie.getResource() == ZAAK) {
                if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                    zoekenService.addZaak(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
                } else if (notificatie.getAction() == DELETE) {
                    zoekenService.removeZaak(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
                }
            } else if (notificatie.getResource() == STATUS && notificatie.getAction() == CREATE) {
                zoekenService.addZaak(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
            }
        }
    }
}
