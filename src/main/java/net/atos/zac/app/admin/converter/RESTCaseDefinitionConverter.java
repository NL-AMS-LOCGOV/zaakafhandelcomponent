/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import static net.atos.zac.app.planitems.model.PlanItemType.HUMAN_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.USER_EVENT_LISTENER;

import javax.inject.Inject;

import org.flowable.cmmn.api.repository.CaseDefinition;

import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.flowable.FlowableService;

public class RESTCaseDefinitionConverter {

    @Inject
    private FlowableService flowableService;

    public RESTCaseDefinition convertToRESTCaseDefinition(final String caseDefinitionKey) {
        final CaseDefinition caseDefinition = flowableService.readCaseDefinition(caseDefinitionKey);
        final RESTCaseDefinition restCaseDefinition = new RESTCaseDefinition(caseDefinition.getName(), caseDefinitionKey);
        restCaseDefinition.humanTaskDefinitions = flowableService.listHumanTasks(caseDefinition.getId()).stream()
                .map(humanTaskDefinition -> new RESTPlanItemDefinition(humanTaskDefinition.getId(), humanTaskDefinition.getName(), HUMAN_TASK))
                .toList();
        restCaseDefinition.userEventListenerDefinitions = flowableService.listUserEventListeners(caseDefinition.getId()).stream()
                .map(userEventListenerDefinition -> new RESTPlanItemDefinition(userEventListenerDefinition.getId(), userEventListenerDefinition.getName(),
                                                                               USER_EVENT_LISTENER))
                .toList();
        return restCaseDefinition;
    }
}
