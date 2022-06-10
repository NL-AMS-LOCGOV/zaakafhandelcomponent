/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;

import static net.atos.zac.zoeken.model.FilterWaarde.NIET_LEEG;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.task.api.Task;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringSubjectField;
import net.atos.zac.signalering.model.SignaleringTarget;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringVerzendInfo;
import net.atos.zac.signalering.model.SignaleringVerzondenZoekParameters;
import net.atos.zac.util.UriUtil;
import net.atos.zac.util.event.JobId;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.DatumRange;
import net.atos.zac.zoeken.model.DatumVeld;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@ApplicationScoped
@Transactional
public class SignaleringenJob {

    private static final Logger LOG = Logger.getLogger(SignaleringenJob.class.getName());

    public static final String ZAAK_AFGEHANDELD_QUERY = "zaak_afgehandeld";

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private FlowableService flowableService;

    /**
     * This is the batchjob to send E-Mail warnings about cases that are approaching their einddatum gepland or their uiterlijke einddatum afdoening.
     */
    public void zaakSignaleringenVerzenden() {
        final SignaleringVerzendInfo info = new SignaleringVerzendInfo();
        LOG.info(String.format("%s: gestart...",
                               JobId.ZAAK_SIGNALERINGEN_JOB.getName()));
        ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI())
                .forEach(zaaktype -> {
                    final UUID zaaktypeUUID = UriUtil.uuidFromURI(zaaktype.getUrl());
                    final ZaakafhandelParameters parameters = zaakafhandelParameterBeheerService.readZaakafhandelParameters(zaaktypeUUID);
                    if (parameters.getEinddatumGeplandWaarschuwing() != null) {
                        info.dueVerzonden += zaakEinddatumGeplandVerzenden(zaaktype, parameters.getEinddatumGeplandWaarschuwing());
                        zaakEinddatumGeplandOnterechtVerzondenVerwijderen(zaaktype, parameters.getEinddatumGeplandWaarschuwing());
                    }
                    if (parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing() != null) {
                        info.fatalVerzonden += zaakUiterlijkeEinddatumAfdoeningVerzenden(zaaktype, parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                        zaakUiterlijkeEinddatumAfdoeningOnterechtVerzondenVerwijderen(zaaktype, parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                    }
                });
        LOG.info(String.format("%s: gestopt (%d streefdatum waarschuwingen, %d fatale datum waarschuwingen)",
                               JobId.ZAAK_SIGNALERINGEN_JOB.getName(),
                               info.dueVerzonden,
                               info.fatalVerzonden));
    }

    /**
     * Send the E-Mail warnings about the einddatum gepland
     *
     * @return the number of E-Mails sent
     */
    private int zaakEinddatumGeplandVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoekZaak(getZaakSignaleringTeVerzendenZoekParameters(DatumVeld.ZAAK_EINDDATUM_GEPLAND, zaaktype, venster))
                .getItems().stream()
                .map(zaak -> buildZaakSignalering(getZaakSignaleringTarget(zaak, SignaleringSubjectField.DUE), zaak))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendZaakSignalering(signalering, SignaleringSubjectField.DUE));
        return verzonden[0];
    }

    /**
     * Send the E-Mail warnings about the uiterlijke einddatum afdoening
     *
     * @return the number of E-Mails sent
     */
    private int zaakUiterlijkeEinddatumAfdoeningVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoekZaak(getZaakSignaleringTeVerzendenZoekParameters(DatumVeld.ZAAK_UITERLIJKE_EINDDATUM_AFDOENING, zaaktype, venster))
                .getItems().stream()
                .map(zaak -> buildZaakSignalering(getZaakSignaleringTarget(zaak, SignaleringSubjectField.FATAL), zaak))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendZaakSignalering(signalering, SignaleringSubjectField.FATAL));
        return verzonden[0];
    }

    private String getZaakSignaleringTarget(final ZaakZoekObject zaak, final SignaleringSubjectField field) {
        if (signaleringenService.readInstellingenUser(SignaleringType.Type.ZAAK_VERLOPEND, zaak.getBehandelaarGebruikersnaam()).isMail() &&
                signaleringenService.findSignaleringVerzonden(
                        getZaakSignaleringVerzondenParameters(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(), field)) == null) {
            return zaak.getBehandelaarGebruikersnaam();
        }
        return null;
    }

    private Signalering buildZaakSignalering(final String target, final ZaakZoekObject zoekObject) {
        if (target != null) {
            final Zaak zaak = new Zaak();
            zaak.setUuid(UUID.fromString(zoekObject.getUuid()));
            final Signalering signalering = signaleringenService.signaleringInstance(SignaleringType.Type.ZAAK_VERLOPEND);
            signalering.setTargetUser(target);
            signalering.setSubject(zaak);
            return signalering;
        }
        return null;
    }

    private int verzendZaakSignalering(final Signalering signalering, final SignaleringSubjectField field) {
        final String bericht = SignaleringType.Type.ZAAK_VERLOPEND.getBericht().split(SignaleringType.SEPARATOR)[field.ordinal()];
        signaleringenService.sendSignalering(signalering, bericht);
        signaleringenService.createSignaleringVerzonden(signalering, field);
        return 1;
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the einddatum gepland has changed)
     */
    private void zaakEinddatumGeplandOnterechtVerzondenVerwijderen(final Zaaktype zaaktype, final int venster) {
        zoekenService.zoekZaak(getZaakSignaleringLaterTeVerzendenZoekParameters(DatumVeld.ZAAK_EINDDATUM_GEPLAND, zaaktype, venster))
                .getItems().stream()
                .map(zaak -> getZaakSignaleringVerzondenParameters(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(),
                                                                   SignaleringSubjectField.DUE))
                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the uiterlijke einddatum afdoening has changed)
     */
    private void zaakUiterlijkeEinddatumAfdoeningOnterechtVerzondenVerwijderen(final Zaaktype zaaktype, final int venster) {
        zoekenService.zoekZaak(getZaakSignaleringLaterTeVerzendenZoekParameters(DatumVeld.ZAAK_UITERLIJKE_EINDDATUM_AFDOENING, zaaktype, venster))
                .getItems().stream()
                .map(zaak -> getZaakSignaleringVerzondenParameters(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(),
                                                                   SignaleringSubjectField.FATAL))

                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    private ZoekParameters getZaakSignaleringTeVerzendenZoekParameters(final DatumVeld veld, final Zaaktype zaaktype, final int venster) {
        final LocalDate now = LocalDate.now();
        final ZoekParameters parameters = getOpenZaakMetBehandelaarZoekParameters(zaaktype);
        parameters.addDatum(veld, new DatumRange(now, now.plusDays(venster)));
        return parameters;
    }

    private ZoekParameters getZaakSignaleringLaterTeVerzendenZoekParameters(final DatumVeld veld, final Zaaktype zaaktype, final int venster) {
        final LocalDate now = LocalDate.now();
        final ZoekParameters parameters = getOpenZaakMetBehandelaarZoekParameters(zaaktype);
        parameters.addDatum(veld, new DatumRange(now.plusDays(venster + 1), null));
        return parameters;
    }

    private ZoekParameters getOpenZaakMetBehandelaarZoekParameters(final Zaaktype zaaktype) {
        final ZoekParameters parameters = new ZoekParameters(ZoekObjectType.ZAAK);
        parameters.addFilter(FilterVeld.ZAAK_ZAAKTYPE_UUID, UriUtil.uuidFromURI(zaaktype.getUrl()).toString());
        parameters.addFilter(FilterVeld.ZAAK_BEHANDELAAR, NIET_LEEG.toString());
        parameters.addFilterQuery(ZAAK_AFGEHANDELD_QUERY, "false");
        parameters.setRows(Integer.MAX_VALUE);
        return parameters;
    }

    private SignaleringVerzondenZoekParameters getZaakSignaleringVerzondenParameters(final String target, final String zaakUUID,
            final SignaleringSubjectField field) {
        return new SignaleringVerzondenZoekParameters(SignaleringTarget.USER, target)
                .types(SignaleringType.Type.ZAAK_VERLOPEND)
                .subjectZaak(UUID.fromString(zaakUUID))
                .subjectfield(field);
    }

    /**
     * This is the batchjob to send E-Mail warnings about tasks that are at or past their due date.
     */
    public void taakSignaleringenVerzenden() {
        final SignaleringVerzendInfo info = new SignaleringVerzendInfo();
        LOG.info(String.format("%s: gestart...",
                               JobId.TAAK_SIGNALERINGEN_JOB.getName()));
        info.dueVerzonden += taakDueVerzenden();
        taakDueOnterechtVerzondenVerwijderen();
        LOG.info(String.format("%s: gestopt (%d streefdatum waarschuwingen)",
                               JobId.TAAK_SIGNALERINGEN_JOB.getName(),
                               info.dueVerzonden));
    }

    /**
     * Send the E-Mail warnings about the due date
     *
     * @return the number of E-Mails sent
     */
    private int taakDueVerzenden() {
        final int[] verzonden = new int[1];
        flowableService.listOpenTasksDueNow().stream()
                .map(taak -> buildTaakSignalering(getTaakSignaleringTarget(taak), taak))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendTaakSignalering(signalering));
        return verzonden[0];
    }

    private String getTaakSignaleringTarget(final Task taak) {
        if (signaleringenService.readInstellingenUser(SignaleringType.Type.TAAK_VERLOPEN, taak.getAssignee()).isMail() &&
                signaleringenService.findSignaleringVerzonden(
                        getTaakSignaleringVerzondenParameters(taak.getAssignee(), taak.getId())) == null) {
            return taak.getAssignee();
        }
        return null;
    }

    private Signalering buildTaakSignalering(final String target, final Task taak) {
        if (target != null) {
            final Signalering signalering = signaleringenService.signaleringInstance(SignaleringType.Type.TAAK_VERLOPEN);
            signalering.setTargetUser(target);
            signalering.setSubject(taak);
            return signalering;
        }
        return null;
    }

    private int verzendTaakSignalering(final Signalering signalering) {
        signaleringenService.sendSignalering(signalering);
        signaleringenService.createSignaleringVerzonden(signalering, SignaleringSubjectField.DUE);
        return 1;
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the due date has changed)
     */
    private void taakDueOnterechtVerzondenVerwijderen() {
        flowableService.listOpenTasksDueLater().stream()
                .map(taak -> getTaakSignaleringVerzondenParameters(taak.getAssignee(), taak.getId()))
                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    private SignaleringVerzondenZoekParameters getTaakSignaleringVerzondenParameters(final String target, final String taakId) {
        return new SignaleringVerzondenZoekParameters(SignaleringTarget.USER, target)
                .types(SignaleringType.Type.TAAK_VERLOPEN)
                .subjectTaak(taakId)
                .subjectfield(SignaleringSubjectField.DUE);
    }
}
