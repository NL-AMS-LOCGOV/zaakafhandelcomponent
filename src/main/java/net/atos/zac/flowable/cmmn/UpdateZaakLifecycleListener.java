/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static org.apache.commons.lang3.StringUtils.substringBetween;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.common.engine.api.delegate.Expression;

import net.atos.zac.flowable.CaseHandlingService;

/**
 *
 */
public class UpdateZaakLifecycleListener implements PlanItemInstanceLifecycleListener {

    private static final String NAAM_STATUS_OPEN = "[";

    private static final String NAAM_STATUS_CLOSE = "]";

    private static final String NAAM_RESULTAAT_OPEN = "{";

    private static final String NAAM_RESULTAAT_CLOSE = "}";

    private String sourceState;

    private String targetState;

    private Expression statusExpression;

    private Expression resultaatExpression;

    /**
     * Default constructor used when explicitly added to a PlanItem in the Case diagram
     */
    public UpdateZaakLifecycleListener() {
    }

    /**
     * Constructor used when implicitly called when added to CMMN Engine Configuration
     */
    public UpdateZaakLifecycleListener(final String sourceState, final String targetState) {
        this.sourceState = sourceState;
        this.targetState = targetState;
    }

    public void setStatus(final Expression status) {
        statusExpression = status;
    }

    public void setResultaat(final Expression resultaat) {
        resultaatExpression = resultaat;
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
    public void stateChanged(final DelegatePlanItemInstance planItemInstance, final String oldState, final String newState) {
        final String status = statusExpression != null ? statusExpression.getValue(planItemInstance).toString() : extractStatusFromName(
                planItemInstance.getName());
        final String resultaat = resultaatExpression != null ? resultaatExpression.getValue(planItemInstance).toString() : extractResultaatFromName(
                planItemInstance.getName());
        if (status != null || resultaat != null) {
            CaseHandlingService.getHandleService().updateZaak(planItemInstance.getCaseInstanceId(), status, resultaat);
        }
    }

    private String extractStatusFromName(final String name) {
        return substringBetween(name, NAAM_STATUS_OPEN, NAAM_STATUS_CLOSE);
    }

    private String extractResultaatFromName(final String name) {
        return substringBetween(name, NAAM_RESULTAAT_OPEN, NAAM_RESULTAAT_CLOSE);
    }
}
