/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.app.taken.model.TaakStatus.NIET_TOEGEKEND;
import static net.atos.zac.app.taken.model.TaakStatus.TOEGEKEND;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.TaskUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

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
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public List<RESTTaak> convertTaskInfoList(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(this::convertTaskInfo)
                .collect(Collectors.toList());
    }

    public RESTTaak convertTaskInfo(final TaskInfo task) {
        final RESTTaak restTaak = convertPartialTaskInfo(task);
        restTaak.formulierDefinitie = zaakafhandelParameterService.findFormulierDefinitie(task);
        return restTaak;
    }

    public RESTTaak convertPartialTaskInfo(final TaskInfo task) {
        final RESTTaak restTaak = new RESTTaak();
        restTaak.id = task.getId();
        restTaak.naam = task.getName();
        restTaak.toelichting = task.getDescription();
        restTaak.creatiedatumTijd = convertToZonedDateTime(task.getCreateTime());
        restTaak.toekenningsdatumTijd = convertToZonedDateTime(task.getClaimTime());
        restTaak.streefdatum = convertToLocalDate(task.getDueDate());
        restTaak.behandelaar = medewerkerConverter.convertGebruikersnaam(task.getAssignee());
        restTaak.groep = groepConverter.convertGroupId(TaskUtil.extractGroupId(task.getIdentityLinks()));
        restTaak.status = convertToStatus(task);
        restTaak.zaakUUID = flowableService.readZaakUuidForTask(task.getId());
        restTaak.zaakIdentificatie = flowableService.readZaakIdentificatieForTask(task.getId());
        restTaak.zaaktypeOmschrijving = flowableService.readZaaktypeOmschrijvingorTask(task.getId());

        return restTaak;
    }

    public RESTTaak convertTaskInfo(final TaskInfo task, final Map<String, String> taakdata) {
        final RESTTaak restTaak = convertTaskInfo(task);
        restTaak.taakdata = taakdata;
        return restTaak;
    }

    public RESTTaak convertTaskInfo(final TaskInfo task, final FormulierDefinitie formulierDefinitie, final Map<String, String> taakdata) {
        final RESTTaak restTaak = convertTaskInfo(task, taakdata);
        restTaak.formulierDefinitie = formulierDefinitie;
        return restTaak;
    }

    public void convertRESTTaak(final RESTTaak restTaak, final Task task) {
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.streefdatum));
    }

    private static TaakStatus convertToStatus(final TaskInfo taskInfo) {
        if (taskInfo instanceof Task) {
            if (taskInfo.getAssignee() == null) {
                return NIET_TOEGEKEND;
            } else {
                return TOEGEKEND;
            }
        } else {
            return AFGEROND;
        }
    }
}
