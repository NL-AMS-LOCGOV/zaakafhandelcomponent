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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.rechten.RechtOperatie;
import net.atos.zac.rechten.TaakRechten;

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

    public List<RESTTaak> convertTasks(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(this::convertTask)
                .collect(Collectors.toList());
    }

    public RESTTaak convertTask(final TaskInfo task) {
        final RESTTaak restTaak = convertTaskInfo(task);
        if (task instanceof Task) {
            restTaak.zaakUUID = flowableService.readZaakUuidForTask(task.getId());
            restTaak.zaakIdentificatie = flowableService.readZaakIdentificatieForTask(task.getId());
            restTaak.zaaktypeOmschrijving = flowableService.readZaaktypeIdentificatieForTask(task.getId());
        }

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        restTaak.rechten = getRechten(task);
        return restTaak;
    }

    public RESTTaak convertTask(final TaskInfo task, final String taakBehandelFormulier, final Map<String, String> taakdata) {
        final RESTTaak restTaak = convertTask(task);
        restTaak.taakBehandelFormulier = taakBehandelFormulier;
        restTaak.taakdata = taakdata;
        return restTaak;
    }

    public void convertTaak(final RESTTaak restTaak, final Task task) {
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.streefdatum));
    }

    private RESTTaak convertTaskInfo(final TaskInfo taskInfo) {
        final RESTTaak restTaak = new RESTTaak();
        restTaak.id = taskInfo.getId();
        restTaak.naam = taskInfo.getName();
        restTaak.toelichting = taskInfo.getDescription();
        restTaak.creatiedatumTijd = convertToZonedDateTime(taskInfo.getCreateTime());
        restTaak.toekenningsdatumTijd = convertToZonedDateTime(taskInfo.getClaimTime());
        restTaak.streefdatum = convertToLocalDate(taskInfo.getDueDate());
        restTaak.behandelaar = medewerkerConverter.convertGebruikersnaam(taskInfo.getAssignee());
        restTaak.groep = groepConverter.convertGroupId(extractGroupId(taskInfo.getIdentityLinks()));
        restTaak.status = convertToStatus(taskInfo);
        return restTaak;
    }

    private static String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
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

    private Map<RechtOperatie, Boolean> getRechten(final TaskInfo taskInfo) {
        final Map<RechtOperatie, Boolean> rechten = new HashMap<>();

        final String groepId = extractGroupId(taskInfo.getIdentityLinks());
        final TaakStatus status = convertToStatus(taskInfo);

        rechten.put(RechtOperatie.TOEKENNEN, TaakRechten.isToekennenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId));
        rechten.put(RechtOperatie.VRIJGEVEN, TaakRechten.isVrijgevenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));
        rechten.put(RechtOperatie.TOEKENNEN_AAN_MIJ, TaakRechten.isKenToeAanMijToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));
        rechten.put(RechtOperatie.BEHANDELEN, TaakRechten.isBehandelenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));

        return rechten;
    }
}
