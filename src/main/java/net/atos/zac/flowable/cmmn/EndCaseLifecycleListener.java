/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static java.lang.String.format;

import java.util.UUID;
import java.util.logging.Logger;

import org.flowable.cmmn.api.listener.CaseInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.CaseInstance;

import net.atos.zac.flowable.FlowableHelper;

/**
 *
 */
public class EndCaseLifecycleListener implements CaseInstanceLifecycleListener {

    private static final Logger LOG = Logger.getLogger(EndCaseLifecycleListener.class.getName());

    private static final String EINDSTATUS_TOELICHTING = "Zaak beeindigd vanuit Case";

    private final String sourceState;

    private final String targetState;

    public EndCaseLifecycleListener(final String sourceState, final String targetState) {
        this.sourceState = sourceState;
        this.targetState = targetState;
    }

    @Override
    public String getSourceState() {
        return sourceState;
    }

    @Override
    public String getTargetState() {
        return targetState;
    }

    @Override
    public void stateChanged(final CaseInstance caseInstance, final String oldState, final String newState) {
        final UUID zaakUUID = UUID.fromString(caseInstance.getBusinessKey());
        if (FlowableHelper.getInstance().getZrcClientService().readZaak(zaakUUID).isOpen()) {
            LOG.info(format("Zaak %s: End Zaak", caseInstance.getBusinessKey()));
            FlowableHelper.getInstance().getZgwApiService().endZaak(zaakUUID, EINDSTATUS_TOELICHTING);
        }
    }
}
