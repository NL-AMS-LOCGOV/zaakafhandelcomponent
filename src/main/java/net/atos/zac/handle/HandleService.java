/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.handle;

import static net.atos.client.zgw.shared.util.URIUtil.parseUUIDFromResourceURI;
import static net.atos.zac.websocket.event.ObjectTypeEnum.ZAAK;
import static net.atos.zac.websocket.event.ObjectTypeEnum.ZAAK_BETROKKENEN;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Medewerker;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.events.EventingServiceBean;
import net.atos.zac.flowable.cmmn.CmmnService;

/**
 *
 */
@Stateless
public class HandleService {

    private static final Logger LOG = Logger.getLogger(HandleService.class.getName());

    private static final String EJB_LOOKUP_NAME = String.format("java:module/%s", HandleService.class.getSimpleName());

    private static final String EINDSTATUS_TOELICHTING = "Zaak beeindigd vanuit Case";

    private static final String STATUS_TOELICHTING = "Status gewijizgd vanuit Case";

    private static final String RESULTAAT_TOELICHTING = "Resultaat gewijizgd vanuit Case";

    private static final String ORGANISATORISCHE_EENHEID_ROL_TOELICHTING = "Groep verantwoordelijk voor afhandelen zaak";

    private static final String MEDEWERKER_ROL_TOELICHTING = "Medewerker verantwoordelijk voor afhandelen zaak";

    @Inject
    private CmmnService cmmnService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    @Inject
    private EventingServiceBean eventingService;

    public static HandleService getHandleService() {
        try {
            return InitialContext.doLookup(EJB_LOOKUP_NAME);
        } catch (final NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void startZaakAfhandeling(final Notificatie notificatie) {
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

    public void updateZaak(final String caseInstanceId, final String status, final String resultaat) {
        final UUID zaakUUID = cmmnService.getZaakUUID(caseInstanceId);
        if (status != null) {
            LOG.info(String.format("Zaak %s: Verander status in '%s'", zaakUUID, status));
        }
        if (resultaat != null) {
            LOG.info(String.format("Zaak %s: Verander resultaat in '%s'", zaakUUID, resultaat));
        }
        zgwApiService.updateZaak(zaakUUID, status, STATUS_TOELICHTING, resultaat, RESULTAAT_TOELICHTING);
        eventingService.versturen(ZAAK.wijziging(zaakUUID));
    }

    public void endZaak(final UUID zaakUUID) {
        LOG.info(String.format("Zaak %s: Beeindig Case", zaakUUID));
        zgwApiService.endZaak(zaakUUID, EINDSTATUS_TOELICHTING);
        eventingService.versturen(ZAAK.wijziging(zaakUUID));
    }

    public String ophalenZaakBehandelaarGroepId(final URI zaakURI, final URI zaaktypeURI) {
        final Optional<Rol<?>> rol = ophalenZaakBehandelaarId(zaakURI, zaaktypeURI,
                                                              BetrokkeneType.ORGANISATORISCHE_EENHEID);
        return rol.isPresent() ? ((RolOrganisatorischeEenheid) rol.get()).getBetrokkeneIdentificatie()
                .getIdentificatie() : null;
    }

    public String ophalenZaakBehandelaarMedewerkerId(final URI zaakURI, final URI zaaktypeURI) {
        final Optional<Rol<?>> rol = ophalenZaakBehandelaarId(zaakURI, zaaktypeURI, BetrokkeneType.MEDEWERKER);
        return rol.isPresent() ? ((RolMedewerker) rol.get()).getBetrokkeneIdentificatie().getIdentificatie() : null;
    }

    private Optional<Rol<?>> ophalenZaakBehandelaarId(final URI zaakURI, final URI zaaktypeURI, final BetrokkeneType betrokkeneType) {
        final Roltype roltype = ztcClientService.getRoltype(zaaktypeURI, AardVanRol.BEHANDELAAR);
        final RolListParameters rolListParameters = new RolListParameters();
        rolListParameters.setZaak(zaakURI);
        rolListParameters.setBetrokkeneType(betrokkeneType);
        rolListParameters.setRoltype(roltype.getUrl());
        return zrcClient.rolList(rolListParameters).getSingleResult();
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

    public void zetZaakBehandelaarMedewerker(final URI zaakURI, final URI zaaktypeURI, final User user) {
        final UUID zaakUUID = parseUUIDFromResourceURI(zaakURI);
        LOG.info(String.format("Zaak %s: Behandeld door medewerker '%s'", zaakUUID, user.getId()));
        final Medewerker medewerker = new Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setAchternaam(user.getLastName());
        if (StringUtils.isNotBlank(user.getFirstName())) {
            medewerker.setVoorletters(user.getFirstName().substring(0, 1));
        }
        final Roltype roltype = ztcClientService.getRoltype(zaaktypeURI, AardVanRol.BEHANDELAAR);
        final RolMedewerker rol = new RolMedewerker(zaakURI, roltype.getUrl(), MEDEWERKER_ROL_TOELICHTING, medewerker);
        zrcClient.rolCreate(rol);
        eventingService.versturen(ZAAK.wijziging(zaakUUID));
        eventingService.versturen(ZAAK_BETROKKENEN.wijziging(zaakUUID));
    }
}
