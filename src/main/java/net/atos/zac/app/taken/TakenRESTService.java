/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_TAAK_DOCUMENT;
import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.configuratie.ConfiguratieService.TAAK_ELEMENT_ONDERTEKENEN;
import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import net.atos.zac.app.informatieobjecten.EnkelvoudigInformatieObjectOndertekenService;
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
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.flowable.TaskVariablesService;
import net.atos.zac.policy.PolicyService;
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

    @Inject
    private TaskService taskService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private TaskVariablesService taskVariablesService;

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
    private EnkelvoudigInformatieObjectOndertekenService enkelvoudigInformatieObjectOndertekenService;

    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> listTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getLezen());
        return taakConverter.convert(taskService.listTasksForCase(zaakUUID));
    }

    @GET
    @Path("{taskId}")
    public RESTTaak readTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = taskService.readTask(taskId);
        assertPolicy(policyService.readTaakRechten(task).getLezen());
        final RESTTaak restTaak = taakConverter.convert(task, true);
        deleteSignaleringen(task);
        return restTaak;
    }

    @PUT
    @Path("taakdata")
    public RESTTaak updateTaakdata(final RESTTaak restTaak) {
        final TaskInfo task = taskService.readTask(restTaak.id);
        assertPolicy(taskService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getWijzigen());
        taskVariablesService.setTaakdata(restTaak.id, restTaak.taakdata);
        taskVariablesService.setTaakinformatie(restTaak.id, restTaak.taakinformatie);
        return restTaak;
    }

    @PUT
    @Path("lijst/verdelen")
    public void verdelenVanuitLijst(final RESTTaakVerdelenGegevens restTaakVerdelenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten().getZakenTakenVerdelen());
        final List<String> taakIds = new ArrayList<>();
        restTaakVerdelenGegevens.taken.forEach(taak -> {
            Task task = taskService.readOpenTask(taak.taakId);
            boolean changed = false;
            if (restTaakVerdelenGegevens.behandelaarGebruikersnaam != null) {
                task = assignTaak(taak.taakId, restTaakVerdelenGegevens.behandelaarGebruikersnaam);
                changed = true;
            }

            if (restTaakVerdelenGegevens.groepId != null) {
                taskService.assignTaskToGroup(task, restTaakVerdelenGegevens.groepId);
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
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten().getZakenTakenVerdelen());
        final List<String> taakIds = new ArrayList<>();
        restTaakVerdelenGegevens.taken.forEach(taak -> {
            final Task task = assignTaak(taak.taakId, null);
            taakBehandelaarGewijzigd(task, taak.zaakUuid);
            taakIds.add(taak.taakId);
        });
        indexeerService.indexeerDirect(taakIds, ZoekObjectType.TAAK);
    }

    @PATCH
    @Path("lijst/toekennen/mij")
    public RESTTaak toekennenAanIngelogdeMedewerkerVanuitLijst(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken());

        final Task task = ingelogdeMedewerkerToekennenAanTaak(restTaakToekennenGegevens);
        return taakConverter.convert(task, true);
    }

    @PATCH
    @Path("toekennen")
    public void toekennen(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        final Task task = taskService.readOpenTask(restTaakToekennenGegevens.taakId);
        assertPolicy(taskService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getToekennen());


        final String behandelaar = task.getAssignee();
        final String groep = taakConverter.extractGroupId(task.getIdentityLinks());

        boolean changed = false;
        if (!StringUtils.equals(behandelaar, restTaakToekennenGegevens.behandelaarId)) {
            assignTaak(restTaakToekennenGegevens.taakId, restTaakToekennenGegevens.behandelaarId);
            changed = true;
        }

        if (!StringUtils.equals(groep, restTaakToekennenGegevens.groepId)) {
            taskService.assignTaskToGroup(task, restTaakToekennenGegevens.groepId);
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
        return taakConverter.convert(task, true);
    }

    @PATCH
    @Path("")
    public RESTTaak updateTaak(final RESTTaak restTaak) {
        Task task = taskService.readOpenTask(restTaak.id);
        assertPolicy(taskService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getWijzigen());
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.streefdatum));
        task = taskService.updateTask(task);
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUuid));
        return taakConverter.convert(task, true);
    }

    @PATCH
    @Path("complete")
    public RESTTaak completeTaak(final RESTTaak restTaak) {
        final TaskInfo taskinfo = taskService.readTask(restTaak.id);
        assertPolicy(taskService.getTaakStatus(taskinfo) != AFGEROND && policyService.readTaakRechten(taskinfo).getWijzigen());
        final String loggedInUserId = loggedInUserInstance.get().getId();
        if (restTaak.behandelaar == null || !restTaak.behandelaar.id.equals(loggedInUserId)) {
            taskService.assignTaskToUser(restTaak.id, loggedInUserId);
        }

        createDocuments(restTaak);
        final Zaak zaak = zrcClientService.readZaak(restTaak.zaakUuid);
        ondertekenEnkelvoudigInformatieObjecten(restTaak.taakdata, zaak);
        taskVariablesService.setTaakdata(restTaak.id, restTaak.taakdata);
        taskVariablesService.setTaakinformatie(restTaak.id, restTaak.taakinformatie);
        final HistoricTaskInstance task = taskService.completeTask(restTaak.id);
        indexeerService.addZaak(restTaak.zaakUuid, false);
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUuid));
        return taakConverter.convert(task, true);
    }

    @POST
    @Path("upload/{uuid}/{field}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("field") final String field, @PathParam("uuid") final UUID uuid, @MultipartForm final RESTFileUpload data) {
        String fileKey = String.format("_FILE__%s__%s", uuid, field);
        httpSession.get().setAttribute(fileKey, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("{taskId}/historie")
    public List<RESTTaakHistorieRegel> listHistorie(@PathParam("taskId") final String taskId) {
        assertPolicy(policyService.readTaakRechten(taskService.readTask(taskId)).getLezen());
        final List<HistoricTaskLogEntry> historicTaskLogEntries = taskService.listHistorieForTask(taskId);
        return taakHistorieConverter.convert(historicTaskLogEntries);
    }

    private Task ingelogdeMedewerkerToekennenAanTaak(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        Task task = taskService.readOpenTask(restTaakToekennenGegevens.taakId);
        assertPolicy(taskService.getTaakStatus(task) != AFGEROND && policyService.readTaakRechten(task).getToekennen());

        task = assignTaak(restTaakToekennenGegevens.taakId, loggedInUserInstance.get().getId());
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);
        indexeerService.indexeerDirect(restTaakToekennenGegevens.taakId, ZoekObjectType.TAAK);
        return task;
    }

    private Task assignTaak(final String taakId, final String assignee) {
        final Task task = taskService.assignTaskToUser(taakId, assignee);
        eventingService.send(SignaleringEventUtil.event(SignaleringType.Type.TAAK_OP_NAAM, task, loggedInUserInstance.get()));
        return task;
    }

    private void createDocuments(final RESTTaak restTaak) {
        final Zaak zaak = zrcClientService.readZaak(restTaak.zaakUuid);
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
                final EnkelvoudigInformatieobjectWithInhoud document = restInformatieobjectConverter.convert(restTaakDocumentData, uploadedFile);
                final ZaakInformatieobject zaakInformatieobject =
                        zgwApiService.createZaakInformatieobjectForZaak(zaak, document, document.getTitel(),
                                                                        OMSCHRIJVING_TAAK_DOCUMENT,
                                                                        OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
                restTaak.taakdata.replace(key, UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject()).toString());
                httpSession.removeAttribute(fileKey);
            }
        }
    }

    private void ondertekenEnkelvoudigInformatieObjecten(final Map<String, String> taakdata, final Zaak zaak) {
        final String ondertekenen = taakdata.get(TAAK_ELEMENT_ONDERTEKENEN);
        if (ondertekenen != null) {
            Arrays.stream(ondertekenen.split(";"))
                    .filter(StringUtils::isNotEmpty)
                    .map(UUID::fromString)
                    .map(drcClientService::readEnkelvoudigInformatieobject)
                    .forEach(enkelvoudigInformatieobject -> {
                        assertPolicy(enkelvoudigInformatieobject.getOndertekening() == null &&
                                             policyService.readDocumentRechten(enkelvoudigInformatieobject, zaak).getOndertekenen());
                        enkelvoudigInformatieObjectOndertekenService.ondertekenEnkelvoudigInformatieObject(enkelvoudigInformatieobject.getUUID());
                    });
        }
    }

    private void deleteSignaleringen(final TaskInfo taskInfo) {
        final LoggedInUser loggedInUser = loggedInUserInstance.get();
        signaleringenService.deleteSignaleringen(new SignaleringZoekParameters(loggedInUser).types(SignaleringType.Type.TAAK_OP_NAAM).subject(taskInfo));
        signaleringenService.deleteSignaleringen(new SignaleringZoekParameters(loggedInUser).types(SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD)
                                                         .subjectZaak(caseVariablesService.readZaakUUID(taskInfo.getScopeId())));
    }

    private void taakBehandelaarGewijzigd(final Task task, final UUID zaakUuid) {
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(zaakUuid));
    }
}
