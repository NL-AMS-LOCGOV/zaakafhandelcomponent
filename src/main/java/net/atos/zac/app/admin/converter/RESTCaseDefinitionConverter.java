/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import static net.atos.zac.app.planitems.model.PlanItemType.HUMAN_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.USER_EVENT_LISTENER;

import javax.inject.Inject;

import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.cmmn.model.UserEventListener;

import net.atos.zac.app.admin.model.RESTCaseDefinition;
import net.atos.zac.app.admin.model.RESTPlanItemDefinition;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.flowable.TakenService;

public class RESTCaseDefinitionConverter {

    @Inject
    private CMMNService cmmnService;

    @Inject
    private TakenService takenService;


    public RESTCaseDefinition convertToRESTCaseDefinition(final String caseDefinitionKey, final boolean inclusiefRelaties) {
        final CaseDefinition caseDefinition = cmmnService.readCaseDefinition(caseDefinitionKey);
        return convertToRESTCaseDefinition(caseDefinition, inclusiefRelaties);
    }

    public RESTCaseDefinition convertToRESTCaseDefinition(final CaseDefinition caseDefinition, final boolean inclusiefRelaties) {
        final RESTCaseDefinition restCaseDefinition = new RESTCaseDefinition(caseDefinition.getName(), caseDefinition.getKey());
        if (inclusiefRelaties) {
            restCaseDefinition.humanTaskDefinitions = cmmnService.listHumanTasks(caseDefinition.getId()).stream()
                    .map(this::convertHumanTaskDefinition)
                    .toList();
            restCaseDefinition.userEventListenerDefinitions = cmmnService.listUserEventListeners(caseDefinition.getId())
                    .stream()
                    .map(this::convertUserEventListenerDefinition)
                    .toList();
        }
        return restCaseDefinition;
    }

    private RESTPlanItemDefinition convertHumanTaskDefinition(final HumanTask humanTaskDefinition) {
        return new RESTPlanItemDefinition(humanTaskDefinition.getId(), humanTaskDefinition.getName(), HUMAN_TASK);
    }

    private RESTPlanItemDefinition convertUserEventListenerDefinition(final UserEventListener userEventListenerDefinition) {
        return new RESTPlanItemDefinition(userEventListenerDefinition.getId(), userEventListenerDefinition.getName(), USER_EVENT_LISTENER);
    }
}
