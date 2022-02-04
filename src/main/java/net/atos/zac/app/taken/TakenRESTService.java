/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.util.ConfigurationService.OMSCHRIJVING_TAAK_DOCUMENT;
import static net.atos.zac.util.ConfigurationService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.RESTTaakDocumentData;
import net.atos.zac.app.taken.model.RESTTaakToekennenGegevens;
import net.atos.zac.app.taken.model.RESTTaakVerdelenGegevens;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

/**
 *
 */
@Singleton
@Path("taken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TakenRESTService {

    @Inject
    private FlowableService flowableService;

    @Inject
    private RESTTaakConverter taakConverter;

    @Inject
    private EventingService eventingService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @Inject
    private RESTInformatieobjectConverter restInformatieobjectConverter;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @GET
    @Path("werkvoorraad")
    public TableResponse<RESTTaak> listWerkvoorraadTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService.listOpenTasksForGroups(ingelogdeMedewerker.get().getGroupIds(),
                                                                        TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                        tableState.getSort().getDirection(), tableState.getPagination().getFirstResult(),
                                                                        tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTaskInfoList(tasks);
        return new TableResponse<>(taken, flowableService.countOpenTasksForGroups(ingelogdeMedewerker.get().getGroupIds()));
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTTaak> listMijnTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService.listOpenTasksOwnedByMedewerker(ingelogdeMedewerker.get().getGebruikersnaam(),
                                                                                TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                                tableState.getSort().getDirection(),
                                                                                tableState.getPagination().getFirstResult(),
                                                                                tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTaskInfoList(tasks);
        return new TableResponse<>(taken, flowableService.countOpenTasksOwnedByMedewerker(ingelogdeMedewerker.get().getGebruikersnaam()));
    }


    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> listTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        final List<TaskInfo> tasks = flowableService.listAllTasksForZaak(zaakUUID);
        return taakConverter.convertTaskInfoList(tasks);
    }

    @GET
    @Path("{taskId}")
    public RESTTaak readTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = flowableService.readTaskInfo(taskId);
        final FormulierDefinitie formulierDefinitie = zaakafhandelParameterService.findFormulierDefinitie(task);
        final Map<String, String> taakdata = flowableService.readTaakdata(taskId);
        return taakConverter.convertTaskInfo(task, formulierDefinitie, taakdata);
    }

    @PUT
    @Path("taakdata")
    public RESTTaak updateTaakdata(final RESTTaak restTaak) {
        flowableService.updateTaakdata(restTaak.id, restTaak.taakdata);
        return restTaak;
    }

    @PUT
    @Path("verdelen")
    public void allocateTaak(final RESTTaakVerdelenGegevens restTaakVerdelenGegevens) {
        restTaakVerdelenGegevens.taakGegevens.forEach(task -> {
            final TaskInfo taskInfo = flowableService.assignTask(task.taakId,
                                                                 restTaakVerdelenGegevens.behandelaarGebruikersnaam);
            taakBehandelaarGewijzigd(taskInfo, task.zaakUuid);
        });
    }

    @PUT
    @Path("vrijgeven")
    public void releaseTaak(final RESTTaakVerdelenGegevens restTaakVerdelenGegevens) {
        restTaakVerdelenGegevens.taakGegevens.forEach(task -> {
            final TaskInfo taskInfo = flowableService.assignTask(task.taakId, null);
            taakBehandelaarGewijzigd(taskInfo, task.zaakUuid);
        });
    }

    @PATCH
    @Path("assign")
    public void assignTaak(final RESTTaak restTaak) {
        final Task task = flowableService.assignTask(restTaak.id,
                                                     restTaak.behandelaar != null ? restTaak.behandelaar.gebruikersnaam : null);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);
    }

    @PATCH
    @Path("assignTologgedOnUser")
    public RESTTaak assignToLoggedOnUser(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        final Task task = flowableService.assignTask(restTaakToekennenGegevens.taakId, ingelogdeMedewerker.get().getGebruikersnaam());
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);
        return taakConverter.convertTaskInfo(task);
    }

    @PATCH
    @Path("assign/group")
    public void assignGroup(final RESTTaak restTaak) {
        final Task task = flowableService.assignTaskToGroup(restTaak.id, restTaak.groep.id);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);
    }

    @PATCH
    @Path("")
    public RESTTaak partialUpdateTaak(final RESTTaak restTaak) {
        Task task = flowableService.readTask(restTaak.id);
        taakConverter.convertRESTTaak(restTaak, task);
        final Task updatedTask = flowableService.updateTask(task);
        eventingService.send(TAAK.updated(updatedTask));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUUID));
        return taakConverter.convertTaskInfo(updatedTask);
    }

    @PATCH
    @Path("complete")
    public RESTTaak completeTaak(final RESTTaak restTaak) {
        if (restTaak.behandelaar == null || !restTaak.behandelaar.gebruikersnaam.equals(
                ingelogdeMedewerker.get().getGebruikersnaam())) {
            flowableService.assignTask(restTaak.id, ingelogdeMedewerker.get().getGebruikersnaam());
        }

        createDocuments(restTaak);
        final Map<String, String> taakdata = flowableService.updateTaakdata(restTaak.id, restTaak.taakdata);
        final HistoricTaskInstance task = flowableService.completeTask(restTaak.id);
        eventingService.send(TAAK.updated(task));
        eventingService.send(ZAAK_TAKEN.updated(restTaak.zaakUUID));
        return taakConverter.convertTaskInfo(task, taakdata);
    }

    private void taakBehandelaarGewijzigd(final TaskInfo taskInfo, final UUID zaakUuid) {
        eventingService.send(TAAK.updated(taskInfo));
        eventingService.send(ZAAK_TAKEN.updated(zaakUuid));
    }

    @POST
    @Path("upload/{uuid}/{field}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("field") final String field, @PathParam("uuid") final UUID uuid, @MultipartForm final RESTFileUpload data) {
        String fileKey = String.format("_FILE__%s__%s", uuid, field);
        httpSession.get().setAttribute(fileKey, data);
        return Response.ok("\"Success\"").build();
    }

    private void createDocuments(final RESTTaak restTaak) {
        final Zaak zaak = zrcClientService.readZaak(restTaak.zaakUUID);
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
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage(), e); //invalid form-group data
                }
                final EnkelvoudigInformatieobjectWithInhoud document = restInformatieobjectConverter.convert(restTaakDocumentData, uploadedFile);
                final ZaakInformatieobject zaakInformatieobject =
                        zgwApiService.createZaakInformatieobjectForZaak(zaak, document, document.getTitel(),
                                                                        OMSCHRIJVING_TAAK_DOCUMENT, OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
                restTaak.taakdata.replace(key, UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject()).toString());
                httpSession.removeAttribute(fileKey);
            }
        }
    }
}
