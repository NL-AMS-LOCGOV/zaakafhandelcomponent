/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems;

import static net.atos.zac.flowable.FlowableService.VAR_CASE_ZAAK_UUID;

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

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.zac.app.planitems.converter.RESTPlanItemConverter;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.UserEventListenerData;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;

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

    @Inject
    private ZGWApiService zgwApiService;

    @GET
    @Path("zaak/{uuid}")
    public List<RESTPlanItem> listPlanItemsForZaak(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = flowableService.listPlanItemsForOpenCase(zaakUUID);
        return planItemConverter.convertPlanItems(planItems, zaakUUID);
    }

    @GET
    @Path("{id}")
    public RESTPlanItem readHumanTask(@PathParam("id") final String planItemId) {
        final PlanItemInstance planItem = flowableService.readOpenPlanItem(planItemId);
        final UUID zaakUuidForCase = (UUID) flowableService.readOpenCaseVariable(planItem.getCaseInstanceId(), VAR_CASE_ZAAK_UUID);
        final HumanTaskParameters humanTaskParameters = zaakafhandelParameterService.getHumanTaskParameters(planItem);
        return planItemConverter.convertHumanTask(planItem, zaakUuidForCase, humanTaskParameters);
    }

    @PUT
    @Path("doHumanTask")
    public void doHumanTask(final RESTPlanItem restPlanItem) {
        final PlanItemInstance planItem = flowableService.readOpenPlanItem(restPlanItem.id);
        final HumanTaskParameters humanTaskParameters = zaakafhandelParameterService.getHumanTaskParameters(planItem);
        final Date streefdatum = DateUtils.addDays(new Date(), humanTaskParameters.getDoorlooptijd());
        flowableService.startHumanTaskPlanItem(planItem, restPlanItem.groep.id,
                                               restPlanItem.medewerker != null ? restPlanItem.medewerker.gebruikersnaam : null, streefdatum,
                                               restPlanItem.taakdata, restPlanItem.taakStuurGegevens.sendMail,
                                               restPlanItem.taakStuurGegevens.onderwerp);
    }

    @PUT
    @Path("doUserEventListener")
    public void doUserEventListener(final UserEventListenerData userEventListenerData) {
        switch (userEventListenerData.actie) {
            case ONTVANKELIJK -> flowableService.startUserEventListenerPlanItem(userEventListenerData.planItemInstanceId, null);
            case NIET_ONTVANKELIJK ->
                    flowableService.startUserEventListenerPlanItem(userEventListenerData.planItemInstanceId, userEventListenerData.resultaatToelichting);
            case AFHANDELEN -> {
                zgwApiService.createResultaatForZaak(userEventListenerData.zaakUuid, userEventListenerData.resultaattypeUuid,
                                                     userEventListenerData.resultaatToelichting);
                flowableService.startUserEventListenerPlanItem(userEventListenerData.planItemInstanceId, userEventListenerData.resultaatToelichting);
            }
        }
    }
}
