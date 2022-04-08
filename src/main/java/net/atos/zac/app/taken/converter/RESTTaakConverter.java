/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.app.taken.model.TaakStatus.NIET_TOEGEKEND;
import static net.atos.zac.app.taken.model.TaakStatus.TOEGEKEND;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAKTYPE_UUUID;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAK_IDENTIFICATIE;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAK_UUID;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;

/**
 *
 */
public class RESTTaakConverter {

    @Inject
    private FlowableService flowableService;

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public List<RESTTaak> convertTasksForOpenCase(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(task -> convertTaskForOpenCase(task, false))
                .toList();
    }

    public List<RESTTaak> convertTasksForClosedCase(final List<HistoricTaskInstance> tasks) {
        return tasks.stream()
                .map(task -> convertTaskForClosedCase(task, false))
                .toList();
    }

    public RESTTaak convertTask(final TaskInfo task) {
        return flowableService.isOpenCase(task.getScopeId()) ? convertTaskForOpenCase(task, true) : convertTaskForClosedCase((HistoricTaskInstance) task, true);
    }

    public RESTTaak convertTaskForOpenCase(final TaskInfo task) {
        return convertTaskForOpenCase(task, true);
    }

    public void updateOpenTask(final Task task, final RESTTaak restTaak) {
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.streefdatum));
    }

    private RESTTaak convertTaskForOpenCase(final TaskInfo task, final boolean withTaakdata) {
        return task instanceof Task ? convertOpenTask((Task) task, withTaakdata) : convertClosedTaskForOpenCase((HistoricTaskInstance) task, withTaakdata);
    }

    private RESTTaak convertTaskForClosedCase(final HistoricTaskInstance task, final boolean withTaakdata) {
        final RESTTaak restTaak = convertTaskInfoForClosedCase(task);
        restTaak.status = AFGEROND;
        if (withTaakdata) {
            restTaak.taakdata = flowableService.readTaakdataForClosedTask(task.getId());
        }
        return restTaak;
    }

    private RESTTaak convertOpenTask(final Task task, boolean withTaakdata) {
        final RESTTaak restTaak = convertTaskInfoForOpenCase(task);
        restTaak.status = task.getAssignee() == null ? NIET_TOEGEKEND : TOEGEKEND;
        if (withTaakdata) {
            restTaak.taakdata = flowableService.readTaakdataForOpenTask(task.getId());
        }
        return restTaak;
    }

    private RESTTaak convertClosedTaskForOpenCase(final HistoricTaskInstance task, boolean withTaakdata) {
        final RESTTaak restTaak = convertTaskInfoForOpenCase(task);
        restTaak.status = AFGEROND;
        if (withTaakdata) {
            restTaak.taakdata = flowableService.readTaakdataForClosedTask(task.getId());
        }
        return restTaak;
    }

    private RESTTaak convertTaskInfoForOpenCase(final TaskInfo taskInfo) {
        final RESTTaak restTaak = new RESTTaak();
        restTaak.zaakUUID = (UUID) flowableService.readOpenCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAK_UUID);
        restTaak.zaakIdentificatie = (String) flowableService.readOpenCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAK_IDENTIFICATIE);
        restTaak.zaaktypeOmschrijving = (String) flowableService.readOpenCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
        final UUID zaaktypeUUID = (UUID) flowableService.readOpenCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAKTYPE_UUUID);
        return convertTaskInfoForCase(restTaak, taskInfo, zaaktypeUUID);
    }

    private RESTTaak convertTaskInfoForClosedCase(final HistoricTaskInstance taskInfo) {
        final RESTTaak restTaak = new RESTTaak();
        restTaak.zaakUUID = (UUID) flowableService.readClosedCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAK_UUID);
        restTaak.zaakIdentificatie = (String) flowableService.readClosedCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAK_IDENTIFICATIE);
        restTaak.zaaktypeOmschrijving = (String) flowableService.readClosedCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
        final UUID zaaktypeUUID = (UUID) flowableService.readClosedCaseVariable(taskInfo.getScopeId(), VAR_CASE_ZAAKTYPE_UUUID);
        return convertTaskInfoForCase(restTaak, taskInfo, zaaktypeUUID);
    }

    private RESTTaak convertTaskInfoForCase(final RESTTaak restTaak, final TaskInfo taskInfo, final UUID zaaktypeUUID) {
        restTaak.id = taskInfo.getId();
        restTaak.naam = taskInfo.getName();
        restTaak.toelichting = taskInfo.getDescription();
        restTaak.creatiedatumTijd = convertToZonedDateTime(taskInfo.getCreateTime());
        restTaak.toekenningsdatumTijd = convertToZonedDateTime(taskInfo.getClaimTime());
        restTaak.streefdatum = convertToLocalDate(taskInfo.getDueDate());
        restTaak.behandelaar = medewerkerConverter.convertGebruikersnaam(taskInfo.getAssignee());
        restTaak.groep = groepConverter.convertGroupId(extractGroupId(taskInfo.getIdentityLinks()));
        restTaak.formulierDefinitie = zaakafhandelParameterService.findFormulierDefinitie(zaaktypeUUID, taskInfo.getTaskDefinitionKey());
        return restTaak;
    }

    private String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
    }
}
