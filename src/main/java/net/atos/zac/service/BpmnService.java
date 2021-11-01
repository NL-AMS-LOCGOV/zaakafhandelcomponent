/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.flowable.cdi.BusinessProcess;

import net.atos.zac.flowable.bpmn.ProcessContext;

/**
 *
 */
@Stateless
public class BpmnService {

    private static final Logger LOG = Logger.getLogger(BpmnService.class.getName());

    @Inject
    private BusinessProcess businessProcess;

    private void starProces(final String procesNaam, final UUID zaakUuid) {
        LOG.info(() -> String.format("Starten proces '%s' voor zaak '%s'", procesNaam, zaakUuid));
        final ProcessContext processContext = new ProcessContext();
        processContext.setTrace("startZaak()");
        businessProcess.setVariable("context", processContext);
        businessProcess.startProcessByKey(procesNaam, zaakUuid.toString());
    }

    public void completeTask(final String taakId) {
        businessProcess.startTask(taakId);
        final ProcessContext melding = businessProcess.getVariable("context");
        LOG.info(String.format(">>> Melding: %s", melding.getTrace()));
        melding.setTrace(melding.getTrace() + " completeTask()");
        businessProcess.completeTask();
    }
}
