/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAKTYPE_UUUID;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import net.atos.zac.zaaksturing.model.UserEventListenerParameters;

import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
public class ZaakafhandelParameterService {

    private static final String PLAN_ITEM_DEFINITION_TYPE_HUMAN_TASK = "humantask";

    @Inject
    private FlowableService flowableService;

    @Inject
    private ZaakafhandelParameterBeheerService beheerService;

    public ZaakafhandelParameters getZaakafhandelParameters(final Zaak zaak) {
        final UUID zaakTypeUUID = UriUtil.uuidFromURI(zaak.getZaaktype());
        return beheerService.readZaakafhandelParameters(zaakTypeUUID);
    }

    public HumanTaskParameters getHumanTaskParameters(final PlanItemInstance planItem) {
        final UUID zaaktypeUUID = (UUID) flowableService.readOpenCaseVariable(planItem.getCaseInstanceId(), VAR_CASE_ZAAKTYPE_UUUID);
        if (planItem.getPlanItemDefinitionType().equals(PLAN_ITEM_DEFINITION_TYPE_HUMAN_TASK)) {
            return beheerService.readHumanTaskParameters(zaaktypeUUID, planItem.getPlanItemDefinitionId());
        }
        final HumanTaskParameters parameters = new HumanTaskParameters();
        parameters.setPlanItemDefinitionID(planItem.getPlanItemDefinitionId());
        ZaakafhandelParameters zaakafhandelParameters = beheerService.readZaakafhandelParameters(zaaktypeUUID);
        parameters.setZaakafhandelParameters(zaakafhandelParameters);
        return parameters;
    }

    public UserEventListenerParameters getUserEventParameters(final PlanItemInstance planItem) {
        final UUID zaaktypeUUID = (UUID) flowableService.readOpenCaseVariable(planItem.getCaseInstanceId(),
                                                                              VAR_CASE_ZAAKTYPE_UUUID);

        return beheerService.readUserEventListenerParameters(zaaktypeUUID, planItem.getPlanItemDefinitionId());
    }

    public FormulierDefinitie findFormulierDefinitie(final UUID zaaktypeUUID, final String taskDefinitionKey) {
        final HumanTaskParameters humanTaskParameters = beheerService.readHumanTaskParameters(zaaktypeUUID, taskDefinitionKey);
        if (humanTaskParameters == null) {
            throw new NotFoundException(
                    String.format("HumanTaskParameters not configured! zaakTypeUUID: %s, planItemDefinitionKey: %s",
                                  zaaktypeUUID, zaaktypeUUID));
        }
        return FormulierDefinitie.valueOf(humanTaskParameters.getFormulierDefinitieID());
    }
}
