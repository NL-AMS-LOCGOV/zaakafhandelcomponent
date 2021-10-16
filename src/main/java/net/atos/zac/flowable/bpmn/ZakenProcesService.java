/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.flowable.cdi.annotation.BusinessKey;

import net.atos.client.zgw.shared.ZGWApiService;

/**
 *
 */
@Named("zaken")
public class ZakenProcesService {

    private static final Logger LOG = Logger.getLogger(ZakenProcesService.class.getName());

    @Inject
    @BusinessKey
    private String zaakUUID;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ProcessContext context;

    public void wijzigStatus(final String statustypeOmschrijving) {
        LOG.info(String.format(">>> Trace: %s", context.getTrace()));
        context.setTrace(context.getTrace() + " wijzigStatus()");
        zgwApiService.setZaakstatus(UUID.fromString(zaakUUID), statustypeOmschrijving, "Status gezet door wijzig status node");
        LOG.info(() -> String.format("Status van zaak '%s' gewijzigd naar statustype met omschrijving '%s'", zaakUUID, statustypeOmschrijving));
    }

    public void beeindigZaak(final String resultaattypeOmschrijving) {
        LOG.info(String.format(">>> Trace: %s", context.getTrace()));
        zgwApiService.endZaakWithResultaat(UUID.fromString(zaakUUID), "Status gezet door end node", resultaattypeOmschrijving, "Resultaat gezet door end node");
        LOG.info(() -> String.format("Zaak '%s' beeindigd met resultaattype omschrijving '%s'", zaakUUID, resultaattypeOmschrijving));
    }
}

