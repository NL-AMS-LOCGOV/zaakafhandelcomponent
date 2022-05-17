/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAKTYPE_UUUID;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAK_IDENTIFICATIE;
import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAK_UUID;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.engine.RuntimeService;
import org.flowable.task.api.Task;

import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.HerindexeerInfo;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Path("fixutil")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_HTML)
public class FixUtilRESTService {

    private static final Logger LOG = Logger.getLogger(FixUtilRESTService.class.getName());

    @Inject
    private CmmnRuntimeService cmmnRuntimeService;

    @Inject
    private CmmnTaskService cmmnTaskService;

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private IndexeerService indexeerService;

    @GET
    @Path("countmissing")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countMissingVariables() {
        countMissingVariable(VAR_CASE_ZAAK_UUID);
        countMissingVariable(VAR_CASE_ZAAK_IDENTIFICATIE);
        countMissingVariable(VAR_CASE_ZAAKTYPE_UUUID);
        countMissingVariable(VAR_CASE_ZAAKTYPE_OMSCHRIJVING);
        return Response.noContent().build();
    }

    @GET
    @Path("herindexeren/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public HerindexeerInfo herindexeer(@PathParam("type") ZoekObjectType type) {
        return indexeerService.herindexeren(type);
    }

    /**
     * TODO: #993 - Indexeren extern aanroepen dmv Cron/Curl
     *
     * @param type   ZAAK is momenteel geimplementeerd
     * @param aantal 100 is een mooi aantal
     * @return het aantal resterende items na het uitvoeren van deze call
     */
    @GET
    @Path("indexeren/{type}/{aantal}")
    @Produces(MediaType.APPLICATION_JSON)
    public String indexeer(@PathParam("type") ZoekObjectType type, @PathParam("aantal") int aantal) {
        if (type == ZoekObjectType.ZAAK) {
            int aantalResterend = indexeerService.indexeerZaken(aantal);
            return "\"Aantal resterende zaken: %d\"".formatted(aantalResterend);
        } else {
            throw new RuntimeException("indexeren: " + type + "nog niet geïmplementeerd");
        }
    }

    @GET
    @Path("logzaaktypeuuid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logExistingZaaktypeUUID() {
        final List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery().variableExists(VAR_CASE_ZAAKTYPE_UUUID).list();
        caseInstances.forEach(caseInstance -> logVariable(caseInstance.getId(), VAR_CASE_ZAAKTYPE_UUUID));
        return Response.noContent().build();
    }

    @GET
    @Path("fixmissingzaaktypeuuid/{zaaktypeuuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fixMissingZaaktypeUUID(@PathParam("zaaktypeuuid") final String zaaktypeUUIDString) {
        final UUID zaaktypeUUID = UUID.fromString(zaaktypeUUIDString);
        final List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery().variableNotExists(VAR_CASE_ZAAKTYPE_UUUID).list();
        caseInstances.forEach(caseInstance -> fixVariable(caseInstance.getId(), VAR_CASE_ZAAKTYPE_UUUID, zaaktypeUUID));
        return Response.noContent().build();
    }

    @GET
    @Path("fixexistingzaaktypeuuid/{zaaktypeuuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fixAllZaaktypeUUID(@PathParam("zaaktypeuuid") final String zaaktypeUUIDString) {
        final UUID zaaktypeUUID = UUID.fromString(zaaktypeUUIDString);
        final List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery().variableExists(VAR_CASE_ZAAKTYPE_UUUID).list();
        caseInstances.forEach(caseInstance -> fixVariable(caseInstance.getId(), VAR_CASE_ZAAKTYPE_UUUID, zaaktypeUUID));
        return Response.noContent().build();
    }

    @GET
    @Path("logtasksmissingscopeid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logTasksMissingScopeId() {
        final List<Task> tasks = cmmnTaskService.createTaskQuery().list().stream().filter(task -> task.getScopeId() == null).toList();
        LOG.info(String.format("Number of tasks missing scopeId : %d", tasks.size()));
        tasks.forEach(task -> LOG.info(String.format("%s : name = '%s', createTime = '%s'", task.getId(), task.getName(), task.getCreateTime().toString())));
        return Response.noContent().build();
    }

    @GET
    @Path("completetasksmissingscopeid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTasksMissingScopeId() {
        runtimeService.createActivityInstanceQuery().list()
                .forEach(activityInstance -> runtimeService.deleteProcessInstance(activityInstance.getProcessInstanceId(), "none"));
        return Response.noContent().build();
    }

    private void countMissingVariable(final String variable) {
        final long count = cmmnRuntimeService.createCaseInstanceQuery().variableNotExists(variable).count();
        LOG.info(String.format("Number of cases missing variable '%s' = %d", variable, count));
    }

    private void logVariable(final String caseInstanceId, final String variable) {
        final Object value = cmmnRuntimeService.getVariable(caseInstanceId, variable);
        LOG.info(String.format("'%s' : '%s' = '%s'", caseInstanceId, variable, value.toString()));
    }

    private void fixVariable(final String caseInstanceId, final String variable, final Object value) {
        LOG.info(String.format("'%s' : Set '%s' to '%s'", caseInstanceId, variable, value));
        cmmnRuntimeService.setVariable(caseInstanceId, variable, value);
    }
}
