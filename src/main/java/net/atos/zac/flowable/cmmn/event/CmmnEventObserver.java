/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

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
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

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

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Override
    public void onFire(final @ObservesAsync CmmnEvent event) {
        startZaakAfhandeling(zrcClientService.readZaak(event.getObjectId()));
    }

    private void startZaakAfhandeling(final Zaak zaak) {
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.getZaakafhandelParameters(zaak);
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        if (zaakafhandelParameters != null) {
            final String caseDefinitionKey = zaakafhandelParameters.getCaseDefinitionID();
            LOG.info(() -> String.format("Zaak %s: Starten Case definition '%s'", zaak.getUuid(), caseDefinitionKey));
            zetZaakBehandelaarOrganisatorischeEenheid(zaak, zaakafhandelParameters);
            flowableService.startCase(caseDefinitionKey, zaak, zaaktype);
        } else {
            LOG.warning(String.format("Zaaktype '%s': Geen zaakafhandelParameters gevonden", zaaktype.getIdentificatie()));
        }
    }

    private void zetZaakBehandelaarOrganisatorischeEenheid(final Zaak zaak, final ZaakafhandelParameters zaakafhandelParameters) {
        LOG.info(String.format("Zaak %s: toegekend aan groep '%s'", zaak.getUuid(), zaakafhandelParameters.getGroepID()));
        final Group group = flowableService.readGroup(zaakafhandelParameters.getGroepID());
        final OrganisatorischeEenheid organisatorischeEenheid = new OrganisatorischeEenheid();
        organisatorischeEenheid.setIdentificatie(group.getId());
        organisatorischeEenheid.setNaam(group.getName());
        final Roltype roltype = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR);
        final RolOrganisatorischeEenheid rol = new RolOrganisatorischeEenheid(zaak.getUrl(), roltype.getUrl(), ORGANISATORISCHE_EENHEID_ROL_TOELICHTING,
                                                                              organisatorischeEenheid);
        zrcClientService.createRol(rol);
    }
}
