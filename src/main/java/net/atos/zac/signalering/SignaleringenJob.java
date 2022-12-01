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
import net.atos.zac.flowable.TaskService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringDetail;
import net.atos.zac.signalering.model.SignaleringTarget;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringVerzendInfo;
import net.atos.zac.signalering.model.SignaleringVerzondenZoekParameters;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.DatumRange;
import net.atos.zac.zoeken.model.DatumVeld;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.index.ZoekObjectType;
import net.atos.zac.zoeken.model.zoekobject.ZaakZoekObject;

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
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private TaskService taskService;

    public void signaleringenVerzenden() {
        zaakSignaleringenVerzenden();
        taakSignaleringenVerzenden();
    }

    /**
     * This is the batchjob to send E-Mail warnings about cases that are approaching their einddatum gepland or their uiterlijke einddatum afdoening.
     */
    private void zaakSignaleringenVerzenden() {
        final SignaleringVerzendInfo info = new SignaleringVerzendInfo();
        LOG.info("Zaak signaleringen verzenden: gestart...");
        ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI())
                .forEach(zaaktype -> {
                    final ZaakafhandelParameters parameters = zaakafhandelParameterService.readZaakafhandelParameters(
                            zaaktype.getUUID());
                    if (parameters.getEinddatumGeplandWaarschuwing() != null) {
                        info.streefdatumVerzonden += zaakEinddatumGeplandVerzenden(zaaktype,
                                                                                   parameters.getEinddatumGeplandWaarschuwing());
                        zaakEinddatumGeplandOnterechtVerzondenVerwijderen(zaaktype,
                                                                          parameters.getEinddatumGeplandWaarschuwing());
                    }
                    if (parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing() != null) {
                        info.fataledatumVerzonden += zaakUiterlijkeEinddatumAfdoeningVerzenden(zaaktype,
                                                                                               parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                        zaakUiterlijkeEinddatumAfdoeningOnterechtVerzondenVerwijderen(zaaktype,
                                                                                      parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
                    }
                });
        LOG.info(String.format(
                "Zaak signaleringen verzenden: gestopt (%d streefdatum waarschuwingen, %d fatale datum waarschuwingen)",
                info.streefdatumVerzonden,
                info.fataledatumVerzonden));
    }

    /**
     * Send the E-Mail warnings about the einddatum gepland
     *
     * @return the number of E-Mails sent
     */
    private int zaakEinddatumGeplandVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoek(getZaakSignaleringTeVerzendenZoekParameters(DatumVeld.ZAAK_STREEFDATUM, zaaktype, venster))
                .getItems().stream()
                .map(zaakZoekObject -> (ZaakZoekObject) zaakZoekObject)
                .map(zaakZoekObject -> buildZaakSignalering(
                        getZaakSignaleringTarget(zaakZoekObject, SignaleringDetail.STREEFDATUM),
                        zaakZoekObject,
                        SignaleringDetail.STREEFDATUM))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendZaakSignalering(signalering));
        return verzonden[0];
    }

    /**
     * Send the E-Mail warnings about the uiterlijke einddatum afdoening
     *
     * @return the number of E-Mails sent
     */
    private int zaakUiterlijkeEinddatumAfdoeningVerzenden(final Zaaktype zaaktype, final int venster) {
        final int[] verzonden = new int[1];
        zoekenService.zoek(getZaakSignaleringTeVerzendenZoekParameters(DatumVeld.ZAAK_FATALE_DATUM, zaaktype, venster))
                .getItems().stream()
                .map(zaakZoekObject -> (ZaakZoekObject) zaakZoekObject)
                .map(zaakZoekObject -> buildZaakSignalering(
                        getZaakSignaleringTarget(zaakZoekObject, SignaleringDetail.FATALE_DATUM),
                        zaakZoekObject,
                        SignaleringDetail.FATALE_DATUM))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendZaakSignalering(signalering));
        return verzonden[0];
    }

    private String getZaakSignaleringTarget(final ZaakZoekObject zaak, final SignaleringDetail detail) {
        if (signaleringenService.readInstellingenUser(SignaleringType.Type.ZAAK_VERLOPEND,
                                                      zaak.getBehandelaarGebruikersnaam()).isMail() &&
                !signaleringenService.findSignaleringVerzonden(
                        getZaakSignaleringVerzondenParameters(zaak.getBehandelaarGebruikersnaam(), zaak.getUuid(),
                                                              detail)).isPresent()) {
            return zaak.getBehandelaarGebruikersnaam();
        }
        return null;
    }

    private Signalering buildZaakSignalering(final String target, final ZaakZoekObject zaakZoekObject,
            final SignaleringDetail detail) {
        if (target != null) {
            final Zaak zaak = new Zaak();
            zaak.setUuid(UUID.fromString(zaakZoekObject.getUuid()));
            final Signalering signalering = signaleringenService.signaleringInstance(
                    SignaleringType.Type.ZAAK_VERLOPEND);
            signalering.setTargetUser(target);
            signalering.setSubject(zaak);
            signalering.setDetail(detail);
            return signalering;
        }
        return null;
    }

    private int verzendZaakSignalering(final Signalering signalering) {
        signaleringenService.sendSignalering(signalering);
        signaleringenService.createSignaleringVerzonden(signalering);
        return 1;
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the einddatum gepland has changed)
     */
    private void zaakEinddatumGeplandOnterechtVerzondenVerwijderen(final Zaaktype zaaktype, final int venster) {
        zoekenService.zoek(getZaakSignaleringLaterTeVerzendenZoekParameters(DatumVeld.STREEFDATUM, zaaktype, venster))
                .getItems().stream()
                .map(zaakZoekObject -> (ZaakZoekObject) zaakZoekObject)
                .map(zaakZoekObject -> getZaakSignaleringVerzondenParameters(
                        zaakZoekObject.getBehandelaarGebruikersnaam(),
                        zaakZoekObject.getUuid(),
                        SignaleringDetail.STREEFDATUM))
                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the uiterlijke einddatum afdoening has changed)
     */
    private void zaakUiterlijkeEinddatumAfdoeningOnterechtVerzondenVerwijderen(final Zaaktype zaaktype,
            final int venster) {
        zoekenService.zoek(
                        getZaakSignaleringLaterTeVerzendenZoekParameters(DatumVeld.ZAAK_FATALE_DATUM, zaaktype, venster))
                .getItems().stream()
                .map(zaakZoekObject -> (ZaakZoekObject) zaakZoekObject)
                .map(zaakZoekObject -> getZaakSignaleringVerzondenParameters(
                        zaakZoekObject.getBehandelaarGebruikersnaam(),
                        zaakZoekObject.getUuid(),
                        SignaleringDetail.FATALE_DATUM))
                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    private ZoekParameters getZaakSignaleringTeVerzendenZoekParameters(final DatumVeld veld, final Zaaktype zaaktype,
            final int venster) {
        final LocalDate now = LocalDate.now();
        final ZoekParameters parameters = getOpenZaakMetBehandelaarZoekParameters(zaaktype);
        parameters.addDatum(veld, new DatumRange(now, now.plusDays(venster)));
        return parameters;
    }

    private ZoekParameters getZaakSignaleringLaterTeVerzendenZoekParameters(final DatumVeld veld,
            final Zaaktype zaaktype, final int venster) {
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

    private SignaleringVerzondenZoekParameters getZaakSignaleringVerzondenParameters(final String target,
            final String zaakUUID,
            final SignaleringDetail detail) {
        return new SignaleringVerzondenZoekParameters(SignaleringTarget.USER, target)
                .types(SignaleringType.Type.ZAAK_VERLOPEND)
                .subjectZaak(UUID.fromString(zaakUUID))
                .detail(detail);
    }

    /**
     * This is the batchjob to send E-Mail warnings about tasks that are at or past their due date.
     */
    private void taakSignaleringenVerzenden() {
        final SignaleringVerzendInfo info = new SignaleringVerzendInfo();
        LOG.info("Taak signaleringen verzenden: gestart...");
        info.streefdatumVerzonden += taakDueVerzenden();
        taakDueOnterechtVerzondenVerwijderen();
        LOG.info(String.format("Taak signaleringen verzenden: gestopt (%d streefdatum waarschuwingen)",
                               info.streefdatumVerzonden));
    }

    /**
     * Send the E-Mail warnings about the due date
     *
     * @return the number of E-Mails sent
     */
    private int taakDueVerzenden() {
        final int[] verzonden = new int[1];
        taskService.listOpenTasksDueNow().stream()
                .map(taak -> buildTaakSignalering(getTaakSignaleringTarget(taak), taak))
                .filter(Objects::nonNull)
                .forEach(signalering -> verzonden[0] += verzendTaakSignalering(signalering));
        return verzonden[0];
    }

    private String getTaakSignaleringTarget(final Task taak) {
        if (signaleringenService.readInstellingenUser(SignaleringType.Type.TAAK_VERLOPEN, taak.getAssignee())
                .isMail() &&
                !signaleringenService.findSignaleringVerzonden(
                        getTaakSignaleringVerzondenParameters(taak.getAssignee(), taak.getId())).isPresent()) {
            return taak.getAssignee();
        }
        return null;
    }

    private Signalering buildTaakSignalering(final String target, final Task taak) {
        if (target != null) {
            final Signalering signalering = signaleringenService.signaleringInstance(
                    SignaleringType.Type.TAAK_VERLOPEN);
            signalering.setTargetUser(target);
            signalering.setSubject(taak);
            signalering.setDetail(SignaleringDetail.STREEFDATUM);
            return signalering;
        }
        return null;
    }

    private int verzendTaakSignalering(final Signalering signalering) {
        signaleringenService.sendSignalering(signalering);
        signaleringenService.createSignaleringVerzonden(signalering);
        return 1;
    }

    /**
     * Make sure already sent E-Mail warnings will get send again (in cases where the due date has changed)
     */
    private void taakDueOnterechtVerzondenVerwijderen() {
        taskService.listOpenTasksDueLater().stream()
                .map(taak -> getTaakSignaleringVerzondenParameters(taak.getAssignee(), taak.getId()))
                .forEach(signaleringenService::deleteSignaleringVerzonden);
    }

    private SignaleringVerzondenZoekParameters getTaakSignaleringVerzondenParameters(final String target,
            final String taakId) {
        return new SignaleringVerzondenZoekParameters(SignaleringTarget.USER, target)
                .types(SignaleringType.Type.TAAK_VERLOPEN)
                .subjectTaak(taakId)
                .detail(SignaleringDetail.STREEFDATUM);
    }
}
