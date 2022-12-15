/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static java.lang.String.format;

import java.util.UUID;
import java.util.logging.Logger;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.common.engine.api.delegate.Expression;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.flowable.FlowableHelper;

/**
 *
 */
public class UpdateZaakLifecycleListener implements PlanItemInstanceLifecycleListener {

    private static final Logger LOG = Logger.getLogger(UpdateZaakLifecycleListener.class.getName());

    private static final String STATUS_TOELICHTING = "Status gewijzigd vanuit Case";

    private Expression statusExpression;

    public void setStatus(final Expression status) {
        statusExpression = status;
    }

    @Override
    public String getSourceState() {
        return null;
    }

    @Override
    public String getTargetState() {
        return null;
    }

    @Override
    public void stateChanged(final DelegatePlanItemInstance planItemInstance, final String oldState,
            final String newState) {
        if (statusExpression != null) {
            updateZaak(planItemInstance, statusExpression.getValue(planItemInstance).toString());
        }
    }

    private void updateZaak(final PlanItemInstance planItemInstance, final String statustypeOmschrijving) {
        final UUID zaakUUID = FlowableHelper.getInstance().getZaakVariabelenService().readZaakUUID(planItemInstance);
        final Zaak zaak = FlowableHelper.getInstance().getZrcClientService().readZaak(zaakUUID);
        LOG.info(format("Zaak %s: Change Status to '%s'", zaakUUID, statustypeOmschrijving));
        FlowableHelper.getInstance().getZgwApiService()
                .createStatusForZaak(zaak, statustypeOmschrijving, STATUS_TOELICHTING);
    }
}
