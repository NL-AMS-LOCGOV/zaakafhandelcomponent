/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems;

import static net.atos.zac.configuratie.ConfiguratieService.BIJLAGEN;
import static net.atos.zac.documentcreatie.model.TaakData.TAAKDATA_BODY;
import static net.atos.zac.documentcreatie.model.TaakData.TAAKDATA_EMAILADRES;
import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.time.DateUtils;
import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.planitems.converter.RESTPlanItemConverter;
import net.atos.zac.app.planitems.model.RESTHumanTaskData;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.RESTUserEventListenerData;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mailtemplates.MailTemplateService;
import net.atos.zac.mailtemplates.model.Mail;
import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.UriUtil;
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
    private BRCClientService brcClientService;

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

    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private PolicyService policyService;

    @GET
    @Path("zaak/{uuid}/humanTaskPlanItems")
    public List<RESTPlanItem> listHumanTaskPlanItems(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> humanTaskPlanItems = caseService.listHumanTaskPlanItems(zaakUUID);
        return planItemConverter.convertPlanItems(humanTaskPlanItems, zaakUUID).stream()
                .filter(restPlanItem -> restPlanItem.actief).toList();
    }

    @GET
    @Path("zaak/{uuid}/userEventListenerPlanItems")
    public List<RESTPlanItem> listUserEventListenerPlanItems(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> userEventListenerPlanItems = caseService.listUserEventListenerPlanItems(zaakUUID);
        return planItemConverter.convertPlanItems(userEventListenerPlanItems, zaakUUID);
    }

    @GET
    @Path("humanTaskPlanItem/{id}")
    public RESTPlanItem readHumanTaskPlanItem(@PathParam("id") final String planItemId) {
        final PlanItemInstance humanTaskPlanItem = caseService.readOpenPlanItem(planItemId);
        final UUID zaaktypeUUID = caseVariablesService.readZaaktypeUUID(humanTaskPlanItem.getCaseInstanceId());
        final UUID zaakUuidForCase = caseVariablesService.readZaakUUID(humanTaskPlanItem.getCaseInstanceId());
        final HumanTaskParameters humanTaskParameters =
                zaakafhandelParameterService.readZaakafhandelParameters(zaaktypeUUID)
                        .findHumanTaskParameter(humanTaskPlanItem.getPlanItemDefinitionId());
        return planItemConverter.convertHumanTask(humanTaskPlanItem, zaakUuidForCase, humanTaskParameters);
    }

    @POST
    @Path("doHumanTaskPlanItem")
    public void doHumanTaskplanItem(final RESTHumanTaskData humanTaskData) {
        final PlanItemInstance planItem = caseService.readOpenPlanItem(humanTaskData.planItemInstanceId);
        final UUID zaakUUID = caseVariablesService.readZaakUUID(planItem.getCaseInstanceId());
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final HumanTaskParameters humanTaskParameters =
                zaakafhandelParameterService.readZaakafhandelParameters(UriUtil.uuidFromURI(zaak.getZaaktype()))
                        .findHumanTaskParameter(planItem.getPlanItemDefinitionId());
        final Date streefdatum = humanTaskParameters != null && humanTaskParameters.getDoorlooptijd() != null ?
                DateUtils.addDays(new Date(), humanTaskParameters.getDoorlooptijd()) : null;
        if (humanTaskData.taakStuurGegevens.sendMail) {
            String bijlagen = null;
            if (humanTaskData.taakdata.containsKey(BIJLAGEN) && humanTaskData.taakdata.get(BIJLAGEN) != null) {
                bijlagen = humanTaskData.taakdata.get(BIJLAGEN);
            }

            final MailTemplate mailTemplate =
                    mailTemplateService.readMailtemplate(Mail.valueOf(humanTaskData.taakStuurGegevens.mail));

            humanTaskData.taakdata.put(TAAKDATA_BODY,
                                       mailService.sendMail(
                                               new Ontvanger(humanTaskData.taakdata.get(TAAKDATA_EMAILADRES)),
                                               mailTemplate.getOnderwerp(),
                                               humanTaskData.taakdata.get(TAAKDATA_BODY),
                                               bijlagen, true, zaak,
                                               null));
        }
        caseService.startHumanTask(planItem, humanTaskData.groep.id,
                                   humanTaskData.medewerker != null ? humanTaskData.medewerker.id : null, streefdatum,
                                   humanTaskData.taakdata, zaakUUID);
        indexeerService.addZaak(zaakUUID, false);
    }

    @POST
    @Path("doUserEventListenerPlanItem")
    public void doUserEventListenerPlanItem(final RESTUserEventListenerData userEventListenerData) {
        final Zaak zaak = zrcClientService.readZaak(userEventListenerData.zaakUuid);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getBehandelen());
        switch (userEventListenerData.actie) {
            case INTAKE_AFRONDEN -> {
                final PlanItemInstance planItemInstance = caseService.readOpenPlanItem(
                        userEventListenerData.planItemInstanceId);
                caseVariablesService.setOntvankelijk(planItemInstance.getCaseInstanceId(),
                                                     userEventListenerData.zaakOntvankelijk);
                if (!userEventListenerData.zaakOntvankelijk) {
                    final ZaakafhandelParameters zaakafhandelParameters =
                            zaakafhandelParameterService.readZaakafhandelParameters(
                                    UriUtil.uuidFromURI(zaak.getZaaktype()));
                    zgwApiService.createResultaatForZaak(zaak,
                                                         zaakafhandelParameters.getNietOntvankelijkResultaattype(),
                                                         userEventListenerData.resultaatToelichting);
                }
            }
            case ZAAK_AFHANDELEN -> {
                assertPolicy(!zrcClientService.heeftOpenDeelzaken(zaak));
                if (brcClientService.findBesluit(zaak).isPresent()) {
                    final Resultaat resultaat = zrcClientService.readResultaat(zaak.getResultaat());
                    resultaat.setToelichting(userEventListenerData.resultaatToelichting);
                    zrcClientService.updateResultaat(resultaat);
                } else {
                    zgwApiService.createResultaatForZaak(zaak, userEventListenerData.resultaattypeUuid,
                                                         userEventListenerData.resultaatToelichting);
                }
            }
        }
        caseService.startUserEventListener(userEventListenerData.planItemInstanceId);
    }
}
