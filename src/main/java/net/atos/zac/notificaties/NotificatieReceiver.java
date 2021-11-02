/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static javax.ws.rs.core.Response.noContent;
import static net.atos.client.zgw.shared.util.URIUtil.parseUUIDFromResourceURI;
import static net.atos.zac.websocket.event.ObjectTypeEnum.ZAAK;
import static net.atos.zac.websocket.event.ObjectTypeEnum.ZAAK_BETROKKENEN;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.idm.api.Group;

import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.service.CmmnService;
import net.atos.zac.service.EventingService;

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

    private static final String ORGANISATORISCHE_EENHEID_ROL_TOELICHTING = "Groep verantwoordelijk voor afhandelen zaak";

    @EJB
    private EventingService eventingService;

    @EJB
    private CmmnService cmmnService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    @POST
    public Response notificatieReceive(final Notificatie notificatie) {
        LOG.info(() -> String
                .format("Notificatie ontvangen: kanaal='%s', resource='%s', actie='%s', aanmaakdatum='%s'",
                        notificatie.getKanaal(), notificatie.getResource(), notificatie.getActie(), notificatie.getAanmaakdatum().toString()));
        // Bij aanmaken van abonnement in open-notificaties stuurt deze een test notificatie naar kanaal "test". Vandaar de test op kanaal.
        if (KANAAL_ZAKEN.equals(notificatie.getKanaal()) && RESOURCE_ZAAK.equals(notificatie.getResource()) && ACTIE_CREATE.equals(notificatie.getActie())) {
            startZaakAfhandeling(notificatie);
        }
        return noContent().build();
    }

    private void startZaakAfhandeling(final Notificatie notificatie) {
        final Zaak zaak = zrcClientService.getZaak(notificatie.getHoofdObject());
        final Zaaktype zaaktype = ztcClientService.getZaaktype(zaak.getZaaktype());
        if (zaaktype.getReferentieproces() != null && StringUtils.isNotEmpty(zaaktype.getReferentieproces().getNaam())) {
            final String caseDefinitionKey = zaaktype.getReferentieproces().getNaam();
            LOG.info(() -> String.format("Zaak %s: Starten Case definition '%s'", zaak.getUuid(), caseDefinitionKey));
            final Group group = cmmnService.getZaakBehandelaarGroup(caseDefinitionKey);
            zetZaakBehandelaarOrganisatorischeEenheid(zaak.getUrl(), zaaktype.getUrl(), group);
            cmmnService.startCase(caseDefinitionKey, zaak, zaaktype);
            eventingService.versturen(ZAAK.wijziging(zaak));
        } else {
            LOG.warning(String.format("Zaaktype '%s': Geen referentie proces gevonden", zaaktype.getIdentificatie()));
        }
    }

    private void zetZaakBehandelaarOrganisatorischeEenheid(final URI zaakURI, final URI zaaktypeURI, final Group group) {
        final UUID zaakUUID = parseUUIDFromResourceURI(zaakURI);
        LOG.info(String.format("Zaak %s: Behandeld door groep '%s'", zaakUUID, group.getId()));
        final OrganisatorischeEenheid organisatorischeEenheid = new OrganisatorischeEenheid();
        organisatorischeEenheid.setIdentificatie(group.getId());
        organisatorischeEenheid.setNaam(group.getName());
        final Roltype roltype = ztcClientService.getRoltype(zaaktypeURI, AardVanRol.BEHANDELAAR);
        final RolOrganisatorischeEenheid rol = new RolOrganisatorischeEenheid(zaakURI, roltype.getUrl(), ORGANISATORISCHE_EENHEID_ROL_TOELICHTING,
                                                                              organisatorischeEenheid);
        zrcClient.rolCreate(rol);
        eventingService.versturen(ZAAK.wijziging(zaakUUID));
        eventingService.versturen(ZAAK_BETROKKENEN.wijziging(zaakUUID));
    }
}
