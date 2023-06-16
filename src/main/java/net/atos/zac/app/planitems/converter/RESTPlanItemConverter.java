/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.converter;

import static net.atos.zac.app.planitems.model.PlanItemType.HUMAN_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.PROCESS_TASK;
import static net.atos.zac.app.planitems.model.PlanItemType.USER_EVENT_LISTENER;
import static net.atos.zac.util.UriUtil.uuidFromURI;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.UserEventListenerActie;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.ReferentieTabelWaarde;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

/**
 *
 */
public class RESTPlanItemConverter {

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public List<RESTPlanItem> convertPlanItems(final List<PlanItemInstance> planItems, final Zaak zaak) {
        final UUID zaaktypeUUID = uuidFromURI(zaak.getZaaktype());
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                zaaktypeUUID);
        return planItems.stream()
                .map(planItemInstance -> this.convertPlanItem(planItemInstance, zaak.getUuid(), zaakafhandelParameters))
                .toList();
    }

    public RESTPlanItem convertPlanItem(final PlanItemInstance planItem, final UUID zaakUuid,
            final ZaakafhandelParameters zaakafhandelParameters) {
        final RESTPlanItem restPlanItem = new RESTPlanItem();
        restPlanItem.id = planItem.getId();
        restPlanItem.naam = planItem.getName();
        restPlanItem.zaakUuid = zaakUuid;
        restPlanItem.type = convertDefinitionType(planItem.getPlanItemDefinitionType());
        return switch (restPlanItem.type) {
            case USER_EVENT_LISTENER -> convertUserEventListener(restPlanItem, planItem, zaakafhandelParameters);
            case HUMAN_TASK -> convertHumanTask(restPlanItem, planItem, zaakafhandelParameters);
            case PROCESS_TASK -> convertProcessTask(restPlanItem);
        };
    }

    private RESTPlanItem convertUserEventListener(final RESTPlanItem restPlanItem,
            final PlanItemInstance UserEventListenerPlanItem, final ZaakafhandelParameters zaakafhandelParameters) {
        restPlanItem.userEventListenerActie = UserEventListenerActie.valueOf(
                UserEventListenerPlanItem.getPlanItemDefinitionId());
        restPlanItem.toelichting = zaakafhandelParameters.readUserEventListenerParameters(
                UserEventListenerPlanItem.getPlanItemDefinitionId()).getToelichting();
        return restPlanItem;
    }

    private RESTPlanItem convertHumanTask(final RESTPlanItem restPlanItem, final PlanItemInstance humanTaskPlanItem,
            final ZaakafhandelParameters zaakafhandelParameters) {
        zaakafhandelParameters.findHumanTaskParameter(humanTaskPlanItem.getPlanItemDefinitionId())
                .ifPresent(humanTaskParameters -> {
                    restPlanItem.actief = humanTaskParameters.isActief();
                    restPlanItem.formulierDefinitie = FormulierDefinitie.valueOf(humanTaskParameters.getFormulierDefinitieID());
                    humanTaskParameters.getReferentieTabellen().forEach(
                            rt -> restPlanItem.tabellen.put(rt.getVeld(), rt.getTabel().getWaarden().stream()
                                    .map(ReferentieTabelWaarde::getNaam)
                                    .toList()));
                    restPlanItem.groepId = humanTaskParameters.getGroepID();
                    if (humanTaskParameters.getDoorlooptijd() != null) {
                        restPlanItem.fataleDatum = LocalDate.now().plusDays(humanTaskParameters.getDoorlooptijd());
                    }
                });
        return restPlanItem;
    }

    private RESTPlanItem convertProcessTask(final RESTPlanItem restPlanItem) {
        return restPlanItem;
    }

    private static PlanItemType convertDefinitionType(final String planItemDefinitionType) {
        if (PlanItemDefinitionType.HUMAN_TASK.equals(planItemDefinitionType)) {
            return HUMAN_TASK;
        } else if (PlanItemDefinitionType.PROCESS_TASK.equals(planItemDefinitionType)) {
            return PROCESS_TASK;
        } else if (PlanItemDefinitionType.USER_EVENT_LISTENER.equals(planItemDefinitionType)) {
            return USER_EVENT_LISTENER;
        } else {
            throw new IllegalArgumentException(
                    String.format("Conversie van plan item definition type '%s' wordt niet ondersteund",
                                  planItemDefinitionType));
        }
    }
}
