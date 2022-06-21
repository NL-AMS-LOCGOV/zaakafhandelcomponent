/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ASSIGNEE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_CANDIDATE_GROUP;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_DUE_DATE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_OWNER;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_TAAKDATA;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ZAAK_UUID;
import static net.atos.zac.util.JsonbUtil.JSONB;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.flowable.variable.api.history.HistoricVariableInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.mail.MailService;
import net.atos.zac.zoeken.IndexeerService;

/**
 *
 */
@ApplicationScoped
@Transactional
public class FlowableService {

    public static final String USER_TASK_DESCRIPTION_CHANGED = "USER_TASK_DESCRIPTION_CHANGED";

    private static final String VAR_CASE_RESULTAAT_TOELICHTING = "resultaatToelichting";

    public static final String VAR_CASE_ZAAK_UUID = "zaakUUID";

    public static final String VAR_CASE_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_CASE_ZAAKTYPE_UUUID = "zaaktypeUUID";

    public static final String VAR_CASE_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    private static final String VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD = "ontvangstbevestigingVerstuurd";

    private static final String VAR_CASE_DATUMTIJD_OPGESCHORT = "datumTijdOpgeschort";

    private static final String VAR_CASE_VERWACHTE_DAGEN_OPGESCHORT = "verwachteDagenOpgeschort";

    private static final String VAR_CASE_ONTVANKELIJK = "ontvankelijk";

    private static final String VAR_TASK_TAAKDATA = "taakdata";

    private static final String VAR_TASK_TAAKDOCUMENTEN = "taakdocumenten";

    private static final String VAR_TASK_TAAKINFORMATIE = "taakinformatie";

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
    private MailService mailService;

    @Inject
    private IndexeerService indexeerService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

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

    public List<Task> listOpenTasksAssignedToUser(final String userid, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskAssignee(userid)
                .listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasksAssignedToGroups(final Set<String> groupIds, final TaakSortering sortering, final String direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskCandidateGroupIn(groupIds)
                .ignoreAssigneeValue()
                .listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasksDueNow() {
        return cmmnTaskService.createTaskQuery()
                .taskAssigned()
                .taskDueBefore(tomorrow())
                .list();
    }

    public List<Task> listOpenTasksDueLater() {
        return cmmnTaskService.createTaskQuery()
                .taskAssigned()
                .taskDueAfter(DateUtils.addSeconds(tomorrow(), -1))
                .list();
    }

    private Date tomorrow() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), 1);
    }

    public int countOpenTasksAssignedToUser(final String userId) {
        return (int) cmmnTaskService.createTaskQuery().taskAssignee(userId).count();
    }

    public int countOpenTasksAssignedToGroups(final Set<String> groupIds) {
        return (int) cmmnTaskService.createTaskQuery().taskCandidateGroupIn(groupIds).ignoreAssigneeValue().count();
    }

    public List<Task> listOpenTasks(final UUID zaakUUID) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            return listOpenTasksForCase(caseInstance.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public long countOpenTasks(final UUID zaakUUID) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            return countOpenTasksForOpenCase(caseInstance.getId());
        } else {
            return 0;
        }
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
                .transientVariable(VAR_TRANSIENT_OWNER, loggedInUserInstance.get().getId())
                .transientVariable(VAR_TRANSIENT_CANDIDATE_GROUP, groupId)
                .transientVariable(VAR_TRANSIENT_ASSIGNEE, assignee)
                .transientVariable(VAR_TRANSIENT_ZAAK_UUID, zaakUUID)
                .transientVariable(VAR_TRANSIENT_DUE_DATE, dueDate)
                .transientVariable(VAR_TRANSIENT_TAAKDATA, taakdata)
                .start();
        indexeerService.addZaak(zaakUUID);
    }

    public void startUserEventListenerPlanItem(final String planItemInstanceId, final String resultaatToelichting) {
        final PlanItemInstance planItem = readOpenPlanItem(planItemInstanceId);
        if (StringUtils.isNotEmpty(resultaatToelichting)) {
            cmmnRuntimeService.setVariable(planItem.getCaseInstanceId(), VAR_CASE_ONTVANKELIJK, Boolean.FALSE);
            cmmnRuntimeService.setVariable(planItem.getCaseInstanceId(), VAR_CASE_RESULTAAT_TOELICHTING, resultaatToelichting);
        } else {
            cmmnRuntimeService.setVariable(planItem.getCaseInstanceId(), VAR_CASE_ONTVANKELIJK, Boolean.TRUE);
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
            throw new RuntimeException(String.format("No open plan item found with plan item instance id '%s'", planItemInstanceId));
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

    public HistoricTaskInstance completeTask(final String taskId, final UUID zaakUUID) {
        cmmnTaskService.complete(taskId);
        indexeerService.addZaak(zaakUUID);
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

    public boolean isOpenTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .count() > 0;
    }

    public List<HistoricTaskLogEntry> listHistorieForTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(taskId).list();
    }

    public UUID readZaakUUID(final String caseInstanceId) {
        return (UUID) readCaseVariable(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public UUID readZaakUUIDOpenCase(final String caseInstanceId) {
        return (UUID) readOpenCaseVariable(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public UUID readZaakUUIDClosedCase(final String caseInstanceId) {
        return (UUID) readClosedCaseVariable(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public String readZaakIdentificatie(final String caseInstanceId) {
        return (String) readCaseVariable(caseInstanceId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String readZaakIdentificatieOpenCase(final String caseInstanceId) {
        return (String) readOpenCaseVariable(caseInstanceId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String readZaakIdentificatieClosedCase(final String caseInstanceId) {
        return (String) readClosedCaseVariable(caseInstanceId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public Boolean findOntvangstbevestigingVerstuurd(final UUID zaakUUID) {
        return (Boolean) findCaseVariable(zaakUUID, VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD);
    }

    public void updateOntvangstbevestigingVerstuurd(final UUID zaakUUID, final Boolean ontvangstbevestigingVerstuurd) {
        updateVariable(zaakUUID, VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD, ontvangstbevestigingVerstuurd);
    }

    public UUID readZaaktypeUUIDOpenCase(final String caseInstanceId) {
        return (UUID) readOpenCaseVariable(caseInstanceId, VAR_CASE_ZAAKTYPE_UUUID);
    }

    public UUID readZaaktypeUUIDClosedCase(final String caseInstanceId) {
        return (UUID) readClosedCaseVariable(caseInstanceId, VAR_CASE_ZAAKTYPE_UUUID);
    }

    public String readZaaktypeOmschrijving(final String caseInstanceId) {
        return (String) readCaseVariable(caseInstanceId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public String readZaaktypeOmschrijvingOpenCase(final String caseInstanceId) {
        return (String) readOpenCaseVariable(caseInstanceId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public String readZaaktypeOmschrijvingClosedCase(final String caseInstanceId) {
        return (String) readClosedCaseVariable(caseInstanceId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public ZonedDateTime findDatumtijdOpgeschort(final UUID zaakUUID) {
        return (ZonedDateTime) findCaseVariable(zaakUUID, VAR_CASE_DATUMTIJD_OPGESCHORT);
    }

    public void updateDatumtijdOpgeschort(final UUID zaakUUID, final ZonedDateTime datumtijOpgeschort) {
        updateVariable(zaakUUID, VAR_CASE_DATUMTIJD_OPGESCHORT, datumtijOpgeschort);
    }

    public void removeDatumtijdOpgeschort(final UUID zaakUUID) {
        removeVariable(zaakUUID, VAR_CASE_DATUMTIJD_OPGESCHORT);
    }

    public Integer findVerwachteDagenOpgeschort(final UUID zaakUUID) {
        return (Integer) findCaseVariable(zaakUUID, VAR_CASE_VERWACHTE_DAGEN_OPGESCHORT);
    }

    public void updateVerwachteDagenOpgeschort(final UUID zaakUUID, final Integer verwachteDagenOpgeschort) {
        updateVariable(zaakUUID, VAR_CASE_VERWACHTE_DAGEN_OPGESCHORT, verwachteDagenOpgeschort);
    }

    public void removeVerwachteDagenOpgeschort(final UUID zaakUUID) {
        removeVariable(zaakUUID, VAR_CASE_VERWACHTE_DAGEN_OPGESCHORT);
    }

    public HashMap<String, String> findTaakdata(final String taskId) {
        return (HashMap<String, String>) findTaskVariable(taskId, VAR_TASK_TAAKDATA);
    }

    public HashMap<String, String> findTaakdataOpenTask(final String taskId) {
        return (HashMap<String, String>) findOpenTaskVariable(taskId, VAR_TASK_TAAKDATA);
    }

    public HashMap<String, String> findTaakdataClosedTask(final String taskId) {
        return (HashMap<String, String>) findClosedTaskVariable(taskId, VAR_TASK_TAAKDATA);
    }

    public void updateTaakdata(final String taskId, final Map<String, String> taakdata) {
        updateTaskVariable(taskId, VAR_TASK_TAAKDATA, taakdata);
    }

    public List<UUID> findTaakdocumentenOpenTask(final String taskId) {
        return (List<UUID>) findOpenTaskVariable(taskId, VAR_TASK_TAAKDOCUMENTEN);
    }

    public List<UUID> findTaakdocumentenClosedTask(final String taskId) {
        return (List<UUID>) findClosedTaskVariable(taskId, VAR_TASK_TAAKDOCUMENTEN);
    }

    public void updateTaakdocumenten(final String taskId, final List<UUID> taakdocumenten) {
        updateTaskVariable(taskId, VAR_TASK_TAAKDOCUMENTEN, taakdocumenten);
    }

    public HashMap<String, String> findTaakinformatieOpenTask(final String taskId) {
        return (HashMap<String, String>) findOpenTaskVariable(taskId, VAR_TASK_TAAKINFORMATIE);
    }

    public HashMap<String, String> findTaakinformatieClosedTask(final String taskId) {
        return (HashMap<String, String>) findClosedTaskVariable(taskId, VAR_TASK_TAAKINFORMATIE);
    }

    public void updateTaakinformatie(final String taskId, final Map<String, String> taakinformatie) {
        updateTaskVariable(taskId, VAR_TASK_TAAKINFORMATIE, taakinformatie);
    }

    private List<Task> listOpenTasksForCase(final String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstanceId)
                .includeIdentityLinks()
                .list();
    }

    private long countOpenTasksForOpenCase(final String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstanceId)
                .includeIdentityLinks()
                .count();
    }

    private List<HistoricTaskInstance> listClosedTasksForCase(final String caseInstanceId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .finished()
                .includeIdentityLinks()
                .list();
    }

    private Object findTaskVariable(final String taskId, final String variableName) {
        return isOpenTask(taskId)
                ? readOpenTaskVariable(taskId, variableName)
                : readClosedTaskVariable(taskId, variableName);
    }

    private Object findOpenTaskVariable(final String taskId, final String variableName) {
        return cmmnTaskService.getVariableLocal(taskId, variableName);
    }

    private Object readOpenTaskVariable(final String taskId, final String variableName) {
        final Object variableValue = findOpenTaskVariable(taskId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for open task id '%s'", variableName, taskId));
        }
    }

    private Object findClosedTaskVariable(final String taskId, final String variableName) {
        final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                .taskId(taskId)
                .variableName(variableName)
                .singleResult();
        return historicVariableInstance != null ? historicVariableInstance.getValue() : null;
    }

    private Object readClosedTaskVariable(final String taskId, final String variableName) {
        final Object variableValue = findClosedTaskVariable(taskId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for closed task id '%s'", variableName, taskId));
        }
    }

    private void updateTaskVariable(final String taskId, final String variableName, Object value) {
        cmmnTaskService.setVariableLocal(taskId, variableName, value);
    }


    private Object findOpenCaseVariable(final String caseInstanceId, final String variableName) {
        return cmmnRuntimeService.getVariable(caseInstanceId, variableName);
    }

    private Object findCaseVariable(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();
        if (caseInstance != null) {
            return caseInstance.getCaseVariables().get(variableName);
        } else {
            return findClosedCaseVariable(zaakUUID, variableName);
        }
    }

    private Object readCaseVariable(final String caseInstanceId, final String variableName) {
        return isOpenCase(caseInstanceId)
                ? readOpenCaseVariable(caseInstanceId, variableName)
                : readClosedCaseVariable(caseInstanceId, variableName);
    }

    private Object readOpenCaseVariable(final String caseInstanceId, final String variableName) {
        final Object variableValue = findOpenCaseVariable(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for open case instance id '%s'", variableName, caseInstanceId));
        }
    }

    private Object readClosedCaseVariable(final String caseInstanceId, final String variableName) {
        final Object variableValue = findClosedCaseVariable(caseInstanceId, variableName);
        if (variableValue != null) {
            return variableValue;
        } else {
            throw new RuntimeException(String.format("No variable found with name '%s' for closed case instance id '%s'", variableName, caseInstanceId));
        }
    }

    private void updateVariable(final UUID zaakUUID, final String variableName, final Object value) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.setVariable(caseInstance.getId(), variableName, value);
        } else {
            throw new RuntimeException(String.format("No case instance found for zaak with UUID: '%s'", zaakUUID.toString()));
        }
    }

    private Object findClosedCaseVariable(final UUID zaakUUID, final String variableName) {
        final HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();

        return historicCaseInstance != null ? historicCaseInstance.getCaseVariables().get(variableName) : null;
    }

    private Object findClosedCaseVariable(final String caseInstanceId, final String variableName) {
        final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .variableName(variableName)
                .singleResult();
        return historicVariableInstance != null ? historicVariableInstance.getValue() : null;
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

    private void removeVariable(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.removeVariable(caseInstance.getId(), variableName);
        } else {
            throw new RuntimeException(String.format("No case instance found for zaak with UUID: '%s'", zaakUUID.toString()));
        }
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
