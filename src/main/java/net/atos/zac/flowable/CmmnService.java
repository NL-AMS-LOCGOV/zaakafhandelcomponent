/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.cmmn.ExtraheerGroepLifecycleListener.VAR_LOCAL_CANDIDATE_GROUP_ID;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.idm.api.Group;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakSortering;

/**
 *
 */
@ApplicationScoped
@Transactional
public class CmmnService {

    public static final String VAR_CASE_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_CASE_ZAAK_UUID = "zaakUUID";

    public static final String VAR_CASE_ZAAK_URI = "zaakURI";

    public static final String VAR_CASE_ZAAKTYPE_URI = "zaaktypeURI";

    public static final String VAR_CASE_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    private static final Logger LOG = Logger.getLogger(CmmnService.class.getName());

    private static final String ID_GROEP_ZAAK_OPEN = "*";

    private static final String ID_GROEP_ZAAK_CLOSE = "*";

    @Inject
    private CmmnRuntimeService cmmnRuntimeService;

    @Inject
    private CmmnTaskService cmmnTaskService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    @Inject
    private CmmnRepositoryService cmmnRepositoryService;

    @Inject
    private IdmService idmService;

    public UUID findZaakUuidForCase(final String caseInstanceId) {
        return (UUID) cmmnRuntimeService.getVariable(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public UUID findZaakUuidForTask(final String taskId) {
        return (UUID) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAK_UUID);
    }

    public String findZaakIdentificatieForTask(final String taskId) {
        return (String) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String findZaaktypeOmschrijvingForTask(final String taskId) {
        return (String) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public List<TaskInfo> listTaskInfosForZaak(final UUID zaakUUID) {
        final String caseInstanceId = findCaseInstanceIdForZaak(zaakUUID);
        if (caseInstanceId != null) {
            final List<TaskInfo> taken = new ArrayList<>(listTasksForCase(caseInstanceId));
            taken.addAll(listHistoricTasksForCase(caseInstanceId));
            return taken;
        } else {
            return Collections.emptyList();
        }
    }

    private List<Task> listTasksForCase(final String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstanceId)
                .includeIdentityLinks()
                .list();
    }

    private List<HistoricTaskInstance> listHistoricTasksForCase(final String caseInstanceId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .finished()
                .includeIdentityLinks()
                .list();
    }

    public List<Task> listTasksOwnedByMedewerker(final String gebruikersnaam, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return addTaskSortingToTaskQuery(createTaskQuery(), sortering, direction)
                .taskAssignee(gebruikersnaam)
                .listPage(firstResult, maxResults);
    }

    public int countTasksOwnedByMedewerker(final String gebruikersnaam) {
        return (int) createTaskQuery().taskAssignee(gebruikersnaam).count();
    }

    public List<Task> listTasksForGroups(final List<String> groupIds, final TaakSortering sortering, final String direction,
            final int firstResult,
            final int maxResults) {
        return addTaskSortingToTaskQuery(createTaskQuery(), sortering, direction)
                .taskCandidateGroupIn(groupIds)
                .listPage(firstResult, maxResults);
    }

    public int countTasksForGroups(final List<String> groupIds) {
        return (int) createTaskQuery()
                .taskCandidateGroupIn(groupIds)
                .count();
    }

    public List<PlanItemInstance> listPlanItemsForZaak(final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = listAvailablePlanItemsForZaak(zaakUUID);
        planItems.addAll(listAvailableUserEventListenersForZaak(zaakUUID));
        return planItems;
    }

    public void startCase(final String caseDefinitionKey, final Zaak zaak, final Zaaktype zaaktype) {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder()
                    .caseDefinitionKey(caseDefinitionKey)
                    .businessKey(zaak.getUuid().toString())
                    .variable(VAR_CASE_ZAAK_UUID, zaak.getUuid())
                    .variable(VAR_CASE_ZAAK_URI, zaak.getUrl())
                    .variable(VAR_CASE_ZAAK_IDENTIFICATIE, zaak.getIdentificatie())
                    .variable(VAR_CASE_ZAAKTYPE_URI, zaaktype.getUrl())
                    .variable(VAR_CASE_ZAAKTYPE_OMSCHRIJVING, zaaktype.getOmschrijving())
                    .start();
        } catch (final FlowableObjectNotFoundException flowableObjectNotFoundException) {
            LOG.warning(String.format("Zaak %s: Case with definition key '%s' not found. No Case started.", caseDefinitionKey, zaak.getUuid()));
        }
    }

    public void startHumanTaskPlanItem(final String planItemInstanceId, final String groupId) {
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstanceId)
                .transientVariable(VAR_LOCAL_CANDIDATE_GROUP_ID, groupId)
                .start();
    }

    public void startPlanItem(final String planItemInstanceId) {
        final PlanItemInstance planItem = findPlanItem(planItemInstanceId);
        if (planItem.getPlanItemDefinitionType().equals(USER_EVENT_LISTENER)) {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
        } else {
            cmmnRuntimeService.startPlanItemInstance(planItemInstanceId);
        }
    }

    public PlanItemInstance findPlanItem(final String planItemInstanceId) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemInstanceId(planItemInstanceId)
                .singleResult();
    }

    /**
     * Toekennen van een taak.
     * Dit kan zijn het wijzigen van:
     * - de behandelaar.
     * Met de mogelijkheden:
     * - toekennen aan jezelf (de ingelogde medewerker).
     * - toekennen aan een andere medewerker.
     * - Wanneer de behandelaar null is dan wordt de taak vrijgegeven.
     * - De groep
     * Met als gevolg:
     * - Wanneer de huidige behandelaar geen onderdeel is van de groep dan wordt de taak ook meteen vrijgegeven.
     */
    public Task assignTask(final String taskId, final String userId, final String groupId) {
        if (userId != null) {
            final Task task = findTask(taskId);
            if (!userId.equals(task.getAssignee())) {
                cmmnTaskService.unclaim(taskId);
                cmmnTaskService.claim(taskId, userId);
            }
        } else {
            cmmnTaskService.unclaim(taskId);
        }
        return findTask(taskId);
    }

    public Task updateTask(final Task task) {
        cmmnTaskService.saveTask(task);
        return findTask(task.getId());
    }

    public TaskInfo completeTask(final String taskId) {
        cmmnTaskService.complete(taskId);
        return findHistoricTask(taskId);
    }

    public TaskInfo findTaskInfo(final String taskId) {
        final Task task = findTask(taskId);
        return task != null ? task : findHistoricTask(taskId);
    }

    public Task findTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private HistoricTaskInstance findHistoricTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .finished()
                .includeIdentityLinks()
                .singleResult();
    }

    public Group findGroupForCaseDefinition(final String caseDefinitionKey) {
        final CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery()
                .caseDefinitionKey(caseDefinitionKey)
                .latestVersion()
                .singleResult();
        final String groupId = substringBetween(caseDefinition.getDescription(), ID_GROEP_ZAAK_OPEN, ID_GROEP_ZAAK_CLOSE);
        return idmService.findGroup(groupId);
    }

    public Group findGroupForPlanItem(final String planItemInstanceId) {
        final String groupId = (String) cmmnRuntimeService.getLocalVariable(planItemInstanceId, VAR_LOCAL_CANDIDATE_GROUP_ID);
        return groupId != null ? idmService.findGroup(groupId) : null;
    }

    private List<PlanItemInstance> listAvailablePlanItemsForZaak(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateEnabled()
                .list();
    }

    private List<PlanItemInstance> listAvailableUserEventListenersForZaak(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateAvailable()
                .planItemDefinitionType(USER_EVENT_LISTENER)
                .list();
    }

    private String findCaseInstanceIdForZaak(final UUID zaakUUID) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .singleResult();
        return caseInstance != null ? caseInstance.getId() : null;
    }

    private TaskQuery createTaskQuery() {
        //hier kan nog filtering komen/mapping van tablerequest#getSearch
        return cmmnTaskService.createTaskQuery().includeIdentityLinks();
    }

    private static TaskQuery addTaskSortingToTaskQuery(final TaskQuery taskQuery, final TaakSortering sortering, final String direction) {
        if (sortering != null) {
            final TaskQuery sortedTaskQuery;
            switch (sortering) {
                case TAAKNAAM:
                    sortedTaskQuery = taskQuery.orderByTaskName();
                    break;
                case CREATIEDATUM:
                    sortedTaskQuery = taskQuery.orderByTaskCreateTime();
                    break;
                case STREEFDATUM:
                    sortedTaskQuery = taskQuery.orderByTaskDueDate();
                    break;
                case BEHANDELAAR:
                    sortedTaskQuery = taskQuery.orderByTaskAssignee();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Onbekende predicate met de naam: %s", sortering));
            }

            return "asc".equals(direction) ? sortedTaskQuery.asc() : sortedTaskQuery.desc();
        }

        return taskQuery;
    }
}
