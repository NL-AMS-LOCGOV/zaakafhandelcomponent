/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.converter;

import static net.atos.zac.app.planitems.model.PlanItemType.HUMAN_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.PROCESS_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.USER_EVENT_LISTENER;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.UserEventListenerActie;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

/**
 *
 */
public class RESTPlanItemConverter {

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public List<RESTPlanItem> convertPlanItems(final List<PlanItemInstance> planItems, final UUID zaakUuid) {
        return planItems.stream().map(planItemInstance -> this.convertPlanItem(planItemInstance, zaakUuid)).collect(Collectors.toList());
    }

    public RESTPlanItem convertPlanItem(final PlanItemInstance planItem, final UUID zaakUuid) {
        final RESTPlanItem restPlanItem = new RESTPlanItem();
        restPlanItem.id = planItem.getId();
        restPlanItem.naam = planItem.getName();
        restPlanItem.zaakUuid = zaakUuid;
        restPlanItem.type = convertDefinitionType(planItem.getPlanItemDefinitionType());
        if (restPlanItem.type == USER_EVENT_LISTENER) {
            restPlanItem.userEventListenerActie = UserEventListenerActie.valueOf(planItem.getPlanItemDefinitionId());
            restPlanItem.toelichting = zaakafhandelParameterService.getUserEventParameters(planItem).getToelichting();
        }
        return restPlanItem;
    }

    public RESTPlanItem convertHumanTask(final PlanItemInstance planItem, final UUID zaakUuid, final HumanTaskParameters parameters) {
        final RESTPlanItem restPlanItem = convertPlanItem(planItem, zaakUuid);
        restPlanItem.groep = groepConverter.convertGroupId(parameters.getGroepID());
        if (parameters.getFormulierDefinitieID() != null) {
            restPlanItem.formulierDefinitie = FormulierDefinitie.valueOf(parameters.getFormulierDefinitieID());
        }
        return restPlanItem;
    }

    private static PlanItemType convertDefinitionType(final String planItemDefinitionType) {
        if (PlanItemDefinitionType.HUMAN_TASK.equals(planItemDefinitionType)) {
            return HUMAN_TASK;
        } else if (PlanItemDefinitionType.PROCESS_TASK.equals(planItemDefinitionType)) {
            return PROCESS_TASK;
        } else if (PlanItemDefinitionType.USER_EVENT_LISTENER.equals(planItemDefinitionType)) {
            return USER_EVENT_LISTENER;
        } else {
            throw new IllegalArgumentException(String.format("Conversie van plan item definition type '%s' wordt niet ondersteund", planItemDefinitionType));
        }
    }
}
