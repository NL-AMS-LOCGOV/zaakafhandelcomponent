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
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.atos.client.or.objecttype.ObjecttypesClientService;
import net.atos.zac.aanvraag.ProductAanvraagService;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.SecurityUtil;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.cmmn.event.CmmnEventType;
import net.atos.zac.signalering.event.SignaleringEventUtil;
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

    private static final String OBJECTTYPE_KENMERK = "objectType";

    private static final String PRODUCTAANVRAAG_OBJECTTYPE_NAME = "ProductAanvraag";

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

    @Inject
    private ObjecttypesClientService objecttypesClientService;

    @Inject
    @ConfigProperty(name = "OPEN_NOTIFICATIONS_API_SECRET_KEY")
    private String secret;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @POST
    public Response notificatieReceive(@Context HttpHeaders headers, final Notificatie notificatie) {
        SecurityUtil.setFunctioneelGebruiker(httpSession.get());
        if (isAuthenticated(headers)) {
            LOG.info(() -> String.format("Notificatie ontvangen: %s", notificatie.toString()));
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
        return secret.equals(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
    }

    private void handleZaaktype(final Notificatie notificatie) {
        if (notificatie.getResource() == ZAAKTYPE) {
            if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                zaakafhandelParameterBeheerService.zaaktypeAangepast(notificatie.getResourceUrl());
            }
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
        if (isProductAanvraag(notificatie)) {
            productAanvraagService.verwerkProductAanvraag(notificatie.getResourceUrl());
        }
    }

    private boolean isProductAanvraag(final Notificatie notificatie) {
        final String producttypeUri = notificatie.getProperties().get(OBJECTTYPE_KENMERK);
        if (notificatie.getResource() != OBJECT || notificatie.getAction() != CREATE || isEmpty(producttypeUri)) {
            return false;
        }
        return PRODUCTAANVRAAG_OBJECTTYPE_NAME.equals(objecttypesClientService.readObjecttype(uuidFromURI(producttypeUri)).getName());
    }

    private void handleIndexering(final Notificatie notificatie) {
        if (notificatie.getChannel() == Channel.ZAKEN) {
            if (notificatie.getResource() == ZAAK) {
                if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                    indexeerService.addZaak(uuidFromURI(notificatie.getResourceUrl()), notificatie.getAction() == UPDATE);
                } else if (notificatie.getAction() == DELETE) {
                    indexeerService.removeZaak(uuidFromURI(notificatie.getResourceUrl()));
                }
            } else if (notificatie.getResource() == STATUS || notificatie.getResource() == RESULTAAT || notificatie.getResource() == ROL) {
                indexeerService.addZaak(uuidFromURI(notificatie.getMainResourceUrl()), false);
            }
        }
        if (notificatie.getChannel() == Channel.INFORMATIEOBJECTEN) {
            if (notificatie.getResource() == INFORMATIEOBJECT) {
                if (notificatie.getAction() == CREATE || notificatie.getAction() == UPDATE) {
                    indexeerService.addInformatieobject(uuidFromURI(notificatie.getResourceUrl()));
                } else if (notificatie.getAction() == DELETE) {
                    indexeerService.removeInformatieobject(uuidFromURI(notificatie.getResourceUrl()));
                }
            }
        }
    }

    private void handleInboxDocumenten(final Notificatie notificatie) {
        if (notificatie.getAction() == CREATE) {
            if (notificatie.getResource() == INFORMATIEOBJECT) {
                inboxDocumentenService.create(uuidFromURI(notificatie.getResourceUrl()));
            } else if (notificatie.getResource() == ZAAKINFORMATIEOBJECT) {
                inboxDocumentenService.delete(uuidFromURI(notificatie.getResourceUrl()));
            }
        }
    }
}
