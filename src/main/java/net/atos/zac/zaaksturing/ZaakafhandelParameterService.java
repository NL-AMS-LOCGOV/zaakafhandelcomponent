/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.PlanItemParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
public class ZaakafhandelParameterService {

    @Inject
    private FlowableService flowableService;

    @Inject
    private ZaakafhandelParameterBeheerService beheerService;

    public ZaakafhandelParameters getZaakafhandelParameters(final Zaak zaak) {
        final UUID zaakTypeUUID = UriUtil.uuidFromURI(zaak.getZaaktype());
        return beheerService.readZaakafhandelParameters(zaakTypeUUID);
    }

    public PlanItemParameters getPlanItemParameters(final PlanItemInstance planItem) {
        final UUID zaaktypeUUID = flowableService.readZaaktypeUUIDForCase(planItem.getCaseInstanceId());
        if (planItem.getPlanItemDefinitionType().equals("humantask")) {
            return beheerService.readPlanItemParameters(zaaktypeUUID, planItem.getPlanItemDefinitionId());
        }
        final PlanItemParameters parameters = new PlanItemParameters();
        parameters.setPlanItemDefinitionID(planItem.getPlanItemDefinitionId());
        ZaakafhandelParameters zaakafhandelParameters = beheerService.readZaakafhandelParameters(zaaktypeUUID);
        parameters.setZaakafhandelParameters(zaakafhandelParameters);
        return parameters;
    }

    public FormulierDefinitie findFormulierDefinitie(final TaskInfo taskInfo) {
        final UUID zaakTypeUUID = flowableService.readZaaktypeUUIDForTask(taskInfo.getId());
        PlanItemParameters planItemParameters = beheerService.readPlanItemParameters(zaakTypeUUID, taskInfo.getTaskDefinitionKey());
        if (planItemParameters == null) {
            throw new NotFoundException(String.format("PlanItemParameters not configured! zaakTypeUUID: %s, planItemDefinitionKey: %s",
                                                      zaakTypeUUID, taskInfo.getTaskDefinitionKey()));
        }
        return FormulierDefinitie.valueOf(planItemParameters.getFormulierDefinitieID());
    }

}
