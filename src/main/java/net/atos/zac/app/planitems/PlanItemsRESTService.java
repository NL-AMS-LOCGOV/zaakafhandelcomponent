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

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.planitems.converter.RESTPlanItemConverter;
import net.atos.zac.app.planitems.model.RESTHumanTaskData;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.RESTUserEventListenerData;
import net.atos.zac.app.planitems.model.UserEventListenerActie;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.mail.MailService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zoeken.IndexeerService;

/**
 *
 */
@Singleton
@Path("planitems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanItemsRESTService {

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private CaseService caseService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private RESTPlanItemConverter planItemConverter;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private IndexeerService indexeerService;

    @Inject
    private MailService mailService;

    @GET
    @Path("zaak/{uuid}")
    public List<RESTPlanItem> listPlanItemsForZaak(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> planItems = caseService.listPlanItems(zaakUUID);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        if (zaak.getDeelzaken().stream().anyMatch(uri -> zrcClientService.readZaak(uri).isOpen())) {
            planItems.removeIf(planItem -> planItem.getPlanItemDefinitionId().equals(UserEventListenerActie.ZAAK_AFHANDELEN.toString()));
        }
        return planItemConverter.convertPlanItems(planItems, zaakUUID);
    }

    @GET
    @Path("humanTask/{id}")
    public RESTPlanItem readHumanTask(@PathParam("id") final String planItemId) {
        final PlanItemInstance planItem = caseService.readOpenPlanItem(planItemId);
        final UUID zaakUuidForCase = caseVariablesService.readZaakUUID(planItem.getCaseInstanceId());
        final HumanTaskParameters humanTaskParameters = zaakafhandelParameterService.readHumanTaskParameters(planItem);
        return planItemConverter.convertHumanTask(planItem, zaakUuidForCase, humanTaskParameters);
    }

    @PUT
    @Path("doHumanTask")
    public void doHumanTask(final RESTHumanTaskData humanTaskData) {
        final PlanItemInstance planItem = caseService.readOpenPlanItem(humanTaskData.planItemInstanceId);
        final HumanTaskParameters humanTaskParameters = zaakafhandelParameterService.readHumanTaskParameters(planItem);
        final Date streefdatum = humanTaskParameters.getDoorlooptijd() != null ? DateUtils.addDays(new Date(), humanTaskParameters.getDoorlooptijd()) : null;
        final UUID zaakUUID = caseVariablesService.readZaakUUID(planItem.getCaseInstanceId());
        if (humanTaskData.taakStuurGegevens.sendMail) {
            mailService.sendMail(humanTaskData.taakdata.get("emailadres"), humanTaskData.taakStuurGegevens.onderwerp,
                                 humanTaskData.taakdata.get("body"), true, zaakUUID);
        }
        caseService.startHumanTask(planItem, humanTaskData.groep.id,
                                   humanTaskData.medewerker != null ? humanTaskData.medewerker.id : null, streefdatum,
                                   humanTaskData.taakdata, zaakUUID);
        indexeerService.addZaak(zaakUUID);
    }

    @PUT
    @Path("doUserEventListener")
    public void doUserEventListener(final RESTUserEventListenerData userEventListenerData) {
        switch (userEventListenerData.actie) {
            case INTAKE_AFRONDEN -> {
                final PlanItemInstance planItemInstance = caseService.readOpenPlanItem(userEventListenerData.planItemInstanceId);
                caseVariablesService.setOntvankelijk(planItemInstance.getCaseInstanceId(), userEventListenerData.zaakOntvankelijk);
                if (!userEventListenerData.zaakOntvankelijk) {
                    final Zaak zaak = zrcClientService.readZaak(userEventListenerData.zaakUuid);
                    final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(zaak);
                    zgwApiService.createResultaatForZaak(zaak, zaakafhandelParameters.getNietOntvankelijkResultaattype(),
                                                         userEventListenerData.resultaatToelichting);
                }
            }
            case ZAAK_AFHANDELEN -> {
                zgwApiService.createResultaatForZaak(userEventListenerData.zaakUuid, userEventListenerData.resultaattypeUuid,
                                                     userEventListenerData.resultaatToelichting);
            }
        }
        caseService.startUserEventListener(userEventListenerData.planItemInstanceId);
    }
}
