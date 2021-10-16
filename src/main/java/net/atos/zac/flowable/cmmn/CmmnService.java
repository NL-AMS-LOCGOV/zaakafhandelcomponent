/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.cmmn.ExtraheerGroepLifecycleListener.VAR_LOCAL_CANDIDATE_GROUP_ID;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;

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
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.idm.IdmService;

/**
 *
 */
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

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    public UUID getZaakUUID(final String caseInstanceId) {
        return (UUID) cmmnRuntimeService.getVariable(caseInstanceId, VAR_CASE_ZAAK_UUID);
    }

    public String getZaakIdentificatie(final String caseInstanceID) {
        return (String) cmmnRuntimeService.getVariable(caseInstanceID, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String getZaaktypeOmschrijving(final String caseInstanceID) {
        return (String) cmmnRuntimeService.getVariable(caseInstanceID, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public UUID getZaakUuidViaTaskId(final String taskId) {
        return (UUID) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAK_UUID);
    }

    public String getZaakIdentificatieViaTaskId(final String taskId) {
        return (String) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAK_IDENTIFICATIE);
    }

    public String getZaaktypeOmschrijvingViaTaskId(final String taskId) {
        return (String) cmmnTaskService.getVariable(taskId, VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
    }

    public List<TaskInfo> getTakenVoorZaak(final UUID zaakUUID) {
        final String caseInstanceId = getCaseInstanceId(zaakUUID);
        if (caseInstanceId != null) {
            final List<TaskInfo> taken = new ArrayList<>(getLopendeTakenBijZaak(caseInstanceId));
            taken.addAll(getBeeindigdeTakenVoorZaak(caseInstanceId));
            return taken;
        } else {
            return Collections.emptyList();
        }
    }

    private List<Task> getLopendeTakenBijZaak(final String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstanceId)
                .includeIdentityLinks()
                .list();
    }

    private List<HistoricTaskInstance> getBeeindigdeTakenVoorZaak(final String caseInstanceId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .finished()
                .includeIdentityLinks()
                .list();
    }

    public List<Task> getMijnTaken(final TaakSortering sortering, final String direction, final int firstResult, final int maxResults) {
        return getTaskSorting(getTaskQuery(), sortering, direction)
                .taskAssignee(ingelogdeMedewerker.getGebruikersnaam())
                .listPage(firstResult, maxResults);
    }

    public int getMijnTakenAantal() {
        return (int) getTaskQuery().taskAssignee(ingelogdeMedewerker.getGebruikersnaam()).count();
    }

    public List<Task> getWerkvoorraadTaken(final TaakSortering sortering, final String direction, final int firstResult, final int maxResults) {
        return getTaskSorting(getTaskQuery(), sortering, direction)
                .taskCandidateGroupIn(ingelogdeMedewerker.getGroupIds())
                .listPage(firstResult, maxResults);
    }

    public int getWerkvoorraadTakenAantal() {
        return (int) getTaskQuery()
                .taskCandidateGroupIn(ingelogdeMedewerker.getGroupIds())
                .count();
    }

    private TaskQuery getTaskQuery() {
        //hier kan nog filtering komen/mapping van tablerequest#getSearch
        return cmmnTaskService.createTaskQuery().includeIdentityLinks();
    }

    public List<PlanItemInstance> getPlanItemsVoorZaak(final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = getAvailablePlanItems(zaakUUID);
        planItems.addAll(getAvailableUserEventListeners(zaakUUID));
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

    public void startHumanTaskPlanItemInstance(final String planItemInstanceId, final String groupId) {
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstanceId)
                .transientVariable(VAR_LOCAL_CANDIDATE_GROUP_ID, groupId)
                .start();
    }

    public void startPlanItemInstance(final String planItemInstanceId) {
        final PlanItemInstance planItem = getPlanItem(planItemInstanceId);
        if (planItem.getPlanItemDefinitionType().equals(USER_EVENT_LISTENER)) {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
        } else {
            cmmnRuntimeService.startPlanItemInstance(planItemInstanceId);
        }
    }

    public PlanItemInstance getPlanItem(final String planItemInstanceId) {
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
            final Task task = getLopendeTaak(taskId);
            if (!userId.equals(task.getAssignee())) {
                cmmnTaskService.unclaim(taskId);
                cmmnTaskService.claim(taskId, userId);
            }
        } else {
            cmmnTaskService.unclaim(taskId);
        }
        return getLopendeTaak(taskId);
    }

    public Task saveTask(final Task task) {
        cmmnTaskService.saveTask(task);
        return getLopendeTaak(task.getId());
    }

    public TaskInfo completeTask(final String taskId) {
        cmmnTaskService.complete(taskId);
        return getAfgerondeTaak(taskId);
    }

    public TaskInfo getTaak(final String taskId) {
        final Task task = getLopendeTaak(taskId);
        return task != null ? task : getAfgerondeTaak(taskId);
    }

    public Task getLopendeTaak(final String taskId) {
        return cmmnTaskService.createTaskQuery()
                .taskId(taskId)
                .includeIdentityLinks()
                .singleResult();
    }

    private HistoricTaskInstance getAfgerondeTaak(final String taskId) {
        return cmmnHistoryService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .finished()
                .includeIdentityLinks()
                .singleResult();
    }

    public Group getZaakBehandelaarGroup(final String caseDefinitionKey) {
        final CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery()
                .caseDefinitionKey(caseDefinitionKey)
                .latestVersion()
                .singleResult();
        final String groupId = substringBetween(caseDefinition.getDescription(), ID_GROEP_ZAAK_OPEN, ID_GROEP_ZAAK_CLOSE);
        return idmService.getGroup(groupId);
    }

    private List<PlanItemInstance> getAvailablePlanItems(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateEnabled()
                .list();
    }

    private List<PlanItemInstance> getAvailableUserEventListeners(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateAvailable()
                .planItemDefinitionType(USER_EVENT_LISTENER)
                .list();
    }

    public String getCaseInstanceId(final UUID zaakUUID) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_CASE_ZAAK_UUID, zaakUUID)
                .singleResult();
        return caseInstance != null ? caseInstance.getId() : null;
    }

    public Group getPlanItemGroup(final String planItemInstanceId) {
        final String groupId = (String) cmmnRuntimeService.getLocalVariable(planItemInstanceId, VAR_LOCAL_CANDIDATE_GROUP_ID);
        return groupId != null ? idmService.getGroup(groupId) : null;
    }

    private static TaskQuery getTaskSorting(final TaskQuery taskQuery, final TaakSortering sortering, final String direction) {
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
