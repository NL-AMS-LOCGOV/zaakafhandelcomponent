/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.time.DateUtils;
import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.zac.app.planitems.converter.RESTPlanItemConverter;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.PlanItemParameters;

/**
 *
 */
@Singleton
@Path("planitems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanItemsRESTService {

    @Inject
    private FlowableService flowableService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private RESTPlanItemConverter planItemConverter;

    @GET
    @Path("zaak/{uuid}")
    public List<RESTPlanItem> listPlanItemsForZaak(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = flowableService.listPlanItemsForZaak(zaakUUID);
        return planItemConverter.convertPlanItems(planItems, zaakUUID);
    }

    @GET
    @Path("{id}")
    public RESTPlanItem readPlanItem(@PathParam("id") final String planItemId) {
        final PlanItemInstance planItem = flowableService.readPlanItem(planItemId);
        final UUID zaakUuidForCase = flowableService.readZaakUuidForCase(planItem.getCaseInstanceId());
        final PlanItemParameters planItemParameters = zaakafhandelParameterService.getPlanItemParameters(planItem);
        return planItemConverter.convertPlanItem(planItem, zaakUuidForCase, planItemParameters);
    }

    @PUT
    @Path("do/{id}")
    public RESTPlanItem doPlanItem(final RESTPlanItem restPlanItem) {
        if (restPlanItem.type == PlanItemType.HUMAN_TASK) {
            final PlanItemInstance planItem = flowableService.readPlanItem(restPlanItem.id);
            final PlanItemParameters planItemParameters = zaakafhandelParameterService.getPlanItemParameters(planItem);
            final Date streefdatum = DateUtils.addDays(new Date(), planItemParameters.getDoorlooptijd());
            flowableService.startHumanTaskPlanItem(planItem, restPlanItem.groep.id,
                                                   restPlanItem.medewerker != null ? restPlanItem.medewerker.gebruikersnaam : null, streefdatum,
                                                   restPlanItem.taakdata, restPlanItem.taakStuurGegevens.sendMail,
                                                   restPlanItem.taakStuurGegevens.onderwerp);
        } else {
            flowableService.startPlanItem(restPlanItem.id, restPlanItem.toelichting);
        }
        return restPlanItem;
    }
}
