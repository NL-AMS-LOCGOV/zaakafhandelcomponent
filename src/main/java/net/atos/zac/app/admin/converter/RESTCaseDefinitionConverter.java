/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.atos.zac.app.planitems.model.PlanItemType;

import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.model.HumanTask;

import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.flowable.FlowableService;

import org.flowable.cmmn.model.UserEventListener;

public class RESTCaseDefinitionConverter {

    @Inject
    private FlowableService flowableService;

    public RESTCaseDefinition convertToRest(final String key) {
        if (key == null) {
            return null;
        }
        final CaseDefinition caseDefinition = flowableService.readCaseDefinition(key);
        final List<HumanTask> humanTasks = flowableService.listHumanTasks(caseDefinition.getId());
        final List<UserEventListener> userEventListeners =
                flowableService.listUserEventListeners(caseDefinition.getId());
        final RESTCaseDefinition restCaseDefinition = new RESTCaseDefinition(caseDefinition.getName(), key);
        final List<RESTPlanItemDefinition> planItemDefinitions = new ArrayList<>();
        final List<RESTPlanItemDefinition> userEventListenerDefinitions = new ArrayList<>();
        humanTasks.forEach(humanTask -> {
            final RESTPlanItemDefinition planItemDefinition = new RESTPlanItemDefinition(humanTask.getName(),
                                                                                         humanTask.getId(),
                                                                                         PlanItemType.HUMAN_TASK);
            planItemDefinitions.add(planItemDefinition);
        });
        userEventListeners.forEach(userEventListener -> {
            final RESTPlanItemDefinition planItemDefinition = new RESTPlanItemDefinition(userEventListener.getName(),
                                                                                         userEventListener.getId(),
                                                                                         PlanItemType.USER_EVENT_LISTENER);
            userEventListenerDefinitions.add(planItemDefinition);
        });
        restCaseDefinition.planItemDefinitions = planItemDefinitions;
        restCaseDefinition.userEventListenerDefinitions = userEventListenerDefinitions;
        return restCaseDefinition;
    }
}
