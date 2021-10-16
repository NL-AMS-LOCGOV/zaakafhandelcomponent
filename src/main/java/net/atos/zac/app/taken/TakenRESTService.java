/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken;

import static net.atos.zac.websocket.event.ObjectTypeEnum.TAAK;
import static net.atos.zac.websocket.event.ObjectTypeEnum.ZAAK_TAKEN;

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

import net.atos.zac.app.taken.converter.RESTTaakConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.RESTTaakToekennenGegevens;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.app.util.datatable.TableRequest;
import net.atos.zac.app.util.datatable.TableResponse;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.events.EventingServiceBean;
import net.atos.zac.flowable.cmmn.CmmnService;

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
    private EventingServiceBean eventingService;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @GET
    @Path("werkvoorraad")
    public TableResponse<RESTTaak> getWerkvoorraadTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = cmmnService
                .getWerkvoorraadTaken(TaakSortering.fromValue(tableState.getSort().getPredicate()), tableState.getSort().getDirection(),
                                      tableState.getPagination().getFirstResult(), tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, cmmnService.getWerkvoorraadTakenAantal());
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTTaak> getMijnTaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final List<Task> tasks = cmmnService
                .getMijnTaken(TaakSortering.fromValue(tableState.getSort().getPredicate()), tableState.getSort().getDirection(),
                              tableState.getPagination().getFirstResult(), tableState.getPagination().getPageSize());
        final List<RESTTaak> taken = taakConverter.convertTasks(tasks);
        return new TableResponse<>(taken, cmmnService.getMijnTakenAantal());
    }


    @GET
    @Path("zaak/{zaakUUID}")
    public List<RESTTaak> getTakenVoorZaak(@PathParam("zaakUUID") final UUID zaakUUID) {
        final List<TaskInfo> tasks = cmmnService.getTakenVoorZaak(zaakUUID);
        return taakConverter.convertTasks(tasks);
    }

    @GET
    @Path("{taskId}")
    public RESTTaak getTaak(@PathParam("taskId") final String taskId) {
        final TaskInfo task = cmmnService.getTaak(taskId);
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("toekennen")
    public RESTTaak toekennenTaak(final RESTTaak restTaak) {
        final Task task = cmmnService.assignTask(restTaak.id, restTaak.behandelaar != null ? restTaak.behandelaar.gebruikersnaam : null,
                                                 restTaak.groep != null ? restTaak.groep.id : null);
        versturenTaakWijzigingen(task, restTaak.zaakUUID);
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("toekennen/mij")
    public RESTTaak toekennenAanIngelogdeGebruiker(final RESTTaakToekennenGegevens restTaakToekennenGegevens) {
        final Task task = cmmnService.assignTask(restTaakToekennenGegevens.taakId,
                                                 ingelogdeMedewerker.getGebruikersnaam(),
                                                 null);
        versturenTaakWijzigingen(task, restTaakToekennenGegevens.zaakUuid);
        return taakConverter.convertTask(task);
    }

    private void versturenTaakWijzigingen(final Task taak, final UUID zaakUuid) {
        eventingService.versturen(TAAK.wijziging(taak));
        eventingService.versturen(ZAAK_TAKEN.wijziging(zaakUuid));
    }

    @PATCH
    @Path("bewerken")
    public RESTTaak bewerkenTaak(final RESTTaak restTaak) {
        Task task = cmmnService.getLopendeTaak(restTaak.id);
        taakConverter.convertTaak(restTaak, task);
        task = cmmnService.saveTask(task);
        eventingService.versturen(TAAK.wijziging(task));
        eventingService.versturen(ZAAK_TAKEN.wijziging(restTaak.zaakUUID));
        return taakConverter.convertTask(task);
    }

    @PATCH
    @Path("afronden")
    public RESTTaak afrondenTaak(final RESTTaak restTaak) {
        final TaskInfo taskInfo = cmmnService.completeTask(restTaak.id);
        eventingService.versturen(TAAK.wijziging(taskInfo));
        eventingService.versturen(ZAAK_TAKEN.wijziging(restTaak.zaakUUID));
        return taakConverter.convertTask(taskInfo);
    }
}
