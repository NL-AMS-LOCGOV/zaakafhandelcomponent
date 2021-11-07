/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static java.lang.String.format;
import static net.atos.zac.websocket.event.SchermObjectTypeEnum.ZAAK;

import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.EventingService;

/**
 * Contains actions that can be called from within a Flowable CMMN Case or BPMN Process.
 */
@ApplicationScoped
public class CaseHandlingService {

    private static final Logger LOG = Logger.getLogger(CaseHandlingService.class.getName());

    private static final String EINDSTATUS_TOELICHTING = "Zaak beeindigd vanuit Case";

    private static final String STATUS_TOELICHTING = "Status gewijizgd vanuit Case";

    private static final String RESULTAAT_TOELICHTING = "Resultaat gewijizgd vanuit Case";

    @Inject
    private FlowableService flowableService;

    @Inject
    private EventingService eventingService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    public static CaseHandlingService getHandleService() {
        return CDI.current().select(CaseHandlingService.class).get();
    }

    public void updateZaak(final String caseInstanceId, final String statustypeOmschrijving, final String resultaattypeOmschrijving) {
        final UUID zaakUUID = flowableService.findZaakUuidForCase(caseInstanceId);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        if (statustypeOmschrijving != null) {
            LOG.info(format("Zaak %s: Verander status in '%s'", zaakUUID, statustypeOmschrijving));
            zgwApiService.createStatusForZaak(zaak, statustypeOmschrijving, STATUS_TOELICHTING);
        }
        if (resultaattypeOmschrijving != null) {
            LOG.info(format("Zaak %s: Verander resultaat in '%s'", zaakUUID, resultaattypeOmschrijving));
            zgwApiService.createResultaatForZaak(zaak, resultaattypeOmschrijving, RESULTAAT_TOELICHTING);
        }
        if (statustypeOmschrijving != null || resultaattypeOmschrijving != null) {
            eventingService.send(ZAAK.wijziging(zaakUUID));
        }
    }

    public void endZaak(final UUID zaakUUID) {
        LOG.info(format("Zaak %s: Beeindig Case", zaakUUID));
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zgwApiService.endZaak(zaak, EINDSTATUS_TOELICHTING);
        eventingService.send(ZAAK.wijziging(zaakUUID));
    }
}
