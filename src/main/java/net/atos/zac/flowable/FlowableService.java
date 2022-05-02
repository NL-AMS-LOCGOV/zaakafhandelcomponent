/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_ASSIGNEE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_CANDIDATE_GROUP;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_DUE_DATE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_OWNER;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TASK_ZAAK_UUID;
import static net.atos.zac.util.JsonbUtil.JSONB;
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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.cmmn.model.UserEventListener;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.HistoryService;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.flowable.variable.api.history.HistoricVariableInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.mail.MailService;

/**
 *
 */
@ApplicationScoped
@Transactional
public class FlowableService {

    public static final String VAR_CASE_RESULTAAT_TOELICHTING = "resultaatToelichting";

    public static final String VAR_CASE_ZAAK_UUID = "zaakUUID";

    public static final String VAR_CASE_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_CASE_ZAAKTYPE_UUUID = "zaaktypeUUID";

    public static final String VAR_CASE_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    public static final String VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD = "ontvangstbevestigingVerstuurd";

    public static final String VAR_TASK_TAAKDATA = "taakdata";

    public static final String USER_TASK_DESCRIPTION_CHANGED = "USER_TASK_DESCRIPTION_CHANGED";

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

    @Inject
    private MailService mailService;

    @Inject
    private HistoryService historyService;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    public static class TaskDescriptionChangedData {

        public String newDescription;

        public String previousDescription;

        public TaskDescriptionChangedData() {}

        public TaskDescriptionChangedData(final String previousDescription, final String newDescription) {
            this.newDescription = newDescription;
            this.previousDescription = previousDescription;
        }
    }

    public List<TaskInfo> listTasksForOpenCase(final UUID zaakUUID) {
        final List<TaskInfo> tasks = new ArrayList<>();
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            tasks.addAll(listOpenTasksForCase(caseInstance.getId()));
            tasks.addAll(listClosedTasksForCase(caseInstance.getId()));
        }
        return tasks;
    }

    public List<HistoricTaskInstance> listTasksForClosedCase(final UUID zaakUUID) {
        final HistoricCaseInstance historicCaseInstance = findClosedCaseForZaak(zaakUUID);
        if (historicCaseInstance != null) {
            return listClosedTasksForCase(historicCaseInstance.getId());
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

    private List<HistoricTaskInstance> listClosedTasksForCase(final String caseInstanceId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .finished()
                .includeIdentityLinks()
                .list();
    }

    public List<Task> listOpenTasksAssignedToUser(final String userid, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskAssignee(userid)
                .listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasksAssignedToGroups(final List<String> groupIds, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskCandidateGroupIn(groupIds)
                .ignoreAssigneeValue()
                .listPage(firstResult, maxResults);
    }

    public int countOpenTasksAssignedToUser(final String userId) {
        return (int) cmmnTaskService.createTaskQuery().taskAssignee(userId).count();
    }

    public int countOpenTasksAssignedToGroups(final List<String> groupIds) {
        return (int) cmmnTaskService.createTaskQuery().taskCandidateGroupIn(groupIds).ignoreAssigneeValue().count();
    }

    public List<Task> listOpenTasksforCase(final UUID zaakUUID) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            return listOpenTasksForCase(caseInstance.getId());
        }
        return null;
    }

    public List<PlanItemInstance> listPlanItemsForOpenCase(final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = listEnabledPlanItemsForOpenCase(zaakUUID);
        planItems.addAll(listAvailableUserEventListenersForOpenCase(zaakUUID));
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
            LOG.warning(String.format("Zaak %s: Case with definition key '%s' not found. No Case started.", caseDefinitionKey, zaak.getUuid()));
        }
    }

    public void startHumanTaskPlanItem(final PlanItemInstance planItemInstance, final String groupId, final String assignee, final Date dueDate,
            final Map<String, String> taakdata, final boolean sendMail, final String onderwerp) {
        final UUID zaakUUID = (UUID) readOpenCaseVariable(planItemInstance.getCaseInstanceId(), VAR_CASE_ZAAK_UUID);

        if (sendMail) {
            mailService.sendMail(taakdata.get("emailadres"), onderwerp,
                                 taakdata.get("body"), true, zaakUUID);
        }

        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstance.getId())
                .transientVariable(VAR_TASK_OWNER, ingelogdeMedewerker.get().getGebruikersnaam())
                .transientVariable(VAR_TASK_CANDIDATE_GROUP, groupId)
                .transientVariable(VAR_TASK_ASSIGNEE, assignee)
                .transientVariable(VAR_TASK_ZAAK_UUID, zaakUUID)
                .transientVariable(VAR_TASK_DUE_DATE, dueDate)
                .transientVariable(VAR_TASK_TAAKDATA, taakdata)
                .start();
    }

    public void startUserEventListenerPlanItem(final String planItemInstanceId, final String resultaatToelichting) {
        final PlanItemInstance planItem = readOpenPlanItem(planItemInstanceId);
        if (StringUtils.isNotEmpty(resultaatToelichting)) {
            cmmnRuntimeService.setVariable(planItem.getCaseInstanceId(), VAR_CASE_RESULTAAT_TOELICHTING, resultaatToelichting);
        }
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
    }

    public PlanItemInstance readOpenPlanItem(final String planItemInstanceId) {
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
    public Task assignTaskToUser(final String taskId, final String userId) {
        if (userId != null) {
            cmmnTaskService.setAssignee(taskId, userId);
        } else {
            cmmnTaskService.unclaim(taskId);
        }
        return readOpenTask(taskId);
    }

    /**
     * Assign task to a group
     *
     * @param taskId  Id of the task to assign
     * @param groupId Id of the new group
     * @return Assigned Task
     */
    public Task assignTaskToGroup(final String taskId, final String groupId) {
        final Task task = readOpenTask(taskId);

        task.getIdentityLinks().stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .map(IdentityLinkInfo::getGroupId)
                .forEach(currentGroupId -> cmmnTaskService.deleteGroupIdentityLink(taskId, currentGroupId, IdentityLinkType.CANDIDATE));

        cmmnTaskService.addGroupIdentityLink(taskId, groupId, IdentityLinkType.CANDIDATE);

        return readOpenTask(taskId);
    }

    public Task updateTask(final Task task) {
        final Task originalTask = readOpenTask(task.getId());
        cmmnTaskService.saveTask(task);
        if (!StringUtils.equals(originalTask.getDescription(), task.getDescription())) {
            final TaskDescriptionChangedData descriptionChangedData = new TaskDescriptionChangedData(originalTask.getDescription(), task.getDescription());
            cmmnHistoryService.createHistoricTaskLogEntryBuilder(originalTask)
                    .type(USER_TASK_DESCRIPTION_CHANGED)
                    .data(JSONB.toJson(descriptionChangedData))
                    .create();
        }
        return readOpenTask(task.getId());
    }

    public HistoricTaskInstance completeTask(final String taskId) {
        cmmnTaskService.complete(taskId);
        return readClosedTask(taskId);
    }

    public Task readOpenTask(final String taskId) {
        final Task task = findOpenTask(taskId);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException(String.format("No open task found with task id '%s'", taskId));
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
            throw new RuntimeException(String.format("No group found with group id '%s'", groupId));
        }
    }

    public User readUser(final String userId) {
        final User user = idmIdentityService.createUserQuery()
                .userId(userId)
                .singleResult();
        if (user != null) {
            return user;
        } else {
            throw new RuntimeException(String.format("No user found with user id '%s'", userId));
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

    public Map<String, String> readTaakdataForOpenTask(final String taskId) {
        final Map<String, String> taakdata = (Map<String, String>) cmmnTaskService.getVariableLocal(taskId, VAR_TASK_TAAKDATA);
        return taakdata != null ? taakdata : Collections.emptyMap();
    }

    public Map<String, String> readTaakdataForClosedTask(final String taskId) {
        final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                .taskId(taskId)
                .variableName(VAR_TASK_TAAKDATA)
                .singleResult();
        return historicVariableInstance != null ? (Map<String, String>) historicVariableInstance.getValue() : Collections.emptyMap();
    }

    public Map<String, String> updateTaakdata(final String taskId, final Map<String, String> taakdata) {
        cmmnTaskService.setVariableLocal(taskId, VAR_TASK_TAAKDATA, taakdata);
        return taakdata;
    }

    public List<CaseDefinition> listCaseDefinitions() {
        return cmmnRepositoryService.createCaseDefinitionQuery().latestVersion().list();
    }

    public CaseDefinition readCaseDefinition(final String caseDefinitionKey) {
        final CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery()
                .caseDefinitionKey(caseDefinitionKey)
                .latestVersion()
                .singleResult();
        if (caseDefinition != null) {
            return caseDefinition;
        } else {
            throw new RuntimeException(String.format("No case definition found for case definition key: '%s'", caseDefinitionKey));
        }
    }

    public List<HumanTask> listHumanTasks(final String caseDefinitionKey) {
        final CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinitionKey);
        return cmmnModel.getPrimaryCase().findPlanItemDefinitionsOfType(HumanTask.class);
    }

    public List<UserEventListener> listUserEventListeners(final String caseDefinitionKey) {
        final CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinitionKey);
        return cmmnModel.getPrimaryCase().findPlanItemDefinitionsOfType(UserEventListener.class);
    }

    /**
     * Terminate the case for a zaak.
     * This also terminates all open tasks related to the case,
     * This will also call {@Link EndCaseLifecycleListener}
     *
     * @param zaakUUID UUID of the zaak for which the caxse should be terminated.
     */
    public void terminateCase(final UUID zaakUUID) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        }
    }

    public Object findOpenCaseVariable(final String caseInstanceId, final String variableName) {
        return cmmnRuntimeService.getVariable(caseInstanceId, variableName);
    }

    public Object findVariableForCase(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();
        return caseInstance != null ? caseInstance.getCaseVariables().get(variableName) : null;
    }

    public Object readOpenCaseVariable(final String caseInstanceId, final String variableName) {
        final Object variableValue = findOpenCaseVariable(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for open case instance id '%s'", variableName, caseInstanceId));
        }
    }

    public void createVariableForCase(final UUID zaakUUID, final String variableName, final Object value) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.setVariable(caseInstance.getId(), variableName, value);
        } else {
            throw new RuntimeException(String.format("No case instance found for zaak with UUID: '%s'", zaakUUID.toString()));
        }
    }

    public Object findClosedCaseVariable(final String caseInstanceId, final String variableName) {
        return cmmnHistoryService.createHistoricVariableInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .variableName(variableName)
                .singleResult()
                .getValue();
    }

    public Object readClosedCaseVariable(final String caseInstanceId, final String variableName) {
        final Object variableValue = findClosedCaseVariable(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for closed case instance id '%s'", variableName, caseInstanceId));
        }
    }

    public Object readCaseVariable(final String caseInstanceId, final String variableName) {
        return isOpenCase(caseInstanceId)
                ? readOpenCaseVariable(caseInstanceId, variableName)
                : readClosedCaseVariable(caseInstanceId, variableName);
    }

    public TaskInfo readTask(final String taskId) {
        TaskInfo task = findOpenTask(taskId);
        if (task != null) {
            return task;
        }
        task = findClosedTask(taskId);
        if (task != null) {
            return task;
        }
        throw new RuntimeException(String.format("No task found with task id '%s'", taskId));
    }

    public boolean isOpenCase(final UUID zaakUUID) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .count() > 0;
    }

    public boolean isOpenCase(final String caseInstanceId) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .count() > 0;
    }

    public List<HistoricTaskLogEntry> listHistorieForTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(taskId).list();
    }

    private Task findOpenTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private HistoricTaskInstance readClosedTask(final String taskId) {
        final HistoricTaskInstance task = findClosedTask(taskId);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException(String.format("No closed task found with task id '%s'", taskId));
        }
    }

    private HistoricTaskInstance findClosedTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private List<PlanItemInstance> listEnabledPlanItemsForOpenCase(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateEnabled()
                .list();
    }

    private List<PlanItemInstance> listAvailableUserEventListenersForOpenCase(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateAvailable()
                .planItemDefinitionType(USER_EVENT_LISTENER)
                .list();
    }

    private CaseInstance findOpenCaseForZaak(final UUID zaakUUID) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .singleResult();
    }

    private HistoricCaseInstance findClosedCaseForZaak(final UUID zaakUUID) {
        return cmmnHistoryService.createHistoricCaseInstanceQuery()
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
