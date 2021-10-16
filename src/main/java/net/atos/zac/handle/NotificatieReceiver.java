/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.handle;

import static javax.ws.rs.core.Response.noContent;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @EJB
    private HandleService handleService;

    @POST
    public Response notificatieReceive(final Notificatie notificatie) {
        LOG.info(() -> String
                .format("Notificatie ontvangen: kanaal='%s', resource='%s', actie='%s', aanmaakdatum='%s'",
                        notificatie.getKanaal(), notificatie.getResource(), notificatie.getActie(), notificatie.getAanmaakdatum().toString()));
        // Bij aanmaken van abonnement in open-notificaties stuurt deze een test notificatie naar kanaal "test". Vandaar de test op kanaal.
        if (KANAAL_ZAKEN.equals(notificatie.getKanaal()) && RESOURCE_ZAAK.equals(notificatie.getResource()) && ACTIE_CREATE.equals(notificatie.getActie())) {
            handleService.startZaakAfhandeling(notificatie);
        }
        return noContent().build();
    }
}
