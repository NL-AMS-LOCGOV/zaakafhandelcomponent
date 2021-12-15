/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.RESTTaakToekennenGegevens;
import net.atos.zac.app.taken.model.RESTTaakVerdelenGegevens;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.zaaksturing.ZaakSturingService;
import net.atos.zac.zaaksturing.model.TaakFormulieren;

/**
 *
 */
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
    private ZaakSturingService zaakSturingService;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @GET
    @Path("werkvoorraad")
    public TableResponse<RESTTaak> listWerkvoorraadTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService.listOpenTasksForGroups(ingelogdeMedewerker.getGroupIds(),
                                                                        TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                        tableState.getSort().getDirection(), tableState.getPagination().getFirstResult(),
                                                                        tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTaskInfoList(tasks);
        return new TableResponse<>(taken, flowableService.countOpenTasksForGroups(ingelogdeMedewerker.getGroupIds()));
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTTaak> listMijnTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService.listOpenTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam(),
                                                                                TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                                tableState.getSort().getDirection(),
                                                                                tableState.getPagination().getFirstResult(),
                                                                                tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTaskInfoList(tasks);
        return new TableResponse<>(taken, flowableService.countOpenTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam()));
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
        final String zaaktypeIdentificatie = flowableService.readZaaktypeIdentificatieForTask(taskId);
        final TaakFormulieren taakFormulieren = zaakSturingService.findTaakFormulieren(zaaktypeIdentificatie, task.getTaskDefinitionKey());
        final Map<String, String> taakdata = flowableService.readTaakdata(taskId);
        return taakConverter.convertTaskInfo(task, taakFormulieren.getBehandelFormulier(), taakdata);
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

    @PATCH
    @Path("assign")
    public RESTTaak assignTaak(final RESTTaak restTaak) {
        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = flowableService.assignTask(restTaak.id,
                                                     restTaak.behandelaar != null ? restTaak.behandelaar.gebruikersnaam : null);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);
        return taakConverter.convertTaskInfo(task);
    }

    @PATCH
    @Path("assignTologgedOnUser")
    public RESTTaak assignToLoggedOnUser(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = flowableService.assignTask(restTaakToekennenGegevens.taakId, ingelogdeMedewerker.getGebruikersnaam());
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);
        return taakConverter.convertTaskInfo(task);
    }

    @PATCH
    @Path("assign/group")
    public RESTTaak assignGroup(final RESTTaak restTaak) {
        final Task task = flowableService.assignTaskToGroup(restTaak.id, restTaak.groep.id);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);
        return taakConverter.convertTaskInfo(task);
    }

    @PATCH
    @Path("")
    public RESTTaak partialUpdateTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
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
}
