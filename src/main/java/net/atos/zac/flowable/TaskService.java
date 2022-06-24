/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.util.JsonbUtil.JSONB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;

import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.shared.model.SorteerRichting;

@ApplicationScoped
@Transactional
public class TaskService {

    public static final String USER_TASK_DESCRIPTION_CHANGED = "USER_TASK_DESCRIPTION_CHANGED";

    @Inject
    private CmmnTaskService cmmnTaskService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    @Inject
    private CmmnRepositoryService cmmnRepositoryService;

    @Inject
    private FlowableService flowableService;

    public record TaskDescriptionChangedData(String newDescription, String previousDescription) {}

    public List<Task> listOpenTasksAssignedToUser(final String userid, final TaakSortering sortering, final SorteerRichting direction,
            final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, direction)
                .taskAssignee(userid)
                .listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasks(final TaakSortering sortering, final SorteerRichting SorteerRichting, final int firstResult, final int maxResults) {
        return createOpenTasksQueryWithSorting(sortering, SorteerRichting).listPage(firstResult, maxResults);
    }

    public List<Task> listOpenTasksAssignedToGroups(final Set<String> groupIds, final TaakSortering sortering, final SorteerRichting direction,
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

    public List<TaskInfo> listTasksForOpenCase(final UUID zaakUUID) {
        final List<TaskInfo> tasks = new ArrayList<>();
        final CaseInstance caseInstance = flowableService.findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            tasks.addAll(listOpenTasksForCase(caseInstance.getId()));
            tasks.addAll(listClosedTasksForCase(caseInstance.getId()));
        }
        return tasks;
    }

    public List<HistoricTaskInstance> listTasksForClosedCase(final UUID zaakUUID) {
        final HistoricCaseInstance historicCaseInstance = flowableService.findClosedCaseForZaak(zaakUUID);
        if (historicCaseInstance != null) {
            return listClosedTasksForCase(historicCaseInstance.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<Task> listOpenTasks(final UUID zaakUUID) {
        final CaseInstance caseInstance = flowableService.findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            return listOpenTasksForCase(caseInstance.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<HumanTask> listHumanTasks(final String caseDefinitionKey) {
        final CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinitionKey);
        return cmmnModel.getPrimaryCase().findPlanItemDefinitionsOfType(HumanTask.class);
    }

    public List<HistoricTaskLogEntry> listHistorieForTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(taskId).list();
    }

    public long countOpenTasks(final UUID zaakUUID) {
        final CaseInstance caseInstance = flowableService.findOpenCaseForZaak(zaakUUID);
        if (caseInstance != null) {
            return cmmnTaskService.createTaskQuery()
                    .caseInstanceId(caseInstance.getId())
                    .includeIdentityLinks()
                    .count();
        } else {
            return 0;
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

    public int countOpenTasksAssignedToUser(final String userId) {
        return (int) cmmnTaskService.createTaskQuery().taskAssignee(userId).count();
    }

    public int countOpenTasksAssignedToGroups(final Set<String> groupIds) {
        return (int) cmmnTaskService.createTaskQuery().taskCandidateGroupIn(groupIds).ignoreAssigneeValue().count();
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

    private Task findOpenTask(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private HistoricTaskInstance findClosedTask(final String taskId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private HistoricTaskInstance readClosedTask(final String taskId) {
        final HistoricTaskInstance historicTaskInstance = findClosedTask(taskId);
        if (historicTaskInstance != null) {
            return historicTaskInstance;
        } else {
            throw new RuntimeException(String.format("No closed task found with task id '%s'", taskId));
        }
    }

    private TaskQuery createOpenTasksQueryWithSorting(final TaakSortering sortering, final SorteerRichting direction) {
        final TaskQuery taskQuery = cmmnTaskService.createTaskQuery().includeIdentityLinks();
        if (sortering != null) {
            final TaskQuery sortedTaskQuery = switch (sortering) {
                case ID -> taskQuery.orderByTaskId();
                case TAAKNAAM -> taskQuery.orderByTaskName();
                case CREATIEDATUM -> taskQuery.orderByTaskCreateTime();
                case STREEFDATUM -> taskQuery.orderByTaskDueDate();
                case BEHANDELAAR -> taskQuery.orderByTaskAssignee();
            };
            return direction.equals(SorteerRichting.ASCENDING) ? sortedTaskQuery.asc() : sortedTaskQuery.desc();
        }
        return taskQuery;
    }

    private Date tomorrow() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), 1);
    }
}
