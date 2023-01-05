/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_DOMEIN;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_UUUID;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_DATA;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_IDENTIFICATIE;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_UUID;
import static net.atos.zac.flowable.bpmn.CreateUserTaskInterceptor.VAR_PROCESS_OWNER;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ASSIGNEE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_CANDIDATE_GROUP;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_DUE_DATE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_OWNER;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_TAAKDATA;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ZAAK_UUID;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.HUMAN_TASK;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.PROCESS_TASK;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstanceBuilder;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.cmmn.model.UserEventListener;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
@Transactional
public class CMMNService {

    private static final Logger LOG = Logger.getLogger(CMMNService.class.getName());

    @Inject
    private CmmnRuntimeService cmmnRuntimeService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    @Inject
    private CmmnRepositoryService cmmnRepositoryService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ObjectsClientService objectsClientService;

    public List<PlanItemInstance> listHumanTaskPlanItems(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateEnabled()
                .planItemDefinitionType(HUMAN_TASK)
                .list();
    }

    public List<PlanItemInstance> listProcessTaskPlanItems(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateEnabled()
                .planItemDefinitionType(PROCESS_TASK)
                .list();
    }

    public List<PlanItemInstance> listUserEventListenerPlanItems(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateAvailable()
                .planItemDefinitionType(USER_EVENT_LISTENER)
                .list();
    }

    public void startCase(final Zaak zaak, final Zaaktype zaaktype, final ZaakafhandelParameters zaakafhandelParameters,
            final Map<String, Object> zaakData) {
        final String caseDefinitionKey = zaakafhandelParameters.getCaseDefinitionID();
        LOG.info(() -> String.format("Zaak %s: Starten zaak met CMMN model '%s'", zaak.getUuid(), caseDefinitionKey));
        try {
            final CaseInstanceBuilder caseInstanceBuilder = cmmnRuntimeService.createCaseInstanceBuilder()
                    .caseDefinitionKey(caseDefinitionKey)
                    .businessKey(zaak.getUuid().toString())
                    .variable(VAR_ZAAK_UUID, zaak.getUuid())
                    .variable(VAR_ZAAK_IDENTIFICATIE, zaak.getIdentificatie())
                    .variable(VAR_ZAAKTYPE_UUUID, zaaktype.getUUID())
                    .variable(VAR_ZAAKTYPE_OMSCHRIJVING, zaaktype.getOmschrijving())
                    .variable(VAR_ZAAKTYPE_DOMEIN, zaakafhandelParameters.getDomein());
            if (zaakData != null) {
                caseInstanceBuilder.variable(VAR_ZAAK_DATA, zaakData);
            }
            caseInstanceBuilder.start();
        } catch (final FlowableObjectNotFoundException flowableObjectNotFoundException) {
            LOG.severe(String.format("Zaak %s: CMMN model '%s' bestaat niet. Zaak is niet gestart.", zaak.getUuid(),
                                     caseDefinitionKey));
        }
    }

    /**
     * Terminate the case for a zaak.
     * This also terminates all open tasks related to the case,
     * This will also call {@Link EndCaseLifecycleListener}
     *
     * @param zaakUUID UUID of the zaak for which the caxse should be terminated.
     */
    public void terminateCase(final UUID zaakUUID) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .singleResult();
        if (caseInstance != null) {
            cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        }
    }

    public void startHumanTaskPlanItem(final String planItemInstanceId, final String groupId, final String assignee,
            final Date dueDate, final Map<String, String> taakdata, final UUID zaakUUID) {

        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstanceId)
                .transientVariable(VAR_TRANSIENT_OWNER, loggedInUserInstance.get().getId())
                .transientVariable(VAR_TRANSIENT_CANDIDATE_GROUP, groupId)
                .transientVariable(VAR_TRANSIENT_ASSIGNEE, assignee)
                .transientVariable(VAR_TRANSIENT_ZAAK_UUID, zaakUUID)
                .transientVariable(VAR_TRANSIENT_DUE_DATE, dueDate)
                .transientVariable(VAR_TRANSIENT_TAAKDATA, taakdata)
                .start();
    }

    public void startUserEventListenerPlanItem(final String planItemInstanceId) {
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
    }

    public void startProcessTaskPlanItem(final String planItemInstanceId, final Map<String, Object> processData) {
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstanceId)
                .childTaskVariables(
                        cmmnRuntimeService.getVariables(readOpenPlanItem(planItemInstanceId).getCaseInstanceId()))
                .childTaskVariables(processData)
                .childTaskVariable(VAR_PROCESS_OWNER, loggedInUserInstance.get().getId())
                .start();
    }

    public PlanItemInstance readOpenPlanItem(final String planItemInstanceId) {
        final PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemInstanceId(planItemInstanceId)
                .singleResult();
        if (planItemInstance != null) {
            return planItemInstance;
        } else {
            throw new RuntimeException(
                    String.format("No open plan item found with plan item instance id '%s'", planItemInstanceId));
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
            throw new RuntimeException(
                    String.format("No case definition found for case definition key: '%s'", caseDefinitionKey));
        }
    }

    public List<UserEventListener> listUserEventListeners(final String caseDefinitionKey) {
        return cmmnRepositoryService.getCmmnModel(caseDefinitionKey)
                .getPrimaryCase()
                .findPlanItemDefinitionsOfType(UserEventListener.class);
    }

    public List<HumanTask> listHumanTasks(final String caseDefinitionKey) {
        return cmmnRepositoryService.getCmmnModel(caseDefinitionKey)
                .getPrimaryCase()
                .findPlanItemDefinitionsOfType(HumanTask.class);
    }
}
