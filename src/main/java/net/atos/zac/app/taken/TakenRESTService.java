/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.websocket.event.SchermObjectTypeEnum.TAAK;
import static net.atos.zac.websocket.event.SchermObjectTypeEnum.ZAAK_TAKEN;

import java.util.List;
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
import net.atos.zac.flowable.CmmnService;

/**
 *
 */
@Path("taken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TakenRESTService {

    @Inject
    private CmmnService cmmnService;

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
        final List<Task> tasks = cmmnService
                .listTasksForGroups(ingelogdeMedewerker.getGroupIds(), TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                    tableState.getSort().getDirection(), tableState.getPagination().getFirstResult(),
                                    tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, cmmnService.countTasksForGroups(ingelogdeMedewerker.getGroupIds()));
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTTaak> getMijnTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = cmmnService.listTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam(),
                                                                        TaakSortering.fromValue(tableState.getSort().getPredicate()),
                                                                        tableState.getSort().getDirection(),
                                                                        tableState.getPagination().getFirstResult(),
                                                                        tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, cmmnService.countTasksOwnedByMedewerker(ingelogdeMedewerker.getGebruikersnaam()));
    }


    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> getTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        final List<TaskInfo> tasks = cmmnService.listTaskInfosForZaak(zaakUUID);
        return taakConverter.convertTasks(tasks);
    }

    @GET
    @Path("{taskId}")
    public RESTTaak getTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = cmmnService.findTaskInfo(taskId);
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("toekennen")
    public RESTTaak toekennenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = cmmnService.assignTask(restTaak.id, restTaak.behandelaar != null ? restTaak.behandelaar.gebruikersnaam : null,
                                                 restTaak.groep != null ? restTaak.groep.id : null);
        taakBehandelaarGewijzigd(task, restTaak.zaakUUID);

        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("toekennen/mij")
    public RESTTaak toekennenAanIngelogdeGebruiker(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final Task task = cmmnService.assignTask(restTaakToekennenGegevens.taakId,
                                                 ingelogdeMedewerker.getGebruikersnaam(),
                                                 null);
        taakBehandelaarGewijzigd(task, restTaakToekennenGegevens.zaakUuid);

        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("bewerken")
    public RESTTaak bewerkenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        Task task = cmmnService.findTask(restTaak.id);
        taakConverter.convertTaak(restTaak, task);
        task = cmmnService.updateTask(task);
        eventingService.send(TAAK.wijziging(task));
        eventingService.send(ZAAK_TAKEN.wijziging(restTaak.zaakUUID));
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("afronden")
    public RESTTaak afrondenTaak(final RESTTaak restTaak) {

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        final TaskInfo taskInfo = cmmnService.completeTask(restTaak.id);
        eventingService.send(TAAK.wijziging(taskInfo));
        eventingService.send(ZAAK_TAKEN.wijziging(restTaak.zaakUUID));
        return taakConverter.convertTask(taskInfo);
    }

    private void taakBehandelaarGewijzigd(final Task taak, final UUID zaakUuid) {
        eventingService.send(TAAK.wijziging(taak));
        eventingService.send(ZAAK_TAKEN.wijziging(zaakUuid));
    }
}
