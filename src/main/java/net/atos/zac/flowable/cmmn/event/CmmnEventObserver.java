/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import static net.atos.client.zgw.shared.util.URIUtil.parseUUIDFromResourceURI;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.flowable.idm.api.Group;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.event.AbstractEventObserver;
import net.atos.zac.flowable.FlowableService;

/**
 * Deze bean luistert naar CmmnUpdateEvents, en werkt daar vervolgens flowable mee bij.
 */
@ManagedBean
public class CmmnEventObserver extends AbstractEventObserver<CmmnEvent> {

    private static final Logger LOG = Logger.getLogger(CmmnEventObserver.class.getName());

    private static final String ORGANISATORISCHE_EENHEID_ROL_TOELICHTING = "Groep verantwoordelijk voor afhandelen zaak";

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private FlowableService flowableService;

    @Override
    public void onFire(final @ObservesAsync CmmnEvent event) {
        startZaakAfhandeling(zrcClientService.readZaak(event.getObjectId()));
    }

    private void startZaakAfhandeling(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        if (zaaktype.getReferentieproces() != null && StringUtils.isNotEmpty(zaaktype.getReferentieproces().getNaam())) {
            final String caseDefinitionKey = zaaktype.getReferentieproces().getNaam();
            LOG.info(() -> String.format("Zaak %s: Starten Case definition '%s'", zaak.getUuid(), caseDefinitionKey));
            final Group group = flowableService.findGroupForCaseDefinition(caseDefinitionKey);
            zetZaakBehandelaarOrganisatorischeEenheid(zaak.getUrl(), zaaktype.getUrl(), group);
            flowableService.startCase(caseDefinitionKey, zaak, zaaktype);
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
        final Roltype roltype = ztcClientService.readRoltype(zaaktypeURI, AardVanRol.BEHANDELAAR);
        final RolOrganisatorischeEenheid rol = new RolOrganisatorischeEenheid(zaakURI, roltype.getUrl(), ORGANISATORISCHE_EENHEID_ROL_TOELICHTING,
                                                                              organisatorischeEenheid);
        zrcClientService.createRol(rol);
    }
}
