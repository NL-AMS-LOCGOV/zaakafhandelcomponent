/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_ASSIGNEE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_CANDIDATE_GROUP;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_DUE_DATE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_ZAAK_UUID;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakSortering;

/**
 *
 */
@ApplicationScoped
@Transactional
public class FlowableService {

    public static final String VAR_CASE_NIET_ONTVANKELIJK_TOELICHTING = "nietOntvankelijkToelichting";

    public static final String VAR_CASE_ZAAK_UUID = "zaakUUID";

    public static final String VAR_CASE_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_CASE_ZAAKTYPE_UUUID = "zaaktypeUUID";

    public static final String VAR_CASE_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    public static final String VAR_TASK_TAAKDATA = "taakdata";

    private static final Logger LOG = Logger.getLogger(FlowableService.class.getName());

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
        return (UUID) readCaseVariableForTask(taskId, VAR_CASE_ZAAK_UUID);
    }

    public String readZaakIdentificatieForTask(final String taskId) {
        return (String) readCaseVariableForTask(taskId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String readZaaktypeOmschrijvingorTask(final String taskId) {
        return (String) readCaseVariableForTask(taskId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public UUID readZaaktypeUUIDForTask(final String taskId) {
        return (UUID) readCaseVariableForTask(taskId, VAR_CASE_ZAAKTYPE_UUUID);
    }

    public UUID readZaaktypeUUIDForCase(final String caseInstanceId) {
        return (UUID) readVariableForCase(caseInstanceId, VAR_CASE_ZAAKTYPE_UUUID);
    }

    public List<TaskInfo> listAllTasksForZaak(final UUID zaakUUID) {
        final CaseInstance caseInstance = findCaseInstanceForZaak(zaakUUID);
        if (caseInstance != null) {
            final List<TaskInfo> taken = new ArrayList<>(listOpenTasksForCase(caseInstance.getId()));
            taken.addAll(listCompletedTasksForCase(caseInstance.getId()));
            return taken;
        } else {
            return Collections.emptyList();
        }
    }

    private List<Task> listOpenTasksForCase(final String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstanceId)
                .includeIdentityLinks()
                .list();
    }

    private List<HistoricTaskInstance> listCompletedTasksForCase(final String caseInstanceId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .finished()
                .includeIdentityLinks()
                .list();
    }

    public List<Task> listOpenTasksOwnedByMedewerker(final String gebruikersnaam, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskAssignee(gebruikersnaam)
                .listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasksForGroups(final List<String> groupIds, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskCandidateGroupIn(groupIds)
                .ignoreAssigneeValue()
                .listPage(firstResult, maxResults);
    }

    public int countOpenTasksOwnedByMedewerker(final String gebruikersnaam) {
        return (int) cmmnTaskService.createTaskQuery().taskAssignee(gebruikersnaam).count();
    }

    public int countOpenTasksForGroups(final List<String> groupIds) {
        return (int) cmmnTaskService.createTaskQuery().taskCandidateGroupIn(groupIds).ignoreAssigneeValue().count();
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
                    .variable(VAR_CASE_ZAAK_IDENTIFICATIE, zaak.getIdentificatie())
                    .variable(VAR_CASE_ZAAKTYPE_UUUID, uuidFromURI(zaaktype.getUrl()))
                    .variable(VAR_CASE_ZAAKTYPE_OMSCHRIJVING, zaaktype.getOmschrijving())
                    .start();
        } catch (final FlowableObjectNotFoundException flowableObjectNotFoundException) {
            LOG.warning(String.format("Zaak %s: Case with definition key '%s' not found. No Case started.",
                                      caseDefinitionKey, zaak.getUuid()));
        }
    }

    // TODO Set the owner of a human task
    // https://github.com/nl-ams-locgov/zaakafhandelcomponent/issues/672
    public void startHumanTaskPlanItem(final PlanItemInstance planItemInstance, final String groupId, final String assignee, final Date dueDate,
            final Map<String, String> taakdata) {
        final UUID zaakUUID = readZaakUuidForCase(planItemInstance.getCaseInstanceId());
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstance.getId())
                .transientVariable(VAR_TASK_CANDIDATE_GROUP, groupId)
                .transientVariable(VAR_TASK_ASSIGNEE, assignee)
                .transientVariable(VAR_TASK_ZAAK_UUID, zaakUUID)
                .transientVariable(VAR_TASK_DUE_DATE, dueDate)
                .transientVariable(VAR_TASK_TAAKDATA, taakdata)
                .start();
    }

    public void startPlanItem(final String planItemInstanceId, final String toelichting) {
        final PlanItemInstance planItem = readPlanItem(planItemInstanceId);
        if (planItem.getPlanItemDefinitionType().equals(USER_EVENT_LISTENER)) {
            if (StringUtils.isNotEmpty(toelichting)) {
                cmmnRuntimeService.setVariable(planItem.getCaseInstanceId(), VAR_CASE_NIET_ONTVANKELIJK_TOELICHTING, toelichting);
            }
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
     * Assign task to user.
     * When the userId is null the task is released.
     *
     * @param taskId Id of the task to assign or release.
     * @param userId Id of the user to assign the task to or null which implies that the task is released.
     * @return Assigned or released task.
     */
    public Task assignTask(final String taskId, final String userId) {
        if (userId != null) {
            cmmnTaskService.unclaim(taskId);
            cmmnTaskService.claim(taskId, userId);
        } else {
            cmmnTaskService.unclaim(taskId);
        }
        return readTask(taskId);
    }

    /**
     * Assign task to a group
     *
     * @param taskId  Id of the task to assign
     * @param groupId Id of the new group
     * @return Assigned Task
     */
    public Task assignTaskToGroup(final String taskId, final String groupId) {
        final Task task = readTask(taskId);

        task.getIdentityLinks().stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .map(IdentityLinkInfo::getGroupId)
                .forEach(currentGroupId -> cmmnTaskService.deleteGroupIdentityLink(taskId, currentGroupId, IdentityLinkType.CANDIDATE));

        cmmnTaskService.addGroupIdentityLink(taskId, groupId, IdentityLinkType.CANDIDATE);

        return readTask(taskId);
    }

    public Task updateTask(final Task task) {
        cmmnTaskService.saveTask(task);
        return readTask(task.getId());
    }

    public HistoricTaskInstance completeTask(final String taskId) {
        cmmnTaskService.complete(taskId);
        return readCompletedTask(taskId);
    }

    public TaskInfo readTaskInfo(final String taskId) {
        TaskInfo taskInfo = findOpenTask(taskId);
        if (taskInfo == null) {
            taskInfo = findCompletedTask(taskId);
        }
        if (taskInfo != null) {
            return taskInfo;
        } else {
            throw new RuntimeException(String.format("No TaskInfo found with task id '%s'", taskId));
        }
    }

    public Task readTask(final String taskId) {
        final Task task = findOpenTask(taskId);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException(String.format("No Task found with task id '%s'", taskId));
        }
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
        final Map<String, String> taakdata;
        if (isOpenTask(taskId)) {
            taakdata = (Map<String, String>) cmmnTaskService.getVariableLocal(taskId, VAR_TASK_TAAKDATA);
        } else {
            final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                    .taskId(taskId)
                    .variableName(VAR_TASK_TAAKDATA)
                    .singleResult();
            taakdata = historicVariableInstance != null ? (Map<String, String>) historicVariableInstance.getValue() : null;
        }

        return taakdata != null ? taakdata : Collections.emptyMap();
    }

    public Map<String, String> updateTaakdata(final String taskId, final Map<String, String> taakdata) {
        cmmnTaskService.setVariableLocal(taskId, VAR_TASK_TAAKDATA, taakdata);
        return taakdata;
    }

    public List<CaseDefinition> listCaseDefinitions() {
        return cmmnRepositoryService.createCaseDefinitionQuery().latestVersion().list();
    }

    public CaseDefinition readCaseDefinition(final String caseDefinitionKey) {
        return cmmnRepositoryService.createCaseDefinitionQuery()
                .caseDefinitionKey(caseDefinitionKey)
                .latestVersion()
                .singleResult();
    }

    public List<HumanTask> readHumanTasks(final String caseDefinitionKey) {
        final CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinitionKey);
        return cmmnModel.getPrimaryCase().findPlanItemDefinitionsOfType(HumanTask.class);
    }

    /**
     * Terminate the case for a zaak.
     * This also terminates all open tasks related to the case,
     * This will also call {@Link EndCaseLifecycleListener}
     *
     * @param zaakUUID UUID of the zaak for which the caxse should be terminated.
     */
    public void terminateCase(final UUID zaakUUID) {
        final CaseInstance caseInstance = findCaseInstanceForZaak(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        }
    }

    public Object findVariableForCase(final String caseInstanceId, final String variabelName) {
        return cmmnRuntimeService.getVariable(caseInstanceId, variabelName);
    }

    private Object readVariableForCase(final String caseInstanceId, final String variableName) {
        final Object variableValue = findVariableForCase(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for case instance id '%s'", variableName, caseInstanceId));
        }
    }

    private Object findCaseVariableForTask(final String taskId, final String variableName) {
        final TaskInfo taskInfo = readTaskInfo(taskId);
        return cmmnRuntimeService.getVariable(taskInfo.getScopeId(), variableName);
    }

    private Object readCaseVariableForTask(final String taskId, final String variableName) {
        final Object variableValue = findCaseVariableForTask(taskId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for task id '%s'", variableName, taskId));
        }
    }

    private Task findOpenTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private boolean isOpenTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .count() > 0;
    }

    private HistoricTaskInstance readCompletedTask(final String taskId) {
        final HistoricTaskInstance historicTask = findCompletedTask(taskId);
        if (historicTask != null) {
            return historicTask;
        } else {
            throw new RuntimeException(String.format("No HistoricTaskInstance found with task id '%s'", taskId));
        }
    }

    private HistoricTaskInstance findCompletedTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
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

    private CaseInstance findCaseInstanceForZaak(final UUID zaakUUID) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .singleResult();
    }

    private TaskQuery createOpenTasksQueryWithSorting(final TaakSortering sortering, final String direction) {
        final TaskQuery taskQuery = cmmnTaskService.createTaskQuery().includeIdentityLinks();
        if (sortering != null) {
            final TaskQuery sortedTaskQuery = switch (sortering) {
                case TAAKNAAM -> taskQuery.orderByTaskName();
                case CREATIEDATUM -> taskQuery.orderByTaskCreateTime();
                case STREEFDATUM -> taskQuery.orderByTaskDueDate();
                case BEHANDELAAR -> taskQuery.orderByTaskAssignee();
            };
            return "asc".equals(direction) ? sortedTaskQuery.asc() : sortedTaskQuery.desc();
        }

        return taskQuery;
    }
}
