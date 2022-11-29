/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.flowable.TaskVariablesService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.TaakRechten;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.ReferentieTabelWaarde;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

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
    private RESTRechtenConverter rechtenConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;


    public List<RESTTaak> convert(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(task -> convert(task, false))
                .toList();
    }

    public RESTTaak convert(final TaskInfo taskInfo, boolean withTaakdata) {
        final UUID zaaktypeUuid = caseVariablesService.readZaaktypeUUID(taskInfo.getScopeId());
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(zaaktypeUuid);
        final String zaaktypeOmschrijving = zaakafhandelParameters.getZaaktypeOmschrijving();
        final TaakRechten rechten = policyService.readTaakRechten(zaaktypeOmschrijving);
        final RESTTaak restTaak = new RESTTaak();
        restTaak.id = taskInfo.getId();
        restTaak.rechten = rechtenConverter.convert(rechten);
        restTaak.naam = taskInfo.getName();
        restTaak.zaakUuid = caseVariablesService.readZaakUUID(taskInfo.getScopeId());
        restTaak.zaakIdentificatie = caseVariablesService.readZaakIdentificatie(taskInfo.getScopeId());
        restTaak.status = taskService.getTaakStatus(taskInfo);

        if (rechten.getLezen()) {

            final HumanTaskParameters humanTaskParameters = zaakafhandelParameters.getHumanTaskParametersCollection().stream()
                    .filter(zap -> taskInfo.getTaskDefinitionKey().equals(zap.getPlanItemDefinitionID())).findAny().orElseThrow(IllegalStateException::new);

            restTaak.toelichting = taskInfo.getDescription();
            restTaak.creatiedatumTijd = convertToZonedDateTime(taskInfo.getCreateTime());
            restTaak.toekenningsdatumTijd = convertToZonedDateTime(taskInfo.getClaimTime());
            restTaak.streefdatum = convertToLocalDate(taskInfo.getDueDate());
            restTaak.behandelaar = medewerkerConverter.convertUserId(taskInfo.getAssignee());
            restTaak.groep = groepConverter.convertGroupId(extractGroupId(taskInfo.getIdentityLinks()));
            restTaak.formulierDefinitie = humanTaskParameters.getFormulierDefinitieID();
            humanTaskParameters.getReferentieTabellen().forEach(rt -> {
                restTaak.tabellen.put(rt.getVeld(),
                                      rt.getTabel().getWaarden().stream().map(ReferentieTabelWaarde::getNaam).toList());
            });

            restTaak.zaaktypeOmschrijving = zaaktypeOmschrijving;
            taskVariablesService.findTaakinformatie(taskInfo.getId())
                    .ifPresent(taakinformatie -> restTaak.taakinformatie = taakinformatie);

            if (withTaakdata) {
                taskVariablesService.findTaakdata(taskInfo.getId())
                        .ifPresent(taakdata -> restTaak.taakdata = taakdata);
                taskVariablesService.findTaakdocumenten(taskInfo.getId())
                        .ifPresent(taakdocumenten -> restTaak.taakdocumenten = taakdocumenten);
            }
        }
        return restTaak;
    }

    public String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
    }
}
