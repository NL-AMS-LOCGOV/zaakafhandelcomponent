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

import net.atos.zac.app.formulieren.converter.RESTFormulierDefinitieConverter;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.formulieren.FormulierDefinitieService;
import net.atos.zac.formulieren.FormulierRuntimeService;
import net.atos.zac.formulieren.model.FormulierDefinitie;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

/**
 *
 */
public class RESTTaakConverter {

    @Inject
    private TakenService takenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private RESTGroupConverter groepConverter;

    @Inject
    private RESTUserConverter medewerkerConverter;

    @Inject
    private RESTRechtenConverter rechtenConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private RESTFormulierDefinitieConverter formulierDefinitieConverter;

    @Inject
    private FormulierDefinitieService formulierDefinitieService;

    @Inject
    private FormulierRuntimeService formulierRuntimeService;

    public List<RESTTaak> convert(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(this::convert)
                .toList();
    }

    public RESTTaak convert(final TaskInfo taskInfo) {
        final var restTaak = new RESTTaak();
        restTaak.id = taskInfo.getId();
        restTaak.naam = taskInfo.getName();
        restTaak.status = takenService.getTaakStatus(taskInfo);
        restTaak.zaakUuid = taakVariabelenService.readZaakUUID(taskInfo);
        restTaak.zaakIdentificatie = taakVariabelenService.readZaakIdentificatie(taskInfo);
        final var zaaktypeOmschrijving = taakVariabelenService.readZaaktypeOmschrijving(taskInfo);
        final var rechten = policyService.readTaakRechten(zaaktypeOmschrijving);
        restTaak.rechten = rechtenConverter.convert(rechten);
        if (rechten.getLezen()) {
            restTaak.zaaktypeOmschrijving = zaaktypeOmschrijving;
            restTaak.toelichting = taskInfo.getDescription();
            restTaak.creatiedatumTijd = convertToZonedDateTime(taskInfo.getCreateTime());
            restTaak.toekenningsdatumTijd = convertToZonedDateTime(taskInfo.getClaimTime());
            restTaak.fataledatum = convertToLocalDate(taskInfo.getDueDate());
            restTaak.behandelaar = medewerkerConverter.convertUserId(taskInfo.getAssignee());
            restTaak.groep = groepConverter.convertGroupId(extractGroupId(taskInfo.getIdentityLinks()));
            restTaak.taakinformatie = taakVariabelenService.readTaakinformatie(taskInfo);
            restTaak.taakdata = taakVariabelenService.readTaakdata(taskInfo);
            restTaak.taakdocumenten = taakVariabelenService.readTaakdocumenten(taskInfo);
            final String formulierSysteemnaam;

            if (takenService.isCmmnTask(taskInfo)) {
                formulierSysteemnaam = getFormulierDefinitie(taakVariabelenService.readZaaktypeUUID(taskInfo),
                                                             taskInfo.getTaskDefinitionKey());
            } else {
                formulierSysteemnaam = taskInfo.getFormKey();
            }
            if (formulierSysteemnaam != null) {
                final FormulierDefinitie formulierDefinitie = formulierDefinitieService.readFormulierDefinitie(
                        formulierSysteemnaam);
                formulierRuntimeService.resolveDefaultwaarden(formulierDefinitie);
                formulierRuntimeService.resolveDefaultwaarden(formulierDefinitie, restTaak.taakdata);
                formulierRuntimeService.resolveDefaultwaarden(formulierDefinitie, restTaak);
                restTaak.formulierDefinitie = formulierDefinitieConverter.convert(formulierDefinitie, true);
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

    private String getFormulierDefinitie(final UUID zaaktypeUUID, final String taskDefinitionKey) {
        return zaakafhandelParameterService.readZaakafhandelParameters(zaaktypeUUID)
                .getHumanTaskParametersCollection().stream()
                .filter(humanTaskParameters -> taskDefinitionKey.equals(
                        humanTaskParameters.getPlanItemDefinitionID()))
                .findAny()
                .map(HumanTaskParameters::getAfhandelformulierDefinitieID)
                .orElse(null);
    }
}
