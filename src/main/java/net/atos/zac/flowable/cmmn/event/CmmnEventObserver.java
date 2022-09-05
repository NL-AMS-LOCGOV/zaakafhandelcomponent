/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.event.AbstractEventObserver;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

/**
 * Deze bean luistert naar CmmnUpdateEvents, en werkt daar vervolgens flowable mee bij.
 */
@ManagedBean
public class CmmnEventObserver extends AbstractEventObserver<CmmnEvent> {

    private static final Logger LOG = Logger.getLogger(CmmnEventObserver.class.getName());

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private CaseService caseService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Override
    public void onFire(final @ObservesAsync CmmnEvent event) {
        try {
            startZaakAfhandeling(zrcClientService.readZaak(event.getObjectId()));
        } catch (final Throwable ex) {
            LOG.log(Level.SEVERE, "asynchronous guard", ex);
        }
    }

    private void startZaakAfhandeling(final Zaak zaak) {
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(zaak);
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        if (zaakafhandelParameters.getCaseDefinitionID() != null) {
            final String caseDefinitionKey = zaakafhandelParameters.getCaseDefinitionID();
            LOG.info(() -> String.format("Zaak %s: Starten Case definition '%s'", zaak.getUuid(), caseDefinitionKey));
            caseService.startCase(caseDefinitionKey, zaak, zaaktype);
        } else {
            LOG.warning(String.format("Zaaktype '%s': Geen zaakafhandelParameters gevonden", zaaktype.getIdentificatie()));
        }
    }


}
