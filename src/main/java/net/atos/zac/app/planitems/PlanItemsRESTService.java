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

import javax.enterprise.inject.Instance;
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
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.formulieren.FormulierDefinitieService;
import net.atos.zac.formulieren.model.FormulierDefinitie;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Bronnen;
import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.model.MailGegevens;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.helper.OpschortenZaakHelper;
import net.atos.zac.util.DateTimeConverterUtil;
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
    private FormulierDefinitieService formulierDefinitieService;

    @Inject
    private PolicyService policyService;

    @Inject
    private OpschortenZaakHelper opschortenZaakHelper;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

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
        final FormulierDefinitie formulierDefinitie =
                formulierDefinitieService.readFormulierDefinitie(humanTaskData.formulierDefinitie);
        final RESTFormData formData = new RESTFormData(humanTaskData.data);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final ZaakafhandelParameters zaakafhandelParameters =
                zaakafhandelParameterService.readZaakafhandelParameters(UriUtil.uuidFromURI(zaak.getZaaktype()));
        final Optional<HumanTaskParameters> humanTaskParameter = zaakafhandelParameters
                .findHumanTaskParameter(planItem.getPlanItemDefinitionId());


        final LocalDate fataleDatum;
        if (formData.taakFataleDatum != null) {
            fataleDatum = formData.taakFataleDatum;
        } else {
            fataleDatum = humanTaskParameter.isPresent() && humanTaskParameter.get().getDoorlooptijd() != null ?
                    LocalDate.now().plusDays(humanTaskParameter.get().getDoorlooptijd()) : null;
        }

        if (fataleDatum != null && formData.zaakOpschorten) {
            final long aantalDagen = ChronoUnit.DAYS.between(LocalDate.now(), fataleDatum);
            opschortenZaakHelper.opschortenZaak(zaak, aantalDagen, formulierDefinitie.getNaam());
        }

        Map<String, Object> zaakVariablen = zaakVariabelenService.findVariables(zaakUUID);
        zaakVariablen.putAll(formData.dataElementen);
        zaakVariabelenService.setZaakdata(zaakUUID, zaakVariablen);

        if (formulierDefinitie.isMailVersturen()) {
            MailGegevens mailGegevens = getMailGegevens(formulierDefinitie, formData);
            final String body = mailService.sendMail(mailGegevens, Bronnen.fromZaak(zaak));
            formData.formState.put("mail-bericht", body);
            formData.formState.put("mail-onderwerp", mailGegevens.getSubject());
            formData.formState.put("mail-afzender", mailGegevens.getFrom().getEmail());
            formData.formState.put("mail-ontvanger", mailGegevens.getTo().getEmail());
        }

        final String groepID;
        if (formData.taakToekennenGroep != null) {
            groepID = formData.taakToekennenGroep;
        } else {
            groepID = humanTaskParameter.map(HumanTaskParameters::getGroepID).orElse(null);
        }

        final String toelichting;
        if (formData.toelichting != null) {
            toelichting = formData.toelichting;
        } else {
            toelichting = "Taak gestart door %s".formatted(loggedInUserInstance.get().getFullName());
        }

        final String medewerkerID = formData.taakToekennenMedewerker;

        cmmnService.startHumanTaskPlanItem(humanTaskData.planItemInstanceId, groepID,
                                           medewerkerID,
                                           DateTimeConverterUtil.convertToDate(fataleDatum),
                                           toelichting, formData.formState, zaakUUID);
        indexeerService.addOrUpdateZaak(zaakUUID, false);
    }

    public MailGegevens getMailGegevens(final FormulierDefinitie fd, final RESTFormData data) {
        final String gemeente = configuratieService.readGemeenteNaam();
        final String afzender = switch (fd.getMailFrom()) {
            case "GEMEENTE" -> configuratieService.readGemeenteMail();
            case "MEDEWERKER" -> loggedInUserInstance.get().getEmail();
            case default -> data.substitute(fd.getMailFrom());
        };
        final String ontvanger = switch (fd.getMailTo()) {
            case "INITIATOR" -> null; // deze moet nog
            case default -> data.substitute(fd.getMailTo());
        };
        final String body = data.substitute(fd.getMailBody());
        final String subject = data.substitute(fd.getMailSubject());
        return new MailGegevens(new MailAdres(afzender, gemeente), new MailAdres(ontvanger), subject, body);
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
