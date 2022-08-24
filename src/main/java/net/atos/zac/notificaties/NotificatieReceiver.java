/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static javax.ws.rs.core.Response.noContent;
import static net.atos.zac.notificaties.Action.CREATE;
import static net.atos.zac.notificaties.Action.DELETE;
import static net.atos.zac.notificaties.Action.UPDATE;
import static net.atos.zac.notificaties.Resource.INFORMATIEOBJECT;
import static net.atos.zac.notificaties.Resource.OBJECT;
import static net.atos.zac.notificaties.Resource.RESULTAAT;
import static net.atos.zac.notificaties.Resource.ROL;
import static net.atos.zac.notificaties.Resource.STATUS;
import static net.atos.zac.notificaties.Resource.ZAAK;
import static net.atos.zac.notificaties.Resource.ZAAKINFORMATIEOBJECT;
import static net.atos.zac.notificaties.Resource.ZAAKTYPE;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.client.zgw.shared.cache.event.CacheEventType;
import net.atos.zac.aanvraag.ProductAanvraagService;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.cmmn.event.CmmnEventType;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.websocket.event.ScreenEventType;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zoeken.IndexeerService;

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
    private IndexeerService indexeerService;

    @Inject
    private InboxDocumentenService inboxDocumentenService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    // Injection doesn't work? Timing?
    private static final String OPEN_NOTIFICATIONS_API_SECRET_KEY = ConfigProvider.getConfig().getValue("open.notifications.api.secret.key", String.class);

    @POST
    public Response notificatieReceive(@Context HttpHeaders headers, final Notificatie notificatie) {
        if (isAuthenticated(headers)) {
            LOG.info(() -> String.format("Notificatie ontvangen: %s", notificatie.toString()));
            handleCaches(notificatie);
            handleWebsockets(notificatie);
            if (!configuratieService.isLocalDevelopment()) {
                handleSignaleringen(notificatie);
                handleCmmn(notificatie);
                handleProductAanvraag(notificatie);
                handleIndexering(notificatie);
                handleInboxDocumenten(notificatie);
                handleZaaktype(notificatie);
            }
            return noContent().build();
        } else {
            return noContent().status(Response.Status.FORBIDDEN).build();
        }
    }

    private boolean isAuthenticated(final HttpHeaders headers) {
        return OPEN_NOTIFICATIONS_API_SECRET_KEY.equals(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
    }

    private void handleZaaktype(final Notificatie notificatie) {
        if (notificatie.getResource() == ZAAKTYPE) {
            if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                zaakafhandelParameterBeheerService.zaaktypeAangepast(notificatie.getResourceUrl());
            }
        }
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
            productAanvraagService.verwerkProductAanvraag(notificatie.getResourceUrl());
        }
    }

    private void handleIndexering(final Notificatie notificatie) {
        if (notificatie.getChannel() == Channel.ZAKEN) {
            if (notificatie.getResource() == ZAAK) {
                if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                    indexeerService.addZaak(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
                } else if (notificatie.getAction() == DELETE) {
                    indexeerService.removeZaak(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
                }
            } else if (notificatie.getResource() == STATUS || notificatie.getResource() == RESULTAAT || notificatie.getResource() == ROL) {
                indexeerService.addZaak(UriUtil.uuidFromURI(notificatie.getMainResourceUrl()));
            }
        }
    }

    private void handleInboxDocumenten(final Notificatie notificatie) {
        if (notificatie.getAction() == CREATE) {
            if (notificatie.getResource() == INFORMATIEOBJECT) {
                inboxDocumentenService.create(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
            } else if (notificatie.getResource() == ZAAKINFORMATIEOBJECT) {
                inboxDocumentenService.delete(UriUtil.uuidFromURI(notificatie.getResourceUrl()));
            }
        }
    }
}
