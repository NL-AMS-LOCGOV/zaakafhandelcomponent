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
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
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
public class FlowableService {

    public static final String VAR_CASE_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_CASE_ZAAK_UUID = "zaakUUID";

    public static final String VAR_CASE_ZAAK_URI = "zaakURI";

    public static final String VAR_CASE_ZAAKTYPE_URI = "zaaktypeURI";

    public static final String VAR_CASE_ZAAKTYPE_IDENTIFICATIE = "zaaktypeIdentificatie";

    public static final String VAR_CASE_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    private static final String VAR_TASK_TAAKDATA = "taakdata";

    private static final Logger LOG = Logger.getLogger(FlowableService.class.getName());

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
    private IdmIdentityService idmIdentityService;

    public UUID readZaakUuidForCase(final String caseInstanceId) {
        return (UUID) readVariableForCase(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public UUID readZaakUuidForTask(final String taskId) {
        return (UUID) readVariableForTask(taskId, VAR_CASE_ZAAK_UUID);
    }

    public String readZaakIdentificatieForTask(final String taskId) {
        return (String) readVariableForTask(taskId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String readZaaktypeOmschrijvingorTask(final String taskId) {
        return (String) readVariableForTask(taskId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public String readZaaktypeIdentificatieForTask(final String taskId) {
        return (String) readVariableForTask(taskId, VAR_CASE_ZAAKTYPE_IDENTIFICATIE);
    }

    public String readZaaktypeIdentificatieForCase(final String caseInstanceId) {
        return (String) readVariableForCase(caseInstanceId, VAR_CASE_ZAAKTYPE_IDENTIFICATIE);
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
                    .variable(VAR_CASE_ZAAKTYPE_IDENTIFICATIE, zaaktype.getIdentificatie())
                    .variable(VAR_CASE_ZAAKTYPE_OMSCHRIJVING, zaaktype.getOmschrijving())
                    .start();
        } catch (final FlowableObjectNotFoundException flowableObjectNotFoundException) {
            LOG.warning(String.format("Zaak %s: Case with definition key '%s' not found. No Case started.", caseDefinitionKey, zaak.getUuid()));
        }
    }

    public void startHumanTaskPlanItem(final String planItemInstanceId, final String groupId, final Map<String, String> taakdata) {
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstanceId)
                .transientVariable(VAR_LOCAL_CANDIDATE_GROUP_ID, groupId)
                .localVariable(VAR_TASK_TAAKDATA, taakdata)
                .start();
    }

    public void startPlanItem(final String planItemInstanceId) {
        final PlanItemInstance planItem = readPlanItem(planItemInstanceId);
        if (planItem.getPlanItemDefinitionType().equals(USER_EVENT_LISTENER)) {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
        } else {
            cmmnRuntimeService.startPlanItemInstance(planItemInstanceId);
        }
    }

    public PlanItemInstance readPlanItem(final String planItemInstanceId) {
        final PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemInstanceId(planItemInstanceId)
                .singleResult();
        if (planItemInstance != null) {
            return planItemInstance;
        } else {
            throw new RuntimeException(String.format("No plan item found with plan item instance id '%s'", planItemInstanceId));
        }
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
            final Task task = readTask(taskId);
            if (!userId.equals(task.getAssignee())) {
                cmmnTaskService.unclaim(taskId);
                cmmnTaskService.claim(taskId, userId);
            }
        } else {
            cmmnTaskService.unclaim(taskId);
        }
        return readTask(taskId);
    }

    public Task updateTask(final Task task) {
        cmmnTaskService.saveTask(task);
        return readTask(task.getId());
    }

    public TaskInfo completeTask(final String taskId) {
        cmmnTaskService.complete(taskId);
        return findHistoricTask(taskId);
    }

    public TaskInfo readTaskInfo(final String taskId) {
        TaskInfo taskInfo = findTask(taskId);
        if (taskInfo == null) {
            taskInfo = findHistoricTask(taskId);
        }
        if (taskInfo != null) {
            return taskInfo;
        } else {
            throw new RuntimeException(String.format("No TaskInfo found with task id '%s'", taskId));
        }
    }

    public Task readTask(final String taskId) {
        final Task task = cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException(String.format("No Task found with task id '%s'", taskId));
        }
    }

    public Group findGroupForCaseDefinition(final String caseDefinitionKey) {
        final CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery()
                .caseDefinitionKey(caseDefinitionKey)
                .latestVersion()
                .singleResult();
        final String groupId = substringBetween(caseDefinition.getDescription(), ID_GROEP_ZAAK_OPEN, ID_GROEP_ZAAK_CLOSE);
        return StringUtils.isNotEmpty(groupId) ? readGroup(groupId) : null;
    }

    public Group findGroupForPlanItem(final String planItemInstanceId) {
        final String groupId = (String) cmmnRuntimeService.getLocalVariable(planItemInstanceId, VAR_LOCAL_CANDIDATE_GROUP_ID);
        return groupId != null ? readGroup(groupId) : null;
    }

    public List<Group> listGroups() {
        return idmIdentityService.createGroupQuery()
                .orderByGroupName()
                .asc()
                .list();
    }

    public Group readGroup(final String groupId) {
        final Group group = idmIdentityService.createGroupQuery()
                .groupId(groupId)
                .singleResult();
        if (group != null) {
            return group;
        } else {
            throw new RuntimeException(String.format("No Group found with group id '%s'", groupId));
        }
    }

    public User readUser(final String userId) {
        final User user = idmIdentityService.createUserQuery()
                .userId(userId)
                .singleResult();
        if (user != null) {
            return user;
        } else {
            throw new RuntimeException(String.format("No User found with user id '%s'", userId));
        }
    }

    public List<Group> listGroupsForUser(final String userId) {
        return idmIdentityService.createGroupQuery()
                .groupMember(userId)
                .list();
    }

    public List<User> listUsersInGroup(final String groepId) {
        return idmIdentityService.createUserQuery()
                .memberOfGroup(groepId)
                .list();
    }

    public List<User> listUsers() {
        return idmIdentityService.createUserQuery().list();
    }

    public Map<String, String> readTaakdata(final String taskId) {
        final Map<String, String> taakdata = (Map<String, String>) cmmnTaskService.getVariable(taskId, VAR_TASK_TAAKDATA);
        if (taakdata != null) {
            return taakdata;
        } else {
            return Collections.emptyMap();
        }
    }

    private Object readVariableForCase(final String caseInstanceId, final String variableName) {
        final Object variableValue = cmmnRuntimeService.getVariable(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("Variable '%s' not found for case instance id '%s'", variableName, caseInstanceId));
        }
    }

    private Object readVariableForTask(final String taskId, final String variableName) {
        final Object variableValue = cmmnTaskService.getVariable(taskId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("Variable '%s' not found for task id '%s'", variableName, taskId));
        }
    }

    private TaskInfo findTask(final String taskId) {
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
                    throw new IllegalArgumentException(String.format("Unknown predicate with name: %s", sortering));
            }

            return "asc".equals(direction) ? sortedTaskQuery.asc() : sortedTaskQuery.desc();
        }

        return taskQuery;
    }
}
