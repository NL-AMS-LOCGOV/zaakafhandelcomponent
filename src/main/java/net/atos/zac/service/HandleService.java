/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import static net.atos.zac.websocket.event.SchermObjectTypeEnum.ZAAK;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;

/**
 * Contains actions that can be called from within a Flowable CMMN Case or BPMN Process.
 */
@Stateless
public class HandleService {

    private static final Logger LOG = Logger.getLogger(HandleService.class.getName());

    private static final String EJB_LOOKUP_NAME = String.format("java:module/%s", HandleService.class.getSimpleName());

    private static final String EINDSTATUS_TOELICHTING = "Zaak beeindigd vanuit Case";

    private static final String STATUS_TOELICHTING = "Status gewijizgd vanuit Case";

    private static final String RESULTAAT_TOELICHTING = "Resultaat gewijizgd vanuit Case";

    @EJB
    private CmmnService cmmnService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @EJB
    private EventingService eventingService;

    public static HandleService getHandleService() {
        try {
            return InitialContext.doLookup(EJB_LOOKUP_NAME);
        } catch (final NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateZaak(final String caseInstanceId, final String statustypeOmschrijving, final String resultaattypeOmschrijving) {
        final UUID zaakUUID = cmmnService.getZaakUUID(caseInstanceId);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        if (statustypeOmschrijving != null) {
            LOG.info(String.format("Zaak %s: Verander status in '%s'", zaakUUID, statustypeOmschrijving));
            zgwApiService.createStatusForZaak(zaak, statustypeOmschrijving, STATUS_TOELICHTING);
        }
        if (resultaattypeOmschrijving != null) {
            LOG.info(String.format("Zaak %s: Verander resultaat in '%s'", zaakUUID, resultaattypeOmschrijving));
            zgwApiService.createResultaatForZaak(zaak, resultaattypeOmschrijving, RESULTAAT_TOELICHTING);
        }
        if (statustypeOmschrijving != null || resultaattypeOmschrijving != null) {
            eventingService.versturen(ZAAK.wijziging(zaakUUID));
        }
    }

    public void endZaak(final UUID zaakUUID) {
        LOG.info(String.format("Zaak %s: Beeindig Case", zaakUUID));
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zgwApiService.endZaak(zaak, EINDSTATUS_TOELICHTING);
        eventingService.versturen(ZAAK.wijziging(zaakUUID));
    }

    // ToDo: ESUITEDEV-25830 - Wordt aangeroepen vanuit Converter
    public String ophalenZaakBehandelaarGroepId(final URI zaakURI, final URI zaaktypeURI) {
        final Optional<Rol<?>> rol = ophalenZaakBehandelaarId(zaakURI, zaaktypeURI, BetrokkeneType.ORGANISATORISCHE_EENHEID);
        return rol.isPresent() ? ((RolOrganisatorischeEenheid) rol.get()).getBetrokkeneIdentificatie().getIdentificatie() : null;
    }

    // ToDo: ESUITEDEV-25830 - Wordt aangeroepen vanuit Converter
    public String ophalenZaakBehandelaarMedewerkerId(final URI zaakURI, final URI zaaktypeURI) {
        final Optional<Rol<?>> rol = ophalenZaakBehandelaarId(zaakURI, zaaktypeURI, BetrokkeneType.MEDEWERKER);
        return rol.isPresent() ? ((RolMedewerker) rol.get()).getBetrokkeneIdentificatie().getIdentificatie() : null;
    }

    private Optional<Rol<?>> ophalenZaakBehandelaarId(final URI zaakURI, final URI zaaktypeURI, final BetrokkeneType betrokkeneType) {
        final Roltype roltype = ztcClientService.readRoltype(zaaktypeURI, AardVanRol.BEHANDELAAR);
        final RolListParameters rolListParameters = new RolListParameters();
        rolListParameters.setZaak(zaakURI);
        rolListParameters.setBetrokkeneType(betrokkeneType);
        rolListParameters.setRoltype(roltype.getUrl());
        return zrcClientService.listRollen(rolListParameters).getSingleResult();
    }
}
