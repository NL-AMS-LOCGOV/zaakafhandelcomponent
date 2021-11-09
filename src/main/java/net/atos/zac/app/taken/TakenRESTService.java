/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.websocket.event.ScreenObjectTypeEnum.TAAK;
import static net.atos.zac.websocket.event.ScreenObjectTypeEnum.ZAAK_TAKEN;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.RESTTaakToekennenGegevens;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.FlowableService;

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
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @GET
    @Path("werkvoorraad")
    public TableResponse<RESTTaak> getWerkvoorraadTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService
                .listTasksForGroups(ingelogdeMedewerker.getGroupIds(), TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                    tableState.getSort().getDirection(), tableState.getPagination().getFirstResult(),
                                    tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, flowableService.countTasksForGroups(ingelogdeMedewerker.getGroupIds()));
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTTaak> getMijnTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = flowableService.listTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam(),
                                                                            TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                            tableState.getSort().getDirection(),
                                                                            tableState.getPagination().getFirstResult(),
                                                                            tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, flowableService.countTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam()));
    }


    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> getTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        final List<TaskInfo> tasks = flowableService.listTaskInfosForZaak(zaakUUID);
        return taakConverter.convertTasks(tasks);
    }

    @GET
    @Path("{taskId}")
    public RESTTaak getTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = flowableService.readTaskInfo(taskId);
        final Map<String, String> taakdata = flowableService.readTaakdata(taskId);
        return taakConverter.convertTask(task, taakdata);
    }

    @PATCH
    @Path("toekennen")
    public RESTTaak toekennenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = flowableService.assignTask(restTaak.id, restTaak.behandelaar != null ? restTaak.behandelaar.gebruikersnaam : null,
                                                     restTaak.groep != null ? restTaak.groep.id : null);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);

        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("toekennen/mij")
    public RESTTaak toekennenAanIngelogdeGebruiker(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = flowableService.assignTask(restTaakToekennenGegevens.taakId,
                                                     ingelogdeMedewerker.getGebruikersnaam(),
                                                     null);
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);

        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("bewerken")
    public RESTTaak bewerkenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        Task task = flowableService.readTask(restTaak.id);
        taakConverter.convertTaak(restTaak, task);
        task = flowableService.updateTask(task);
        eventingService.send(TAAK.update(task));
        eventingService.send(ZAAK_TAKEN.update(restTaak.zaakUUID));
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("afronden")
    public RESTTaak afrondenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final TaskInfo taskInfo = flowableService.completeTask(restTaak.id);
        eventingService.send(TAAK.update(taskInfo));
        eventingService.send(ZAAK_TAKEN.update(restTaak.zaakUUID));
        return taakConverter.convertTask(taskInfo);
    }

    private void taakBehandelaarGewijzigd(final Task taak, final UUID zaakUuid) {
        eventingService.send(TAAK.update(taak));
        eventingService.send(ZAAK_TAKEN.update(zaakUuid));
    }
}
