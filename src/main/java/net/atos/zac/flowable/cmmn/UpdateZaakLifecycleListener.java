/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import java.util.UUID;
import java.util.logging.Logger;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.common.engine.api.delegate.Expression;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.flowable.FlowableHelper;

/**
 *
 */
public class UpdateZaakLifecycleListener implements PlanItemInstanceLifecycleListener {

    private static final Logger LOG = Logger.getLogger(UpdateZaakLifecycleListener.class.getName());

    private static final String STATUS_TOELICHTING = "Status gewijzigd vanuit Case";

    private static final String RESULTAAT_TOELICHTING = "Resultaat gewijzigd vanuit Case";

    private static final String NAAM_STATUS_OPEN = "[";

    private static final String NAAM_STATUS_CLOSE = "]";

    private static final String NAAM_RESULTAAT_OPEN = "{";

    private static final String NAAM_RESULTAAT_CLOSE = "}";

    private static final String VAR_CASE_NIET_ONTVANKELIJK_TOELICHTING = "nietOntvankelijkToelichting";

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
            updateZaak(planItemInstance.getCaseInstanceId(), status, resultaat);
        }
    }

    private String extractStatusFromName(final String name) {
        return substringBetween(name, NAAM_STATUS_OPEN, NAAM_STATUS_CLOSE);
    }

    private String extractResultaatFromName(final String name) {
        return substringBetween(name, NAAM_RESULTAAT_OPEN, NAAM_RESULTAAT_CLOSE);
    }

    private void updateZaak(final String caseInstanceId, final String statustypeOmschrijving, final String resultaattypeOmschrijving) {
        final UUID zaakUUID = FlowableHelper.getInstance().getFlowableService().readZaakUuidForCase(caseInstanceId);
        final Zaak zaak = FlowableHelper.getInstance().getZrcClientService().readZaak(zaakUUID);
        if (statustypeOmschrijving != null) {
            LOG.info(format("Zaak %s: Change Status to '%s'", zaakUUID, statustypeOmschrijving));
            FlowableHelper.getInstance().getZgwApiService().createStatusForZaak(zaak, statustypeOmschrijving, STATUS_TOELICHTING);
        }
        if (resultaattypeOmschrijving != null) {
            final Object variableValue = FlowableHelper.getInstance().getFlowableService()
                    .readVariableForCase(caseInstanceId, VAR_CASE_NIET_ONTVANKELIJK_TOELICHTING);
            LOG.info(format("Zaak %s: Change Resultaat to '%s'", zaakUUID, resultaattypeOmschrijving));
            FlowableHelper.getInstance().getZgwApiService()
                    .createResultaatForZaak(zaak, resultaattypeOmschrijving,
                                            variableValue != null ? (String) variableValue : RESULTAAT_TOELICHTING);
        }
    }
}
