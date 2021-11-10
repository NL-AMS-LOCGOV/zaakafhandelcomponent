/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.converter;

import static net.atos.zac.app.planitems.model.PlanItemType.HUMAN_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.PROCESS_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.USER_EVENT_LISTENER;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.idm.api.Group;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.app.planitems.model.RESTPlanItem;

/**
 *
 */
public class RESTPlanItemConverter {

    @Inject
    private RESTGroepConverter groepConverter;

    public List<RESTPlanItem> convertPlanItems(final List<PlanItemInstance> planItems) {
        return planItems.stream().map(this::convertPlanItem).collect(Collectors.toList());
    }

    public RESTPlanItem convertPlanItem(final PlanItemInstance planItem) {
        final RESTPlanItem restPlanItem = new RESTPlanItem();
        restPlanItem.id = planItem.getId();
        restPlanItem.naam = planItem.getName();
        restPlanItem.type = convertDefinitionType(planItem.getPlanItemDefinitionType());
        return restPlanItem;
    }

    public RESTPlanItem convertPlanItem(final PlanItemInstance planItem, final Group group, final String startFormulier) {
        final RESTPlanItem restPlanItem = convertPlanItem(planItem);
        restPlanItem.groep = groepConverter.convertGroup(group);
        restPlanItem.taakStartFormulier = startFormulier;
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
