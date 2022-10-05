/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.util.List;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.planitems.model.HumanTaskFormulierKoppeling;
import net.atos.zac.app.policy.converter.RESTActiesConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.flowable.TaskVariablesService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.TaakActies;

/**
 *
 */
public class RESTTaakConverter {

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private TaskService taskService;

    @Inject
    private RESTGroupConverter groepConverter;

    @Inject
    private RESTUserConverter medewerkerConverter;

    @Inject
    private TaskVariablesService taskVariablesService;

    @Inject
    private RESTActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;


    public List<RESTTaak> convert(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(task -> convert(task, false))
                .toList();
    }

    public RESTTaak convert(final TaskInfo taskInfo, boolean withTaakdata) {
        final String zaaktypeOmschrijving = caseVariablesService.readZaaktypeOmschrijving(taskInfo.getScopeId());
        final TaakActies acties = policyService.readTaakActies(taskInfo, zaaktypeOmschrijving);
        final RESTTaak restTaak = new RESTTaak();
        restTaak.id = taskInfo.getId();
        restTaak.acties = actiesConverter.convert(acties);
        restTaak.naam = taskInfo.getName();
        restTaak.zaakUuid = caseVariablesService.readZaakUUID(taskInfo.getScopeId());
        restTaak.zaakIdentificatie = caseVariablesService.readZaakIdentificatie(taskInfo.getScopeId());
        restTaak.status = taskService.getTaakStatus(taskInfo);
        ;
        if (acties.getLezen()) {
            restTaak.toelichting = taskInfo.getDescription();
            restTaak.creatiedatumTijd = convertToZonedDateTime(taskInfo.getCreateTime());
            restTaak.toekenningsdatumTijd = convertToZonedDateTime(taskInfo.getClaimTime());
            restTaak.streefdatum = convertToLocalDate(taskInfo.getDueDate());
            restTaak.behandelaar = medewerkerConverter.convertUserId(taskInfo.getAssignee());
            restTaak.groep = groepConverter.convertGroupId(extractGroupId(taskInfo.getIdentityLinks()));
            restTaak.formulierDefinitie = HumanTaskFormulierKoppeling.readFormulierDefinitie(taskInfo.getTaskDefinitionKey());
            restTaak.zaaktypeOmschrijving = zaaktypeOmschrijving;
            restTaak.taakinformatie = taskVariablesService.findTaakinformatie(taskInfo.getId());
            if (withTaakdata) {
                restTaak.taakdata = taskVariablesService.findTaakdata(taskInfo.getId());
                restTaak.taakdocumenten = taskVariablesService.findTaakdocumenten(taskInfo.getId());
            }
        }
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
