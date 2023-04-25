/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_TAAK_DOCUMENT;
import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.flowable.TaakVariabelenService.TAAK_DATA_DOCUMENTEN_VERZENDEN_POST;
import static net.atos.zac.flowable.TaakVariabelenService.TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER;
import static net.atos.zac.flowable.TaakVariabelenService.TAAK_DATA_TOELICHTING;
import static net.atos.zac.flowable.TaakVariabelenService.TAAK_DATA_VERZENDDATUM;
import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.informatieobjecten.EnkelvoudigInformatieObjectUpdateService;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.converter.RESTTaakHistorieConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.RESTTaakDocumentData;
import net.atos.zac.app.taken.model.RESTTaakHistorieRegel;
import net.atos.zac.app.taken.model.RESTTaakToekennenGegevens;
import net.atos.zac.app.taken.model.RESTTaakVerdelenGegevens;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.helper.OpschortenZaakHelper;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

/**
 *
 */
@Singleton
@Path("taken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TakenRESTService {

    private static final String REDEN_ZAAK_HERVATTEN = "Aanvullende informatie geleverd";

    private static final String REDEN_TAAK_AFGESLOTEN = "Afgesloten";

    @Inject
    private TakenService takenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private IndexeerService indexeerService;

    @Inject
    private RESTTaakConverter taakConverter;

    @Inject
    private EventingService eventingService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @Inject
    private RESTInformatieobjectConverter restInformatieobjectConverter;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private RESTTaakHistorieConverter taakHistorieConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private EnkelvoudigInformatieObjectUpdateService enkelvoudigInformatieObjectUpdateService;

    @Inject
    private OpschortenZaakHelper opschortenZaakHelper;

    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> listTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getLezen());
        return taakConverter.convert(takenService.listTasksForZaak(zaakUUID));
    }

    @GET
    @Path("{taskId}")
    public RESTTaak readTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = takenService.readTask(taskId);
        assertPolicy(policyService.readTaakRechten(task).getLezen());
        deleteSignaleringen(task);
        return taakConverter.convert(task);
    }

    @PUT
    @Path("taakdata")
    public RESTTaak updateTaakdata(final RESTTaak restTaak) {
        final Task task = takenService.readOpenTask(restTaak.id);
        assertPolicy(takenService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getWijzigen());
        taakVariabelenService.setTaakdata(task, restTaak.taakdata);
        taakVariabelenService.setTaakinformatie(task, restTaak.taakinformatie);
        updateTaak(restTaak);
        return restTaak;
    }

    @PUT
    @Path("lijst/verdelen")
    public void verdelenVanuitLijst(final RESTTaakVerdelenGegevens restTaakVerdelenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten()
                .getZakenTakenVerdelen());
        final List<String> taakIds = new ArrayList<>();
        restTaakVerdelenGegevens.taken.forEach(taak -> {
            Task task = takenService.readOpenTask(taak.taakId);
            boolean changed = false;
            if (restTaakVerdelenGegevens.behandelaarGebruikersnaam != null) {
                task = assignTaak(task.getId(), restTaakVerdelenGegevens.behandelaarGebruikersnaam,
                                  restTaakVerdelenGegevens.reden);
                changed = true;
            }

            if (restTaakVerdelenGegevens.groepId != null) {
                task = takenService.assignTaskToGroup(task, restTaakVerdelenGegevens.groepId,
                                                      restTaakVerdelenGegevens.reden);
                changed = true;
            }

            if (changed) {
                taakBehandelaarGewijzigd(task, taak.zaakUuid);
                taakIds.add(taak.taakId);
            }

        });
        indexeerService.indexeerDirect(taakIds, ZoekObjectType.TAAK);
    }

    @PUT
    @Path("lijst/vrijgeven")
    public void vrijgevenVanuitLijst(final RESTTaakVerdelenGegevens restTaakVerdelenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten()
                .getZakenTakenVerdelen());
        final List<String> taakIds = new ArrayList<>();
        restTaakVerdelenGegevens.taken.forEach(taak -> {
            final var task = assignTaak(taak.taakId, null, restTaakVerdelenGegevens.reden);
            taakBehandelaarGewijzigd(task, taak.zaakUuid);
            taakIds.add(task.getId());
        });
        indexeerService.indexeerDirect(taakIds, ZoekObjectType.TAAK);
    }

    @PATCH
    @Path("lijst/toekennen/mij")
    public RESTTaak toekennenAanIngelogdeMedewerkerVanuitLijst(
            final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken());
        final Task task = ingelogdeMedewerkerToekennenAanTaak(restTaakToekennenGegevens);
        return taakConverter.convert(task);
    }

    @PATCH
    @Path("toekennen")
    public void toekennen(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        Task task = takenService.readOpenTask(restTaakToekennenGegevens.taakId);
        assertPolicy(
                takenService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getToekennen());
        final String behandelaar = task.getAssignee();
        final String groep = taakConverter.extractGroupId(task.getIdentityLinks());
        boolean changed = false;
        if (!StringUtils.equals(behandelaar, restTaakToekennenGegevens.behandelaarId)) {
            task = assignTaak(task.getId(), restTaakToekennenGegevens.behandelaarId, restTaakToekennenGegevens.reden);
            changed = true;
        }

        if (!StringUtils.equals(groep, restTaakToekennenGegevens.groepId)) {
            task = takenService.assignTaskToGroup(task, restTaakToekennenGegevens.groepId,
                                                  restTaakToekennenGegevens.reden);
            changed = true;
        }
        if (changed) {
            taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);
        }
    }

    @PATCH
    @Path("toekennen/mij")
    public RESTTaak toekennenAanIngelogdeMedewerker(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        final Task task = ingelogdeMedewerkerToekennenAanTaak(restTaakToekennenGegevens);
        return taakConverter.convert(task);
    }

    @PATCH
    public RESTTaak updateTaak(final RESTTaak restTaak) {
        Task task = takenService.readOpenTask(restTaak.id);
        assertPolicy(takenService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getWijzigen());
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.fataledatum));
        task = takenService.updateTask(task);
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUuid));
        return taakConverter.convert(task);
    }

    @PATCH
    @Path("complete")
    public RESTTaak completeTaak(final RESTTaak restTaak) {
        Task task = takenService.readOpenTask(restTaak.id);
        final Zaak zaak = zrcClientService.readZaak(restTaak.zaakUuid);
        assertPolicy(takenService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getWijzigen());
        final String loggedInUserId = loggedInUserInstance.get().getId();
        if (restTaak.behandelaar == null || !restTaak.behandelaar.id.equals(loggedInUserId)) {
            task = takenService.assignTaskToUser(task.getId(), loggedInUserId, REDEN_TAAK_AFGESLOTEN);
        }
        createDocuments(restTaak, zaak);
        if (taakVariabelenService.isZaakHervatten(restTaak.taakdata)) {
            opschortenZaakHelper.hervattenZaak(zaak, REDEN_ZAAK_HERVATTEN);
        }
        if (restTaak.taakdata.containsKey(TAAK_DATA_DOCUMENTEN_VERZENDEN_POST)) {
            updateVerzenddatumEnkelvoudigInformatieObjecten(
                    restTaak.taakdata.get(TAAK_DATA_DOCUMENTEN_VERZENDEN_POST),
                    restTaak.taakdata.get(TAAK_DATA_VERZENDDATUM),
                    restTaak.taakdata.get(TAAK_DATA_TOELICHTING));
        }
        ondertekenEnkelvoudigInformatieObjecten(restTaak.taakdata, zaak);
        taakVariabelenService.setTaakdata(task, restTaak.taakdata);
        taakVariabelenService.setTaakinformatie(task, restTaak.taakinformatie);
        final HistoricTaskInstance completedTask = takenService.completeTask(task);
        indexeerService.addOrUpdateZaak(restTaak.zaakUuid, false);
        eventingService.send(TAAK.updated(completedTask));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUuid));
        return taakConverter.convert(completedTask);
    }

    @POST
    @Path("upload/{uuid}/{field}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("field") final String field, @PathParam("uuid") final UUID uuid,
            @MultipartForm final RESTFileUpload data) {
        String fileKey = String.format("_FILE__%s__%s", uuid, field);
        httpSession.get().setAttribute(fileKey, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("{taskId}/historie")
    public List<RESTTaakHistorieRegel> listHistorie(@PathParam("taskId") final String taskId) {
        assertPolicy(policyService.readTaakRechten(takenService.readTask(taskId)).getLezen());
        final List<HistoricTaskLogEntry> historicTaskLogEntries = takenService.listHistorieForTask(taskId);
        return taakHistorieConverter.convert(historicTaskLogEntries);
    }

    private Task ingelogdeMedewerkerToekennenAanTaak(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        Task task = takenService.readOpenTask(restTaakToekennenGegevens.taakId);
        assertPolicy(
                takenService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getToekennen());
        task = assignTaak(task.getId(), loggedInUserInstance.get().getId(), restTaakToekennenGegevens.reden);
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);
        indexeerService.indexeerDirect(restTaakToekennenGegevens.taakId, ZoekObjectType.TAAK);
        return task;
    }

    private Task assignTaak(final String taskId, final String assignee, final String reden) {
        final Task task = takenService.assignTaskToUser(taskId, assignee, reden);
        eventingService.send(
                SignaleringEventUtil.event(SignaleringType.Type.TAAK_OP_NAAM, task, loggedInUserInstance.get()));
        return task;
    }

    private void createDocuments(final RESTTaak restTaak, final Zaak zaak) {
        final HttpSession httpSession = this.httpSession.get();
        for (String key : restTaak.taakdata.keySet()) {
            final String fileKey = String.format("_FILE__%s__%s", restTaak.id, key);
            final RESTFileUpload uploadedFile = (RESTFileUpload) httpSession.getAttribute(fileKey);
            if (uploadedFile != null) {
                final String jsonDocumentData = restTaak.taakdata.get(key);
                if (StringUtils.isEmpty(jsonDocumentData)) { //document uploaded but removed afterwards
                    httpSession.removeAttribute(fileKey);
                    break;
                }
                final RESTTaakDocumentData restTaakDocumentData;
                try {
                    restTaakDocumentData = new ObjectMapper().readValue(jsonDocumentData, RESTTaakDocumentData.class);
                } catch (final JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage(), e); //invalid form-group data
                }
                final EnkelvoudigInformatieobjectWithInhoud document = restInformatieobjectConverter.convert(
                        restTaakDocumentData, uploadedFile);
                final ZaakInformatieobject zaakInformatieobject =
                        zgwApiService.createZaakInformatieobjectForZaak(zaak, document, document.getTitel(),
                                                                        OMSCHRIJVING_TAAK_DOCUMENT,
                                                                        OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
                restTaak.taakdata.replace(key,
                                          UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject()).toString());
                httpSession.removeAttribute(fileKey);
            }
        }
    }

    private void ondertekenEnkelvoudigInformatieObjecten(final Map<String, String> taakdata, final Zaak zaak) {
        final Optional<String> ondertekenen = taakVariabelenService.readOndertekeningen(taakdata);
        ondertekenen.ifPresent(s -> Arrays.stream(s.split(TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER))
                .filter(StringUtils::isNotEmpty)
                .map(UUID::fromString)
                .map(drcClientService::readEnkelvoudigInformatieobject)
                .forEach(enkelvoudigInformatieobject -> {
                    assertPolicy(enkelvoudigInformatieobject.getOndertekening() == null &&
                                         policyService.readDocumentRechten(enkelvoudigInformatieobject, zaak)
                                                 .getOndertekenen());
                    enkelvoudigInformatieObjectUpdateService.ondertekenEnkelvoudigInformatieObject(
                            enkelvoudigInformatieobject.getUUID());
                }));
    }

    private void deleteSignaleringen(final TaskInfo taskInfo) {
        final LoggedInUser loggedInUser = loggedInUserInstance.get();
        signaleringenService.deleteSignaleringen(new SignaleringZoekParameters(loggedInUser)
                                                         .types(SignaleringType.Type.TAAK_OP_NAAM).subject(taskInfo));
        signaleringenService.deleteSignaleringen(new SignaleringZoekParameters(loggedInUser)
                                                         .types(SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD)
                                                         .subjectZaak(taakVariabelenService.readZaakUUID(taskInfo)));
    }

    private void taakBehandelaarGewijzigd(final Task task, final UUID zaakUuid) {
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(zaakUuid));
    }

    private void updateVerzenddatumEnkelvoudigInformatieObjecten(final String documenten,
            final String verzenddatumString,
            final String toelichting) {
        final LocalDate verzenddatum = ZonedDateTime.parse(verzenddatumString).toLocalDate();
        Arrays.stream(documenten.split(TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER))
                .forEach(documentUUID -> setVerzenddatumEnkelvoudigInformatieObject(UUID.fromString(documentUUID),
                                                                                    verzenddatum, toelichting));
    }

    private void setVerzenddatumEnkelvoudigInformatieObject(final UUID uuid, final LocalDate verzenddatum,
            final String toelichting) {
        final var informatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        enkelvoudigInformatieObjectUpdateService.verzendEnkelvoudigInformatieObject(informatieobject.getUUID(),
                                                                                    verzenddatum, toelichting);
    }
}
