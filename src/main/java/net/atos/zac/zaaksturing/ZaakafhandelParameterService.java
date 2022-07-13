/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
public class ZaakafhandelParameterService {

    private static final String PLAN_ITEM_DEFINITION_TYPE_HUMAN_TASK = "humantask";

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private ZaakafhandelParameterBeheerService beheerService;

    public ZaakafhandelParameters readZaakafhandelParameters(final Zaak zaak) {
        final UUID zaakTypeUUID = UriUtil.uuidFromURI(zaak.getZaaktype());
        return beheerService.readZaakafhandelParameters(zaakTypeUUID);
    }

    public HumanTaskParameters findHumanTaskParameters(final PlanItemInstance planItem) {
        final UUID zaaktypeUUID = caseVariablesService.readZaaktypeUUID(planItem.getCaseInstanceId());
        if (planItem.getPlanItemDefinitionType().equals(PLAN_ITEM_DEFINITION_TYPE_HUMAN_TASK)) {
            return beheerService.findHumanTaskParameters(zaaktypeUUID, planItem.getPlanItemDefinitionId());
        } else {
            final HumanTaskParameters parameters = new HumanTaskParameters();
            parameters.setPlanItemDefinitionID(planItem.getPlanItemDefinitionId());
            ZaakafhandelParameters zaakafhandelParameters = beheerService.readZaakafhandelParameters(zaaktypeUUID);
            parameters.setZaakafhandelParameters(zaakafhandelParameters);
            return parameters;
        }
    }

    public UserEventListenerParameters readUserEventParameters(final PlanItemInstance planItem) {
        final UUID zaaktypeUUID = caseVariablesService.readZaaktypeUUID(planItem.getCaseInstanceId());
        return beheerService.readUserEventListenerParameters(zaaktypeUUID, planItem.getPlanItemDefinitionId());
    }
}
