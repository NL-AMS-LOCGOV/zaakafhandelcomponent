/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;
import static net.atos.zac.aanvraag.ProductAanvraagService.OBJECT_TYPE_OVERIGE_PRODUCT_AANVRAAG;
import static net.atos.zac.flowable.CaseVariablesService.VAR_ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.flowable.CaseVariablesService.VAR_ZAAKTYPE_UUUID;
import static net.atos.zac.flowable.CaseVariablesService.VAR_ZAAK_DATA;
import static net.atos.zac.flowable.CaseVariablesService.VAR_ZAAK_IDENTIFICATIE;
import static net.atos.zac.flowable.CaseVariablesService.VAR_ZAAK_UUID;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ASSIGNEE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_CANDIDATE_GROUP;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_DUE_DATE;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_OWNER;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_TAAKDATA;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.VAR_TRANSIENT_ZAAK_UUID;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.HUMAN_TASK;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.USER_EVENT_LISTENER;

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

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.UserEventListener;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.aanvraag.ProductAanvraag;
import net.atos.zac.authentication.LoggedInUser;

@ApplicationScoped
@Transactional
public class CaseService {

    private static final Logger LOG = Logger.getLogger(CaseService.class.getName());

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

    public List<PlanItemInstance> listUserEventListenerPlanItems(final UUID zaakUUID) {
        return cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseVariableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .planItemInstanceStateAvailable()
                .planItemDefinitionType(USER_EVENT_LISTENER)
                .list();
    }

    public void startCase(final String caseDefinitionKey, final Zaak zaak, final Zaaktype zaaktype) {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder()
                    .caseDefinitionKey(caseDefinitionKey)
                    .businessKey(zaak.getUuid().toString())
                    .variable(VAR_ZAAK_UUID, zaak.getUuid())
                    .variable(VAR_ZAAK_IDENTIFICATIE, zaak.getIdentificatie())
                    .variable(VAR_ZAAKTYPE_UUUID, uuidFromURI(zaaktype.getUrl()))
                    .variable(VAR_ZAAKTYPE_OMSCHRIJVING, zaaktype.getOmschrijving())
                    .variable(VAR_ZAAK_DATA, readZaakData(zaak))
                    .start();
        } catch (final FlowableObjectNotFoundException flowableObjectNotFoundException) {
            LOG.warning(String.format("Zaak %s: Case with definition key '%s' not found. No Case started.",
                                      caseDefinitionKey, zaak.getUuid()));
        }
    }

    public void startHumanTask(final PlanItemInstance planItemInstance, final String groupId, final String assignee,
            final Date dueDate,
            final Map<String, String> taakdata, final UUID zaakUUID) {

        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(planItemInstance.getId())
                .transientVariable(VAR_TRANSIENT_OWNER, loggedInUserInstance.get().getId())
                .transientVariable(VAR_TRANSIENT_CANDIDATE_GROUP, groupId)
                .transientVariable(VAR_TRANSIENT_ASSIGNEE, assignee)
                .transientVariable(VAR_TRANSIENT_ZAAK_UUID, zaakUUID)
                .transientVariable(VAR_TRANSIENT_DUE_DATE, dueDate)
                .transientVariable(VAR_TRANSIENT_TAAKDATA, taakdata)
                .start();
    }

    public void startUserEventListener(final String planItemInstanceId) {
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
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
        final CaseInstance caseInstance = findOpenCase(zaakUUID);
        if (caseInstance != null) {
            cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        }
    }

    public boolean isOpenCase(final UUID zaakUUID) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .count() > 0;
    }

    public boolean isOpenCase(final String caseInstanceId) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .count() > 0;
    }

    CaseInstance findOpenCase(final UUID zaakUUID) {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .singleResult();
    }

    HistoricCaseInstance findClosedCase(final UUID zaakUUID) {
        return cmmnHistoryService.createHistoricCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .singleResult();
    }

    private Map<String, Object> readZaakData(final Zaak zaak) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        zaakobjectListParameters.setZaak(zaak.getUrl());
        zaakobjectListParameters.setObjectType(OVERIGE);
        return zrcClientService.listZaakobjecten(zaakobjectListParameters).getSinglePageResults().stream()
                .filter(zaakobject -> OBJECT_TYPE_OVERIGE_PRODUCT_AANVRAAG.equals(zaakobject.getObjectTypeOverige()))
                .map(this::readZaakData)
                .findAny()
                .orElse(Collections.emptyMap());
    }

    private Map<String, Object> readZaakData(final Zaakobject zaakobject) {
        final ORObject object = objectsClientService.readObject(uuidFromURI(zaakobject.getObject()));
        final ProductAanvraag productAanvraag = new ProductAanvraag(object.getRecord().getData());
        return productAanvraag.getData();
    }
}
