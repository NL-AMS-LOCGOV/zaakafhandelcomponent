/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems;

import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import org.flowable.cmmn.api.runtime.PlanItemInstance;

import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.planitems.converter.RESTPlanItemConverter;
import net.atos.zac.app.planitems.model.RESTHumanTaskData;
import net.atos.zac.app.planitems.model.RESTPlanItem;
import net.atos.zac.app.planitems.model.RESTProcessTaskData;
import net.atos.zac.app.planitems.model.RESTUserEventListenerData;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Bronnen;
import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.MailTemplateService;
import net.atos.zac.mailtemplates.model.Mail;
import net.atos.zac.mailtemplates.model.MailGegevens;
import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.helper.OpschortenZaakHelper;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.MailtemplateKoppeling;
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

    private static final String REDEN_OPSCHORTING = "Aanvullende informatie opgevraagd";

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private ZaakVariabelenService zaakVariabelenService;

    @Inject
    private CMMNService cmmnService;

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
    private ConfiguratieService configuratieService;

    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private PolicyService policyService;

    @Inject
    private OpschortenZaakHelper opschortenZaakHelper;

    @GET
    @Path("zaak/{uuid}/humanTaskPlanItems")
    public List<RESTPlanItem> listHumanTaskPlanItems(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> humanTaskPlanItems = cmmnService.listHumanTaskPlanItems(zaakUUID);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return planItemConverter.convertPlanItems(humanTaskPlanItems, zaak).stream()
                .filter(restPlanItem -> restPlanItem.actief)
                .toList();
    }

    @GET
    @Path("zaak/{uuid}/processTaskPlanItems")
    public List<RESTPlanItem> listProcessTaskPlanItems(@PathParam("uuid") final UUID zaakUUID) {
        final var processTaskPlanItems = cmmnService.listProcessTaskPlanItems(zaakUUID);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return planItemConverter.convertPlanItems(processTaskPlanItems, zaak);
    }

    @GET
    @Path("zaak/{uuid}/userEventListenerPlanItems")
    public List<RESTPlanItem> listUserEventListenerPlanItems(@PathParam("uuid") final UUID zaakUUID) {
        final List<PlanItemInstance> userEventListenerPlanItems = cmmnService.listUserEventListenerPlanItems(zaakUUID);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return planItemConverter.convertPlanItems(userEventListenerPlanItems, zaak);
    }

    @GET
    @Path("humanTaskPlanItem/{id}")
    public RESTPlanItem readHumanTaskPlanItem(@PathParam("id") final String planItemId) {
        final PlanItemInstance humanTaskPlanItem = cmmnService.readOpenPlanItem(planItemId);
        final UUID zaakUUID = zaakVariabelenService.readZaakUUID(humanTaskPlanItem);
        final UUID zaaktypeUUID = zaakVariabelenService.readZaaktypeUUID(humanTaskPlanItem);
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                zaaktypeUUID);
        return planItemConverter.convertPlanItem(humanTaskPlanItem, zaakUUID, zaakafhandelParameters);
    }

    @GET
    @Path("processTaskPlanItem/{id}")
    public RESTPlanItem readProcessTaskPlanItem(@PathParam("id") final String planItemId) {
        final PlanItemInstance processTaskPlanItem = cmmnService.readOpenPlanItem(planItemId);
        final UUID zaakUUID = zaakVariabelenService.readZaakUUID(processTaskPlanItem);
        final UUID zaaktypeUUID = zaakVariabelenService.readZaaktypeUUID(processTaskPlanItem);
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                zaaktypeUUID);
        return planItemConverter.convertPlanItem(processTaskPlanItem, zaakUUID, zaakafhandelParameters);
    }

    @POST
    @Path("doHumanTaskPlanItem")
    public void doHumanTaskplanItem(final RESTHumanTaskData humanTaskData) {
        final PlanItemInstance planItem = cmmnService.readOpenPlanItem(humanTaskData.planItemInstanceId);
        final UUID zaakUUID = zaakVariabelenService.readZaakUUID(planItem);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Map<String, String> taakdata = humanTaskData.taakdata;
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final ZaakafhandelParameters zaakafhandelParameters =
                zaakafhandelParameterService.readZaakafhandelParameters(UriUtil.uuidFromURI(zaak.getZaaktype()));
        final Optional<HumanTaskParameters> humanTaskParameters = zaakafhandelParameters
                .findHumanTaskParameter(planItem.getPlanItemDefinitionId());

        final LocalDate fataleDatum;
        if (humanTaskData.fataledatum != null) {
            fataleDatum = humanTaskData.fataledatum;
        } else {
            fataleDatum = humanTaskParameters.isPresent() && humanTaskParameters.get().getDoorlooptijd() != null ?
                    LocalDate.now().plusDays(humanTaskParameters.get().getDoorlooptijd()) : null;
        }
        if (fataleDatum != null && taakVariabelenService.isZaakOpschorten(taakdata)) {
            final long aantalDagen = ChronoUnit.DAYS.between(LocalDate.now(), fataleDatum);
            opschortenZaakHelper.opschortenZaak(zaak, aantalDagen, REDEN_OPSCHORTING);
        }

        if (humanTaskData.taakStuurGegevens.sendMail) {
            final Mail mail = Mail.valueOf(humanTaskData.taakStuurGegevens.mail);

            final MailTemplate mailTemplate = zaakafhandelParameters.getMailtemplateKoppelingen().stream()
                    .map(MailtemplateKoppeling::getMailTemplate)
                    .filter(template -> template.getMail().equals(mail))
                    .findFirst()
                    .orElseGet(() -> mailTemplateService.readMailtemplate(mail));

            final String afzender = configuratieService.readGemeenteNaam();
            taakVariabelenService.setMailBody(taakdata, mailService.sendMail(
                    new MailGegevens(
                            taakVariabelenService.readMailFrom(taakdata)
                                    .map(email -> new MailAdres(email, afzender))
                                    .orElseGet(() -> mailService.getGemeenteMailAdres()),
                            taakVariabelenService.readMailTo(taakdata)
                                    .map(MailAdres::new)
                                    .orElse(null),
                            taakVariabelenService.readMailReplyTo(taakdata)
                                    .map(email -> new MailAdres(email, afzender))
                                    .orElse(null),
                            mailTemplate.getOnderwerp(),
                            taakVariabelenService.readMailBody(taakdata).orElse(null),
                            taakVariabelenService.readMailBijlagen(taakdata).orElse(null),
                            true),
                    Bronnen.fromZaak(zaak)));
        }
        cmmnService.startHumanTaskPlanItem(humanTaskData.planItemInstanceId, humanTaskData.groep.id,
                                           humanTaskData.medewerker != null ? humanTaskData.medewerker.id : null,
                                           DateTimeConverterUtil.convertToDate(fataleDatum),
                                           humanTaskData.toelichting, taakdata, zaakUUID);
        indexeerService.addOrUpdateZaak(zaakUUID, false);
    }

    @POST
    @Path("doProcessTaskPlanItem")
    public void doProcessTaskplanItem(final RESTProcessTaskData processTaskData) {
        cmmnService.startProcessTaskPlanItem(processTaskData.planItemInstanceId, processTaskData.data);
    }

    @POST
    @Path("doUserEventListenerPlanItem")
    public void doUserEventListenerPlanItem(final RESTUserEventListenerData userEventListenerData) {
        final Zaak zaak = zrcClientService.readZaak(userEventListenerData.zaakUuid);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getBehandelen());
        switch (userEventListenerData.actie) {
            case INTAKE_AFRONDEN -> {
                final PlanItemInstance planItemInstance = cmmnService.readOpenPlanItem(
                        userEventListenerData.planItemInstanceId);
                zaakVariabelenService.setOntvankelijk(planItemInstance, userEventListenerData.zaakOntvankelijk);
                if (!userEventListenerData.zaakOntvankelijk) {
                    policyService.checkZaakAfsluitbaar(zaak);
                    final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                            UriUtil.uuidFromURI(zaak.getZaaktype()));
                    zgwApiService.createResultaatForZaak(zaak,
                                                         zaakafhandelParameters.getNietOntvankelijkResultaattype(),
                                                         userEventListenerData.resultaatToelichting);
                }
            }
            case ZAAK_AFHANDELEN -> {
                policyService.checkZaakAfsluitbaar(zaak);
                if (brcClientService.listBesluiten(zaak).isPresent()) {
                    final Resultaat resultaat = zrcClientService.readResultaat(zaak.getResultaat());
                    resultaat.setToelichting(userEventListenerData.resultaatToelichting);
                    zrcClientService.updateResultaat(resultaat);
                } else {
                    zgwApiService.createResultaatForZaak(zaak, userEventListenerData.resultaattypeUuid,
                                                         userEventListenerData.resultaatToelichting);
                }
            }
        }
        cmmnService.startUserEventListenerPlanItem(userEventListenerData.planItemInstanceId);
    }
}
