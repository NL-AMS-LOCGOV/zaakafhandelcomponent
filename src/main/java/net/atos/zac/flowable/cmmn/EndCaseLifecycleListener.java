/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import java.util.UUID;

import org.flowable.cmmn.api.listener.CaseInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.CaseInstance;

import net.atos.zac.flowable.CaseHandlingService;

/**
 *
 */
public class EndCaseLifecycleListener implements CaseInstanceLifecycleListener {

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
        CaseHandlingService.getHandleService().endZaak(UUID.fromString(caseInstance.getBusinessKey()));
    }
}
