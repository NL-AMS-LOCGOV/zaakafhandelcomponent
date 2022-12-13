/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.app.taken.model.TaakStatus.NIET_TOEGEKEND;
import static net.atos.zac.app.taken.model.TaakStatus.TOEGEKEND;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_UUID;
import static net.atos.zac.util.JsonbUtil.FIELD_VISIBILITY_STRATEGY;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.annotation.JsonbCreator;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.engine.HistoryService;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;

import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.shared.model.SorteerRichting;

@ApplicationScoped
@Transactional
public class TakenService {

    public static final String USER_TASK_DESCRIPTION_CHANGED = "USER_TASK_DESCRIPTION_CHANGED";

    @Inject
    private CmmnTaskService cmmnTaskService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    @Inject
    private org.flowable.engine.TaskService bpmnTaskService;

    @Inject
    private HistoryService bpmnHistoryService;

    public record TaskDescriptionChangedData(String previousDescription, String newDescription) {
        @JsonbCreator
        public TaskDescriptionChangedData {
        }
    }

    public List<Task> listOpenTasks(final TaakSortering taakSortering, final SorteerRichting sorteerRichting,
            final int firstResult, final int maxResults) {
        final List<Task> tasks = new LinkedList<>();
        tasks.addAll(
                createOpenTasksQueryWithSorting(cmmnTaskService.createTaskQuery(), taakSortering, sorteerRichting)
                        .listPage(firstResult, maxResults));
        tasks.addAll(
                createOpenTasksQueryWithSorting(bpmnTaskService.createTaskQuery(), taakSortering, sorteerRichting)
                        .listPage(firstResult, maxResults));
        return tasks;
    }

    public long countOpenTasks() {
        return cmmnTaskService.createTaskQuery().count() + bpmnTaskService.createTaskQuery().count();
    }

    public List<Task> listOpenTasksDueNow() {
        final List<Task> tasks = new LinkedList<>();
        final var tomorrow = tomorrow();
        tasks.addAll(cmmnTaskService.createTaskQuery()
                             .taskAssigned()
                             .taskDueBefore(tomorrow)
                             .list());
        tasks.addAll(bpmnTaskService.createTaskQuery()
                             .taskAssigned()
                             .taskDueBefore(tomorrow)
                             .list());
        return tasks;
    }

    public List<Task> listOpenTasksDueLater() {
        final List<Task> tasks = new LinkedList<>();
        final var taskDueAfter = DateUtils.addSeconds(tomorrow(), -1);
        tasks.addAll(cmmnTaskService.createTaskQuery()
                             .taskAssigned()
                             .taskDueAfter(taskDueAfter)
                             .list());
        tasks.addAll(bpmnTaskService.createTaskQuery()
                             .taskAssigned()
                             .taskDueAfter(taskDueAfter)
                             .list());
        return tasks;
    }

    public List<? extends TaskInfo> listTasksForZaak(final UUID zaakUUID) {
        final List<TaskInfo> tasks = new ArrayList<>();
        tasks.addAll(cmmnTaskService.createTaskQuery()
                             .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .includeCaseVariables()
                             .includeTaskLocalVariables()
                             .includeIdentityLinks()
                             .list());
        tasks.addAll(cmmnHistoryService.createHistoricTaskInstanceQuery()
                             .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .includeCaseVariables()
                             .includeTaskLocalVariables()
                             .includeIdentityLinks()
                             .finished()
                             .list());
        tasks.addAll(bpmnTaskService.createTaskQuery()
                             .processVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .includeProcessVariables()
                             .includeTaskLocalVariables()
                             .includeIdentityLinks()
                             .list());
        tasks.addAll(bpmnHistoryService.createHistoricTaskInstanceQuery()
                             .processVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .includeProcessVariables()
                             .includeTaskLocalVariables()
                             .includeIdentityLinks()
                             .finished()
                             .list());
        return tasks;
    }

    public List<Task> listOpenTasksForZaak(final UUID zaakUUID) {
        final List<Task> tasks = new ArrayList<>();
        tasks.addAll(cmmnTaskService.createTaskQuery()
                             .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .list());
        tasks.addAll(bpmnTaskService.createTaskQuery()
                             .processVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                             .list());
        return tasks;
    }

    public long countOpenTasksForZaak(final UUID zaakUUID) {
        return cmmnTaskService.createTaskQuery().caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID).count() +
                bpmnTaskService.createTaskQuery().processVariableValueEquals(VAR_ZAAK_UUID, zaakUUID).count();
    }

    public List<HistoricTaskLogEntry> listHistorieForTask(final String taskId) {
        final List<HistoricTaskLogEntry> historicTaskLogEntries = new LinkedList<>();
        historicTaskLogEntries.addAll(cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(taskId).list());
        historicTaskLogEntries.addAll(bpmnHistoryService.createHistoricTaskLogEntryQuery().taskId(taskId).list());
        return historicTaskLogEntries;
    }

    public TaskInfo readTask(final String taskId) {
        final Task task = findOpenTask(taskId);
        if (task != null) {
            return task;
        }
        final HistoricTaskInstance historicTaskInstance = findClosedTask(taskId);
        if (historicTaskInstance != null) {
            return historicTaskInstance;
        }
        throw new RuntimeException(String.format("No task found with task id '%s'", taskId));
    }

    public Task readOpenTask(final String taskId) {
        final Task task = findOpenTask(taskId);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException(String.format("No open task found with task id '%s'", taskId));
        }
    }

    public HistoricTaskInstance readClosedTask(final String taskId) {
        final HistoricTaskInstance historicTaskInstance = findClosedTask(taskId);
        if (historicTaskInstance != null) {
            return historicTaskInstance;
        } else {
            throw new RuntimeException(String.format("No closed task found with task id '%s'", taskId));
        }
    }

    public Task updateTask(final Task task) {
        final Task originalTask = readOpenTask(task.getId());
        String descriptionChangedData = null;
        if (!StringUtils.equals(originalTask.getDescription(), task.getDescription())) {
            descriptionChangedData = FIELD_VISIBILITY_STRATEGY.toJson(new TaskDescriptionChangedData(
                    defaultString(originalTask.getDescription(), StringUtils.EMPTY),
                    defaultString(task.getDescription(), StringUtils.EMPTY)));
        }
        if (isCmmnTask(task)) {
            cmmnTaskService.saveTask(task);
            if (descriptionChangedData != null) {
                cmmnHistoryService.createHistoricTaskLogEntryBuilder(originalTask)
                        .type(USER_TASK_DESCRIPTION_CHANGED)
                        .data(descriptionChangedData)
                        .create();
            }
        } else {
            bpmnTaskService.saveTask(task);
            if (descriptionChangedData != null) {
                bpmnHistoryService.createHistoricTaskLogEntryBuilder(originalTask)
                        .type(USER_TASK_DESCRIPTION_CHANGED)
                        .data(descriptionChangedData)
                        .create();
            }
        }
        return readOpenTask(task.getId());
    }

    public HistoricTaskInstance completeTask(final Task Task) {
        if (isCmmnTask(Task)) {
            cmmnTaskService.complete(Task.getId());
        } else {
            bpmnTaskService.complete(Task.getId());
        }
        return readClosedTask(Task.getId());
    }

    public Task assignTaskToUser(final Task task, final String userId) {
        if (isCmmnTask(task)) {
            if (userId != null) {
                cmmnTaskService.setAssignee(task.getId(), userId);
            } else {
                cmmnTaskService.unclaim(task.getId());
            }
        } else {
            if (userId != null) {
                bpmnTaskService.setAssignee(task.getId(), userId);
            } else {
                bpmnTaskService.unclaim(task.getId());
            }
        }
        return readOpenTask(task.getId());
    }

    public Task assignTaskToGroup(final Task task, final String groupId) {
        if (isCmmnTask(task)) {
            task.getIdentityLinks().stream()
                    .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                    .map(IdentityLinkInfo::getGroupId)
                    .forEach(currentGroupId -> cmmnTaskService.deleteGroupIdentityLink(task.getId(), currentGroupId,
                                                                                       IdentityLinkType.CANDIDATE));
            cmmnTaskService.addGroupIdentityLink(task.getId(), groupId, IdentityLinkType.CANDIDATE);
        } else {
            task.getIdentityLinks().stream()
                    .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                    .map(IdentityLinkInfo::getGroupId)
                    .forEach(currentGroupId -> bpmnTaskService.deleteGroupIdentityLink(task.getId(), currentGroupId,
                                                                                       IdentityLinkType.CANDIDATE));
            bpmnTaskService.addGroupIdentityLink(task.getId(), groupId, IdentityLinkType.CANDIDATE);
        }
        return readOpenTask(task.getId());
    }

    public boolean isCmmnTask(final TaskInfo taskInfo) {
        return !taskInfo.getCaseVariables().isEmpty();
    }

    public TaakStatus getTaakStatus(final TaskInfo taskInfo) {
        return taskInfo instanceof Task ? (taskInfo.getAssignee() == null ? NIET_TOEGEKEND : TOEGEKEND) : AFGEROND;
    }

    private Task findOpenTask(final String taskId) {
        Task task = cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeCaseVariables()
                .includeTaskLocalVariables()
                .includeIdentityLinks()
                .singleResult();
        if (task == null) {
            task = bpmnTaskService.createTaskQuery()
                    .taskId(taskId)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .includeIdentityLinks()
                    .singleResult();
        }
        return task;
    }

    private HistoricTaskInstance findClosedTask(final String taskId) {
        HistoricTaskInstance task = cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .includeCaseVariables()
                .includeTaskLocalVariables()
                .includeIdentityLinks()
                .singleResult();
        if (task == null) {
            task = bpmnHistoryService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .includeIdentityLinks()
                    .singleResult();
        }
        return task;
    }

    private TaskQuery createOpenTasksQueryWithSorting(final TaskQuery taskQuery, final TaakSortering taakSortering,
            final SorteerRichting sorteerRichting) {
        if (taakSortering != null) {
            final TaskQuery sortedTaskQuery = switch (taakSortering) {
                case ID -> taskQuery.orderByTaskId();
                case TAAKNAAM -> taskQuery.orderByTaskName();
                case CREATIEDATUM -> taskQuery.orderByTaskCreateTime();
                case FATALEDATUM -> taskQuery.orderByTaskDueDate();
                case BEHANDELAAR -> taskQuery.orderByTaskAssignee();
            };
            return sorteerRichting.equals(SorteerRichting.ASCENDING) ? sortedTaskQuery.asc() : sortedTaskQuery.desc();
        }
        return taskQuery;
    }

    private Date tomorrow() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), 1);
    }
}
