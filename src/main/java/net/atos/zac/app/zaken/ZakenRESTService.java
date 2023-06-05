/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import static net.atos.client.zgw.ztc.model.Statustype.isHeropend;
import static net.atos.client.zgw.ztc.model.Statustype.isIntake;
import static net.atos.zac.configuratie.ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER;
import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;
import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.BesluitInformatieobject;
import net.atos.client.zgw.brc.model.Vervalreden;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.AardRelatie;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.HoofdzaakZaakPatch;
import net.atos.client.zgw.zrc.model.LocatieZaakPatch;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.NietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RelevanteZaak;
import net.atos.client.zgw.zrc.model.RelevantezaakZaakPatch;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolNietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Vestiging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.aanvraag.InboxProductaanvraagService;
import net.atos.zac.aanvraag.ProductaanvraagDenhaag;
import net.atos.zac.aanvraag.ProductaanvraagService;
import net.atos.zac.app.admin.converter.RESTZaakAfzenderConverter;
import net.atos.zac.app.admin.model.RESTZaakAfzender;
import net.atos.zac.app.audit.converter.RESTHistorieRegelConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;
import net.atos.zac.app.bag.converter.RESTBAGConverter;
import net.atos.zac.app.bag.model.RESTBAGObject;
import net.atos.zac.app.klanten.KlantenRESTService;
import net.atos.zac.app.klanten.model.klant.IdentificatieType;
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraag;
import net.atos.zac.app.zaken.converter.RESTBesluitConverter;
import net.atos.zac.app.zaken.converter.RESTBesluittypeConverter;
import net.atos.zac.app.zaken.converter.RESTCommunicatiekanaalConverter;
import net.atos.zac.app.zaken.converter.RESTGeometryConverter;
import net.atos.zac.app.zaken.converter.RESTResultaattypeConverter;
import net.atos.zac.app.zaken.converter.RESTZaakBetrokkeneConverter;
import net.atos.zac.app.zaken.converter.RESTZaakConverter;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTBesluit;
import net.atos.zac.app.zaken.model.RESTBesluitIntrekkenGegevens;
import net.atos.zac.app.zaken.model.RESTBesluitVastleggenGegevens;
import net.atos.zac.app.zaken.model.RESTBesluitWijzigenGegevens;
import net.atos.zac.app.zaken.model.RESTBesluittype;
import net.atos.zac.app.zaken.model.RESTCommunicatiekanaal;
import net.atos.zac.app.zaken.model.RESTDocumentOntkoppelGegevens;
import net.atos.zac.app.zaken.model.RESTReden;
import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakAanmaakGegevens;
import net.atos.zac.app.zaken.model.RESTZaakAfbrekenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakAfsluitenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkene;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkeneGegevens;
import net.atos.zac.app.zaken.model.RESTZaakEditMetRedenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakHeropenenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakKoppelGegevens;
import net.atos.zac.app.zaken.model.RESTZaakLocatieGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOntkoppelGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOpschortGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOpschorting;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakToekennenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakVerlengGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.app.zaken.model.RESTZakenVerdeelGegevens;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.BPMNService;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.healthcheck.HealthCheckService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.exception.FoutmeldingException;
import net.atos.zac.shared.helper.OpschortenZaakHelper;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.util.LocalDateUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.websocket.event.ScreenEventType;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakAfzender;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;
import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

/**
 *
 */
@Path("zaken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ZakenRESTService {

    private static final String ROL_VERWIJDER_REDEN = "Verwijderd door de medewerker tijdens het behandelen van de zaak";

    private static final String ROL_TOEVOEGEN_REDEN = "Toegekend door de medewerker tijdens het behandelen van de zaak";

    private static final String AANMAKEN_ZAAK_REDEN = "Aanmaken zaak";

    private static final String VERLENGING = "Verlenging";

    private static final String AANMAKEN_BESLUIT_TOELICHTING = "Aanmaken besluit";

    private static final String WIJZIGEN_BESLUIT_TOELICHTING = "Wijzigen besluit";

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ProductaanvraagService productaanvraagService;

    @Inject
    private BRCClientService brcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private EventingService eventingService;

    @Inject
    private IdentityService identityService;

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private OntkoppeldeDocumentenService ontkoppeldeDocumentenService;

    @Inject
    private IndexeerService indexeerService;

    @Inject
    private PolicyService policyService;

    @Inject
    private CMMNService cmmnService;

    @Inject
    private BPMNService bpmnService;

    @Inject
    private TakenService takenService;

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private InboxProductaanvraagService inboxProductaanvraagService;

    @Inject
    private ZaakVariabelenService zaakVariabelenService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private RESTZaakConverter zaakConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private RESTBesluitConverter besluitConverter;

    @Inject
    private RESTBesluittypeConverter besluittypeConverter;

    @Inject
    private RESTResultaattypeConverter resultaattypeConverter;

    @Inject
    private RESTZaakOverzichtConverter zaakOverzichtConverter;

    @Inject
    private RESTBAGConverter bagConverter;

    @Inject
    private RESTHistorieRegelConverter auditTrailConverter;

    @Inject
    private RESTZaakBetrokkeneConverter zaakBetrokkeneConverter;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private RESTCommunicatiekanaalConverter communicatiekanaalConverter;

    @Inject
    private RESTGeometryConverter restGeometryConverter;

    @Inject
    private HealthCheckService healthCheckService;

    @Inject
    private OpschortenZaakHelper opschortenZaakHelper;

    @Inject
    private RESTZaakAfzenderConverter zaakAfzenderConverter;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @GET
    @Path("zaak/{uuid}")
    public RESTZaak readZaak(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final RESTZaak restZaak = zaakConverter.convert(zaak);
        assertPolicy(restZaak.rechten.lezen);
        deleteSignaleringen(zaak);
        return restZaak;
    }

    @GET
    @Path("zaak/id/{identificatie}")
    public RESTZaak readZaakById(@PathParam("identificatie") final String identificatie) {
        final Zaak zaak = zrcClientService.readZaakByID(identificatie);
        final RESTZaak restZaak = zaakConverter.convert(zaak);
        assertPolicy(restZaak.rechten.lezen);
        deleteSignaleringen(zaak);
        return restZaak;
    }

    @PUT
    @Path("initiator")
    public RESTZaak updateInitiator(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        zgwApiService.findInitiatorForZaak(zaak)
                .ifPresent(initiator -> removeInitiator(zaak, initiator, ROL_VERWIJDER_REDEN));
        addInitiator(gegevens.betrokkeneIdentificatieType, gegevens.betrokkeneIdentificatie, zaak);
        return zaakConverter.convert(zaak);
    }

    @DELETE
    @Path("{uuid}/initiator")
    public RESTZaak deleteInitiator(@PathParam("uuid") final UUID zaakUUID, RESTReden reden) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zgwApiService.findInitiatorForZaak(zaak)
                .ifPresent(initiator -> removeInitiator(zaak, initiator, reden.reden));
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("betrokkene")
    public RESTZaak createBetrokken(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        addBetrokkene(gegevens.roltypeUUID, gegevens.betrokkeneIdentificatieType, gegevens.betrokkeneIdentificatie,
                      gegevens.roltoelichting, zaak);
        return zaakConverter.convert(zaak);
    }

    @DELETE
    @Path("betrokkene/{uuid}")
    public RESTZaak deleteBetrokkene(@PathParam("uuid") final UUID betrokkeneUUID, final RESTReden reden) {
        final Rol<?> betrokkene = zrcClientService.readRol(betrokkeneUUID);
        final Zaak zaak = zrcClientService.readZaak(betrokkene.getZaak());
        removeBetrokkene(zaak, betrokkene, reden.reden);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("zaak")
    public RESTZaak createZaak(final RESTZaakAanmaakGegevens restZaakAanmaakGegevens) {
        final RESTZaak restZaak = restZaakAanmaakGegevens.zaak;
        assertPolicy(policyService.readOverigeRechten().getStartenZaak() &&
                             loggedInUserInstance.get().isGeautoriseerdZaaktype(restZaak.zaaktype.omschrijving));
        final Zaaktype zaaktype = ztcClientService.readZaaktype(restZaak.zaaktype.uuid);
        final Zaak zaak = zgwApiService.createZaak(zaakConverter.convert(restZaak, zaaktype));
        if (StringUtils.isNotEmpty(restZaak.initiatorIdentificatie)) {
            addInitiator(restZaak.initiatorIdentificatieType, restZaak.initiatorIdentificatie, zaak);
        }
        if (restZaak.groep != null) {
            final Group group = identityService.readGroup(restZaak.groep.id);
            zrcClientService.updateRol(zaak, bepaalRolGroep(group, zaak), AANMAKEN_ZAAK_REDEN);
        }
        if (restZaak.behandelaar != null) {
            final User user = identityService.readUser(restZaak.behandelaar.id);
            zrcClientService.updateRol(zaak, bepaalRolMedewerker(user, zaak), AANMAKEN_ZAAK_REDEN);
        }
        cmmnService.startCase(zaak, zaaktype,
                              zaakafhandelParameterService.readZaakafhandelParameters(zaaktype.getUUID()), null);

        if (restZaakAanmaakGegevens.inboxProductaanvraag != null) {
            koppelInboxProductaanvraag(zaak, restZaakAanmaakGegevens.inboxProductaanvraag);
        }

        if (restZaakAanmaakGegevens.bagObjecten != null) {
            for (final RESTBAGObject restbagObject : restZaakAanmaakGegevens.bagObjecten) {
                final Zaakobject zaakobject = bagConverter.convertToZaakobject(restbagObject, zaak);
                zrcClientService.createZaakobject(zaakobject);
            }
        }

        return zaakConverter.convert(zaak);
    }

    private void koppelInboxProductaanvraag(final Zaak zaak, final RESTInboxProductaanvraag inboxProductaanvraag) {
        final ORObject productaanvraagObject = objectsClientService.readObject(inboxProductaanvraag.productaanvraagObjectUUID);
        final ProductaanvraagDenhaag productaanvraag = productaanvraagService.getProductaanvraag(productaanvraagObject);

        productaanvraagService.pairProductaanvraagWithZaak(productaanvraagObject, zaak.getUrl());
        productaanvraagService.pairAanvraagPDFWithZaak(productaanvraag, zaak.getUrl());
        productaanvraagService.pairBijlagenWithZaak(productaanvraag.getAttachments(), zaak.getUrl());

        //verwijder het verwerkte inbox productaanvraag item
        inboxProductaanvraagService.delete(inboxProductaanvraag.id);
        zaakVariabelenService.setZaakdata(zaak.getUuid(), productaanvraagService.getFormulierData(productaanvraagObject));
    }

    @PATCH
    @Path("zaak/{uuid}")
    public RESTZaak updateZaak(@PathParam("uuid") final UUID zaakUUID,
            final RESTZaakEditMetRedenGegevens restZaakEditMetRedenGegevens) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getWijzigen());
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID, zaakConverter.convertToPatch(
                                                                    restZaakEditMetRedenGegevens.zaak),
                                                            restZaakEditMetRedenGegevens.reden);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("{uuid}/zaaklocatie")
    public RESTZaak updateZaakLocatie(@PathParam("uuid") final UUID zaakUUID,
            final RESTZaakLocatieGegevens locatieGegevens) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getWijzigen());
        final LocatieZaakPatch locatieZaakPatch = new LocatieZaakPatch(
                restGeometryConverter.convert(locatieGegevens.geometrie));
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID, locatieZaakPatch, locatieGegevens.reden);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("zaak/{uuid}/opschorting")
    public RESTZaak opschortenZaak(@PathParam("uuid") final UUID zaakUUID,
            final RESTZaakOpschortGegevens opschortGegevens) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        if (opschortGegevens.indicatieOpschorting) {
            return zaakConverter.convert(opschortenZaakHelper.opschortenZaak(zaak, opschortGegevens.duurDagen,
                                                                             opschortGegevens.redenOpschorting));
        } else {
            return zaakConverter.convert(opschortenZaakHelper.hervattenZaak(zaak, opschortGegevens.redenOpschorting));
        }
    }

    @GET
    @Path("zaak/{uuid}/opschorting")
    public RESTZaakOpschorting readOpschortingZaak(@PathParam("uuid") final UUID zaakUUID) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getLezen());
        final RESTZaakOpschorting zaakOpschorting = new RESTZaakOpschorting();
        zaakVariabelenService.findDatumtijdOpgeschort(zaakUUID)
                .ifPresent(datumtijdOpgeschort -> zaakOpschorting.vanafDatumTijd = datumtijdOpgeschort);
        zaakVariabelenService.findVerwachteDagenOpgeschort(zaakUUID)
                .ifPresent(verwachteDagenOpgeschort -> zaakOpschorting.duurDagen = verwachteDagenOpgeschort);
        return zaakOpschorting;
    }

    @PATCH
    @Path("zaak/{uuid}/verlenging")
    public RESTZaak verlengenZaak(@PathParam("uuid") final UUID zaakUUID,
            final RESTZaakVerlengGegevens restZaakVerlengGegevens) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
        assertPolicy(zaak.isOpen() && !isHeropend(statustype) && !zaak.isOpgeschort() &&
                             policyService.readZaakRechten(zaak).getBehandelen());
        final String toelichting = String.format("%s: %s", VERLENGING, restZaakVerlengGegevens.redenVerlenging);
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID, zaakConverter.convertToPatch(zaakUUID,
                                                                                                   restZaakVerlengGegevens),
                                                            toelichting);
        if (restZaakVerlengGegevens.takenVerlengen) {
            final int aantalTakenVerlengd = verlengOpenTaken(zaakUUID, restZaakVerlengGegevens.duurDagen);
            if (aantalTakenVerlengd > 0) {
                eventingService.send(ZAAK_TAKEN.updated(updatedZaak));
            }
        }
        return zaakConverter.convert(updatedZaak, status, statustype);
    }

    @PUT
    @Path("zaakinformatieobjecten/ontkoppel")
    public void ontkoppelInformatieObject(final RESTDocumentOntkoppelGegevens ontkoppelGegevens) {
        final Zaak zaak = zrcClientService.readZaak(ontkoppelGegevens.zaakUUID);
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(
                ontkoppelGegevens.documentUUID);
        assertPolicy(policyService.readDocumentRechten(informatieobject, zaak).getWijzigen());
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(informatieobject.getUrl());
        parameters.setZaak(zaak.getUrl());
        List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(parameters);
        if (zaakInformatieobjecten.isEmpty()) {
            throw new NotFoundException(
                    String.format("Geen ZaakInformatieobject gevonden voor Zaak: '%s' en InformatieObject: '%s'",
                                  ontkoppelGegevens.zaakUUID,
                                  ontkoppelGegevens.documentUUID));
        }
        zaakInformatieobjecten.forEach(zaakInformatieobject ->
                                               zrcClientService.deleteZaakInformatieobject(
                                                       zaakInformatieobject.getUuid(),
                                                       ontkoppelGegevens.reden,
                                                       "Ontkoppeld"));
        if (zrcClientService.listZaakinformatieobjecten(informatieobject).isEmpty()) {
            indexeerService.removeInformatieobject(informatieobject.getUUID());
            ontkoppeldeDocumentenService.create(informatieobject, zaak, ontkoppelGegevens.reden);
        }
    }

    @GET
    @Path("waarschuwing")
    public List<RESTZaakOverzicht> listZaakWaarschuwingen() {
        final LocalDate vandaag = LocalDate.now();
        final Map<UUID, LocalDate> einddatumGeplandWaarschuwing = new HashMap<>();
        final Map<UUID, LocalDate> uiterlijkeEinddatumAfdoeningWaarschuwing = new HashMap<>();
        zaakafhandelParameterService.listZaakafhandelParameters().forEach(parameters -> {
            if (parameters.getEinddatumGeplandWaarschuwing() != null) {
                einddatumGeplandWaarschuwing.put(parameters.getZaakTypeUUID(),
                                                 datumWaarschuwing(vandaag,
                                                                   parameters.getEinddatumGeplandWaarschuwing()));
            }
            if (parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing() != null) {
                uiterlijkeEinddatumAfdoeningWaarschuwing.put(parameters.getZaakTypeUUID(),
                                                             datumWaarschuwing(vandaag,
                                                                               parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing()));
            }
        });
        final ZaakListParameters zaakListParameters = new ZaakListParameters();
        zaakListParameters.setRolBetrokkeneIdentificatieMedewerkerIdentificatie(loggedInUserInstance.get().getId());
        return zrcClientService.listZaken(zaakListParameters).getResults().stream()
                .filter(Zaak::isOpen)
                .filter(zaak -> isWaarschuwing(zaak, vandaag, einddatumGeplandWaarschuwing,
                                               uiterlijkeEinddatumAfdoeningWaarschuwing))
                .map(zaakOverzichtConverter::convert)
                .toList();
    }

    private boolean isWaarschuwing(
            final Zaak zaak,
            final LocalDate vandaag,
            final Map<UUID, LocalDate> einddatumGeplandWaarschuwing,
            final Map<UUID, LocalDate> uiterlijkeEinddatumAfdoeningWaarschuwing) {
        final UUID zaaktypeUUID = UriUtil.uuidFromURI(zaak.getZaaktype());
        return (zaak.getEinddatumGepland() != null &&
                isWaarschuwing(vandaag, zaak.getEinddatumGepland(), einddatumGeplandWaarschuwing.get(zaaktypeUUID))) ||
                isWaarschuwing(vandaag, zaak.getUiterlijkeEinddatumAfdoening(),
                               uiterlijkeEinddatumAfdoeningWaarschuwing.get(zaaktypeUUID));
    }

    private boolean isWaarschuwing(final LocalDate vandaag, final LocalDate datum, final LocalDate datumWaarschuwing) {
        return datumWaarschuwing != null && !datum.isBefore(vandaag) && datum.isBefore(datumWaarschuwing);
    }

    private LocalDate datumWaarschuwing(final LocalDate vandaag, final int dagen) {
        return vandaag.plusDays(dagen + 1);
    }


    @GET
    @Path("zaaktypes")
    public List<RESTZaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI()).stream()
                .filter(zaaktype -> loggedInUserInstance.get().isGeautoriseerdZaaktype(zaaktype.getOmschrijving()))
                .filter(zaaktype -> !zaaktype.getConcept())
                .filter(Zaaktype::isNuGeldig)
                .filter(zaaktype -> healthCheckService.controleerZaaktype(zaaktype.getUrl()).isValide())
                .map(zaaktypeConverter::convert)
                .toList();
    }

    @PUT
    @Path("zaakdata")
    public RESTZaak updateZaakdata(final RESTZaak restZaak) {
        final Zaak zaak = zrcClientService.readZaak(restZaak.uuid);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getWijzigen());

        zaakVariabelenService.setZaakdata(restZaak.uuid, restZaak.zaakdata);
        return restZaak;
    }

    @PATCH
    @Path("toekennen")
    public RESTZaak toekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getToekennen());

        String behandelaar = zgwApiService.findBehandelaarForZaak(zaak)
                .map(rolMedewerker -> rolMedewerker.getBetrokkeneIdentificatie().getIdentificatie())
                .orElse(null);

        if (!StringUtils.equals(behandelaar, toekennenGegevens.behandelaarGebruikersnaam)) {
            if (StringUtils.isNotEmpty(toekennenGegevens.behandelaarGebruikersnaam)) {
                // Toekennen of overdragen
                final User user = identityService.readUser(toekennenGegevens.behandelaarGebruikersnaam);
                zrcClientService.updateRol(zaak, bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
            } else {
                // Vrijgeven
                zrcClientService.deleteRol(zaak, BetrokkeneType.MEDEWERKER, toekennenGegevens.reden);
            }
        }

        zgwApiService.findGroepForZaak(zaak)
                .ifPresent(groep -> {
                    if (!StringUtils.equals(groep.getBetrokkeneIdentificatie().getIdentificatie(),
                                            toekennenGegevens.groepId)) {
                        final Group group = identityService.readGroup(toekennenGegevens.groepId);
                        zrcClientService.updateRol(zaak, bepaalRolGroep(group, zaak), toekennenGegevens.reden);
                    }
                });

        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("lijst/toekennen/mij")
    public RESTZaakOverzicht toekennenAanIngelogdeMedewerkerVanuitLijst(
            final RESTZaakToekennenGegevens toekennenGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken());
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(toekennenGegevens);
        indexeerService.indexeerDirect(zaak.getUuid().toString(), ZoekObjectType.ZAAK);
        return zaakOverzichtConverter.convert(zaak);
    }

    @PUT
    @Path("lijst/verdelen")
    public void verdelenVanuitLijst(final RESTZakenVerdeelGegevens verdeelGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten()
                .getZakenTakenVerdelen());
        final Group group = !StringUtils.isEmpty(verdeelGegevens.groepId) ? identityService.readGroup(
                verdeelGegevens.groepId) : null;
        final User user = !StringUtils.isEmpty(verdeelGegevens.behandelaarGebruikersnaam) ?
                identityService.readUser(verdeelGegevens.behandelaarGebruikersnaam) : null;
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            if (group != null) {
                zrcClientService.updateRol(zaak, bepaalRolGroep(group, zaak), verdeelGegevens.reden);
            }
            if (user != null) {
                zrcClientService.updateRol(zaak, bepaalRolMedewerker(user, zaak), verdeelGegevens.reden);
            }
        });
        indexeerService.indexeerDirect(verdeelGegevens.uuids.stream().map(UUID::toString).collect(Collectors.toList()),
                                       ZoekObjectType.ZAAK);
    }

    @PUT
    @Path("lijst/vrijgeven")
    public void vrijgevenVanuitLijst(final RESTZakenVerdeelGegevens verdeelGegevens) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken() && policyService.readWerklijstRechten()
                .getZakenTakenVerdelen());
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            zrcClientService.deleteRol(zaak, BetrokkeneType.MEDEWERKER, verdeelGegevens.reden);
        });
        indexeerService.indexeerDirect(verdeelGegevens.uuids.stream().map(UUID::toString).toList(),
                                       ZoekObjectType.ZAAK);
    }

    @PATCH
    @Path("/zaak/{uuid}/afbreken")
    public void afbreken(@PathParam("uuid") final UUID zaakUUID, final RESTZaakAfbrekenGegevens afbrekenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Statustype statustype = zaak.getStatus() != null ? ztcClientService.readStatustype(
                zrcClientService.readStatus(zaak.getStatus()).getStatustype()) : null;
        assertPolicy(zaak.isOpen() && !isHeropend(statustype) && policyService.readZaakRechten(zaak)
                .getAfbreken() && !zrcClientService.heeftOpenDeelzaken(
                zaak) && !enkelvoudigInformatieObjectLockService.hasLockedInformatieobjecten(zaak));
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                UriUtil.uuidFromURI(zaak.getZaaktype()));
        final ZaakbeeindigParameter zaakbeeindigParameter = zaakafhandelParameters.readZaakbeeindigParameter(
                afbrekenGegevens.zaakbeeindigRedenId);
        zgwApiService.createResultaatForZaak(zaak, zaakbeeindigParameter.getResultaattype(),
                                             zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        zgwApiService.endZaak(zaak, zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        // Terminate case after the zaak is ended in order to prevent the EndCaseLifecycleListener from ending the zaak.
        cmmnService.terminateCase(zaakUUID);
    }

    @PATCH
    @Path("/zaak/{uuid}/heropenen")
    public void heropenen(@PathParam("uuid") final UUID zaakUUID, final RESTZaakHeropenenGegevens heropenenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(!zaak.isOpen() && policyService.readZaakRechten(zaak).getHeropenen());
        zgwApiService.createStatusForZaak(zaak, STATUSTYPE_OMSCHRIJVING_HEROPEND, heropenenGegevens.reden);
    }

    @PATCH
    @Path("/zaak/{uuid}/afsluiten")
    public void afsluiten(@PathParam("uuid") final UUID zaakUUID, final RESTZaakAfsluitenGegevens afsluitenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak)
                .getBehandelen());
        if (zrcClientService.heeftOpenDeelzaken(zaak)) {
            throw new FoutmeldingException("Deze hoofdzaak heeft open deelzaken en kan niet afgesloten worden.");
        }
        zgwApiService.updateResultaatForZaak(zaak, afsluitenGegevens.resultaattypeUuid, afsluitenGegevens.reden);
        zgwApiService.closeZaak(zaak, afsluitenGegevens.reden);
    }

    @PATCH
    @Path("/zaak/koppel")
    public void koppelZaak(final RESTZaakKoppelGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUuid);
        final Zaak teKoppelenZaak = zrcClientService.readZaak(gegevens.teKoppelenZaakUuid);
        assertPolicy(policyService.readZaakRechten(zaak).getWijzigen() &&
                             policyService.readZaakRechten(teKoppelenZaak).getWijzigen());

        switch (gegevens.relatieType) {
            case HOOFDZAAK -> koppelHoofdEnDeelzaak(teKoppelenZaak, zaak);
            case DEELZAAK -> koppelHoofdEnDeelzaak(zaak, teKoppelenZaak);
            case VERVOLG -> koppelRelevantezaken(zaak, teKoppelenZaak, AardRelatie.VERVOLG);
            case ONDERWERP -> koppelRelevantezaken(zaak, teKoppelenZaak, AardRelatie.ONDERWERP);
            case BIJDRAGE -> koppelRelevantezaken(zaak, teKoppelenZaak, AardRelatie.BIJDRAGE);
        }
        if (gegevens.reverseRelatieType != null) {
            switch (gegevens.reverseRelatieType) {
                case VERVOLG -> koppelRelevantezaken(teKoppelenZaak, zaak, AardRelatie.VERVOLG);
                case ONDERWERP -> koppelRelevantezaken(teKoppelenZaak, zaak, AardRelatie.ONDERWERP);
                case BIJDRAGE -> koppelRelevantezaken(teKoppelenZaak, zaak, AardRelatie.BIJDRAGE);
            }
        }
    }

    @PATCH
    @Path("/zaak/ontkoppel")
    public void ontkoppelZaak(final RESTZaakOntkoppelGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUuid);
        final Zaak gekoppeldeZaak = zrcClientService.readZaakByID(gegevens.gekoppeldeZaakIdentificatie);
        assertPolicy(policyService.readZaakRechten(zaak).getWijzigen() &&
                             policyService.readZaakRechten(gekoppeldeZaak).getWijzigen());

        switch (gegevens.relatietype) {
            case HOOFDZAAK -> ontkoppelHoofdEnDeelzaak(gekoppeldeZaak, zaak, gegevens.reden);
            case DEELZAAK -> ontkoppelHoofdEnDeelzaak(zaak, gekoppeldeZaak, gegevens.reden);
            case VERVOLG -> ontkoppelRelevantezaken(zaak, gekoppeldeZaak, AardRelatie.VERVOLG, gegevens.reden);
            case ONDERWERP -> ontkoppelRelevantezaken(zaak, gekoppeldeZaak, AardRelatie.ONDERWERP, gegevens.reden);
            case BIJDRAGE -> ontkoppelRelevantezaken(zaak, gekoppeldeZaak, AardRelatie.BIJDRAGE, gegevens.reden);
        }
    }

    @PUT
    @Path("toekennen/mij")
    public RESTZaak toekennenAanIngelogdeMedewerker(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(toekennenGegevens);
        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID zaakUUID) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getLezen());
        final List<AuditTrailRegel> auditTrail = zrcClientService.listAuditTrail(zaakUUID);
        return auditTrailConverter.convert(auditTrail);
    }

    @GET
    @Path("zaak/{uuid}/betrokkene")
    public List<RESTZaakBetrokkene> listBetrokkenenVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getLezen());
        return zaakBetrokkeneConverter.convert(
                zrcClientService.listRollen(zaak).stream()
                        .filter(rol -> KlantenRESTService.betrokkenen.contains(
                                AardVanRol.fromValue(rol.getOmschrijvingGeneriek()))));
    }

    /**
     * Retrieve all possible afzenders for a zaak
     *
     * @param zaakUUID the id of the zaak
     * @return list of afzenders
     */
    @GET
    @Path("zaak/{uuid}/afzender")
    public List<RESTZaakAfzender> listAfzendersVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return sortAndRemoveDuplicates(
                resolveZaakAfzenderMail(
                        zaakAfzenderConverter.convertZaakAfzenders(
                                zaakafhandelParameterService.readZaakafhandelParameters(
                                                UriUtil.uuidFromURI(zaak.getZaaktype()))
                                        .getZaakAfzenders()).stream()));
    }

    /**
     * Retrieve the default afzender for a zaak
     *
     * @param zaakUUID the id of the zaak
     * @return the default zaakafzender or null if no default is available
     */
    @GET
    @Path("zaak/{uuid}/afzender/default")
    public RESTZaakAfzender readDefaultAfzenderVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        return resolveZaakAfzenderMail(
                zaakafhandelParameterService.readZaakafhandelParameters(
                                UriUtil.uuidFromURI(zaak.getZaaktype()))
                        .getZaakAfzenders().stream()
                        .filter(ZaakAfzender::isDefault)
                        .map(zaakAfzenderConverter::convertZaakAfzender))
                .findAny()
                .orElse(null);
    }

    private Stream<RESTZaakAfzender> resolveZaakAfzenderMail(final Stream<RESTZaakAfzender> afzenders) {
        return afzenders
                .peek(afzender -> {
                    final ZaakAfzender.Speciaal speciaal = speciaalMail(afzender.mail);
                    if (speciaal != null) {
                        afzender.suffix = "gegevens.mail.afzender." + speciaal;
                        afzender.mail = resolveMail(speciaal, afzender.mail);
                    }
                    afzender.replyTo = resolveMail(speciaalMail(afzender.replyTo), afzender.replyTo);
                })
                .filter(afzender -> afzender.mail != null);
    }

    private ZaakAfzender.Speciaal speciaalMail(final String mail) {
        if (mail != null && !mail.contains("@")) {
            return ZaakAfzender.Speciaal.valueOf(mail);
        }
        return null;
    }

    private String resolveMail(ZaakAfzender.Speciaal speciaal, final String mail) {
        if (speciaal != null) {
            return switch (speciaal) {
                case GEMEENTE -> configuratieService.readGemeenteMail();
                case MEDEWERKER -> loggedInUserInstance.get().getEmail();
            };
        }
        return mail;
    }

    private static List<RESTZaakAfzender> sortAndRemoveDuplicates(Stream<RESTZaakAfzender> afzenders) {
        final List<RESTZaakAfzender> list = afzenders
                .sorted((a, b) -> {
                    final int result = a.mail.compareTo(b.mail);
                    return result == 0 ? a.defaultMail ? -1 : 0 : result;
                })
                .collect(Collectors.toList());
        final Iterator<RESTZaakAfzender> i = list.iterator();
        String previous = null;
        while (i.hasNext()) {
            final RESTZaakAfzender afzender = i.next();
            if (afzender.mail.equals(previous)) {
                i.remove();
            } else {
                previous = afzender.mail;
            }
        }
        return list;
    }

    @GET
    @Path("communicatiekanalen/{inclusiefEFormulier}")
    public List<RESTCommunicatiekanaal> listCommunicatiekanalen(
            @PathParam("inclusiefEFormulier") final boolean inclusiefEFormulier) {
        final List<CommunicatieKanaal> communicatieKanalen = vrlClientService.listCommunicatiekanalen();
        if (!inclusiefEFormulier) {
            communicatieKanalen.removeIf(
                    communicatieKanaal -> communicatieKanaal.getNaam().equals(COMMUNICATIEKANAAL_EFORMULIER));
        }
        return communicatiekanaalConverter.convert(communicatieKanalen);
    }

    @GET
    @Path("besluit/zaakUuid/{zaakUuid}")
    public List<RESTBesluit> listBesluitenForZaakUUID(@PathParam("zaakUuid") final UUID zaakUuid) {
        return brcClientService.listBesluiten(zrcClientService.readZaak(zaakUuid))
                .map(besluitConverter::convertToRESTBesluit)
                .orElse(null);
    }

    @POST
    @Path("besluit")
    public RESTBesluit createBesluit(final RESTBesluitVastleggenGegevens besluitToevoegenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(besluitToevoegenGegevens.zaakUuid);
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final Status zaakStatus = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype zaakStatustype = zaakStatus != null ? ztcClientService.readStatustype(
                zaakStatus.getStatustype()) : null;
        assertPolicy(zaak.isOpen() && isNotEmpty(zaaktype.getBesluittypen()) &&
                             policyService.readZaakRechten(zaak, zaaktype).getBehandelen() &&
                             !isIntake(zaakStatustype));
        final Besluit besluit = besluitConverter.convertToBesluit(zaak, besluitToevoegenGegevens);
        if (zaak.getResultaat() != null) {
            zgwApiService.updateResultaatForZaak(zaak, besluitToevoegenGegevens.resultaattypeUuid, null);
        } else {
            zgwApiService.createResultaatForZaak(zaak, besluitToevoegenGegevens.resultaattypeUuid, null);
        }
        final RESTBesluit resultaat = besluitConverter.convertToRESTBesluit(brcClientService.createBesluit(besluit));
        besluitToevoegenGegevens.informatieobjecten.forEach(documentUri -> {
            final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(
                    documentUri);
            final BesluitInformatieobject besluitInformatieobject = new BesluitInformatieobject(resultaat.url,
                                                                                                informatieobject.getUrl());
            brcClientService.createBesluitInformatieobject(besluitInformatieobject, AANMAKEN_BESLUIT_TOELICHTING);
        });
        // This event should result from a ZAAKBESLUIT CREATED notification on the ZAKEN channel
        // but open_zaak does not send that one, so emulate it here.
        eventingService.send(ScreenEventType.ZAAK_BESLUITEN.updated(zaak));
        return resultaat;
    }

    @PUT
    @Path("besluit")
    public RESTBesluit updateBesluit(final RESTBesluitWijzigenGegevens restBesluitWijzigenGegevens) {
        Besluit besluit = brcClientService.readBesluit(restBesluitWijzigenGegevens.besluitUuid);
        final Zaak zaak = zrcClientService.readZaak(besluit.getZaak());
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getBehandelen());
        besluit = besluitConverter.convertToBesluit(besluit, restBesluitWijzigenGegevens);
        besluit = brcClientService.updateBesluit(besluit, restBesluitWijzigenGegevens.reden);
        if (zaak.getResultaat() != null) {
            final Resultaat zaakResultaat = zrcClientService.readResultaat(zaak.getResultaat());
            final Resultaattype resultaattype = ztcClientService.readResultaattype(
                    restBesluitWijzigenGegevens.resultaattypeUuid);
            if (!UriUtil.equal(zaakResultaat.getResultaattype(), resultaattype.getUrl())) {
                zrcClientService.deleteResultaat(zaakResultaat.getUuid());
                zgwApiService.createResultaatForZaak(zaak, restBesluitWijzigenGegevens.resultaattypeUuid, null);
            }
        }
        updateBesluitInformatieobjecten(besluit, restBesluitWijzigenGegevens.informatieobjecten);
        // This event should result from a ZAAKBESLUIT CREATED notification on the ZAKEN channel
        // but open_zaak does not send that one, so emulate it here.
        eventingService.send(ScreenEventType.ZAAK_BESLUITEN.updated(zaak));
        return besluitConverter.convertToRESTBesluit(besluit);
    }

    private void updateBesluitInformatieobjecten(final Besluit besluit, final List<UUID> nieuweDocumenten) {
        final List<BesluitInformatieobject> besluitInformatieobjecten = brcClientService.listBesluitInformatieobjecten(
                besluit.getUrl());
        final List<UUID> huidigeDocumenten = besluitInformatieobjecten.stream()
                .map(besluitInformatieobject -> UriUtil.uuidFromURI(besluitInformatieobject.getInformatieobject()))
                .toList();

        final Collection<UUID> verwijderen = CollectionUtils.subtract(huidigeDocumenten, nieuweDocumenten);
        final Collection<UUID> toevoegen = CollectionUtils.subtract(nieuweDocumenten, huidigeDocumenten);

        verwijderen.forEach(teVerwijderenInformatieobject -> besluitInformatieobjecten.stream()
                .filter(besluitInformatieobject -> uuidFromURI(besluitInformatieobject.getInformatieobject()).equals(
                        teVerwijderenInformatieobject))
                .forEach(besluitInformatieobject -> brcClientService.deleteBesluitinformatieobject(
                        uuidFromURI(besluitInformatieobject.getUrl()))));

        toevoegen.forEach(documentUri -> {
            final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(
                    documentUri);
            final BesluitInformatieobject besluitInformatieobject = new BesluitInformatieobject(besluit.getUrl(),
                                                                                                informatieobject.getUrl());
            brcClientService.createBesluitInformatieobject(besluitInformatieobject, WIJZIGEN_BESLUIT_TOELICHTING);
        });
    }

    @PUT
    @Path("besluit/intrekken")
    public RESTBesluit intrekkenBesluit(final RESTBesluitIntrekkenGegevens restBesluitIntrekkenGegevens) {
        Besluit besluit = brcClientService.readBesluit(restBesluitIntrekkenGegevens.besluitUuid);
        final Zaak zaak = zrcClientService.readZaak(besluit.getZaak());
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getBehandelen());
        besluit = besluitConverter.convertToBesluit(besluit, restBesluitIntrekkenGegevens);
        besluit = brcClientService.updateBesluit(besluit, getIntrekToelichting(besluit.getVervalreden())
                .formatted(restBesluitIntrekkenGegevens.reden));
        // This event should result from a ZAAKBESLUIT UPDATED notification on the ZAKEN channel
        // but open_zaak does not send that one, so emulate it here.
        eventingService.send(ScreenEventType.ZAAK_BESLUITEN.updated(zaak));
        return besluitConverter.convertToRESTBesluit(besluit);
    }

    private String getIntrekToelichting(final Vervalreden vervalreden) {
        return switch (vervalreden) {
            case INGETROKKEN_OVERHEID -> "Overheid: %s";
            case INGETROKKEN_BELANGHEBBENDE -> "Belanghebbende: %s";
            default -> null;
        };
    }

    @GET
    @Path("besluit/{uuid}/historie")
    public List<RESTHistorieRegel> listBesluitHistorie(@PathParam("uuid") final UUID uuid) {
        final List<AuditTrailRegel> auditTrail = brcClientService.listAuditTrail(uuid);
        return auditTrailConverter.convert(auditTrail);
    }

    @GET
    @Path("besluittypes/{zaaktypeUUID}")
    public List<RESTBesluittype> listBesluittypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken());
        final List<Besluittype> besluittypen = ztcClientService.readBesluittypen(
                        ztcClientService.readZaaktype(zaaktypeUUID).getUrl()).stream()
                .filter(LocalDateUtil::dateNowIsBetween)
                .toList();
        return besluittypeConverter.convertToRESTBesluittypes(besluittypen);
    }

    @GET
    @Path("resultaattypes/{zaaktypeUUID}")
    public List<RESTResultaattype> listResultaattypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        assertPolicy(policyService.readWerklijstRechten().getZakenTaken());
        return resultaattypeConverter.convertResultaattypes(
                ztcClientService.readResultaattypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    @GET
    @Path("{uuid}/procesdiagram")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadProcessDiagram(@PathParam("uuid") final UUID uuid) {
        return Response.ok(bpmnService.getProcessDiagram(uuid))
                .header("Content-Disposition",
                        "attachment; filename=\"procesdiagram.gif\"")
                .build();
    }

    private Zaak ingelogdeMedewerkerToekennenAanZaak(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getToekennen());

        final User user = identityService.readUser(loggedInUserInstance.get().getId());
        zrcClientService.updateRol(zaak, bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
        return zaak;
    }

    private RolOrganisatorischeEenheid bepaalRolGroep(final Group group, final Zaak zaak) {
        final OrganisatorischeEenheid groep = new OrganisatorischeEenheid();
        groep.setIdentificatie(group.getId());
        groep.setNaam(group.getName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolOrganisatorischeEenheid(zaak.getUrl(), roltype, "Behandelend groep van de zaak", groep);
    }

    private RolMedewerker bepaalRolMedewerker(final User user, final Zaak zaak) {
        final net.atos.client.zgw.zrc.model.Medewerker medewerker = new net.atos.client.zgw.zrc.model.Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolMedewerker(zaak.getUrl(), roltype, "Behandelaar van de zaak", medewerker);
    }

    private void deleteSignaleringen(final Zaak zaak) {
        signaleringenService.deleteSignaleringen(
                new SignaleringZoekParameters(loggedInUserInstance.get())
                        .types(SignaleringType.Type.ZAAK_OP_NAAM,
                               SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD)
                        .subject(zaak));
    }

    private void removeInitiator(final Zaak zaak, final Rol<?> initiator, final String reden) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        zrcClientService.deleteRol(initiator, reden);
    }

    private void addInitiator(final IdentificatieType identificatieType, final String identificatie, final Zaak zaak) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaak.getZaaktype());
        switch (identificatieType) {
            case BSN -> addBetrokkenNatuurlijkPersoon(initiator, identificatie, zaak, ROL_TOEVOEGEN_REDEN);
            case VN -> addBetrokkenVestiging(initiator, identificatie, zaak, ROL_TOEVOEGEN_REDEN);
            case RSIN -> addBetrokkenNietNatuurlijkPersoon(initiator, identificatie, zaak, ROL_TOEVOEGEN_REDEN);
            default -> throw new IllegalStateException(
                    String.format("Unexpected value: %s '%s'", identificatieType, identificatie));
        }
    }

    private void removeBetrokkene(final Zaak zaak, final Rol<?> betrokkene, final String reden) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        zrcClientService.deleteRol(betrokkene, reden);
    }

    private void addBetrokkene(final UUID roltype, IdentificatieType identificatieType, final String identificatie,
            final String toelichting, final Zaak zaak) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final Roltype betrokkene = ztcClientService.readRoltype(roltype);
        switch (identificatieType) {
            case BSN -> addBetrokkenNatuurlijkPersoon(betrokkene, identificatie, zaak, toelichting);
            case VN -> addBetrokkenVestiging(betrokkene, identificatie, zaak, toelichting);
            case RSIN -> addBetrokkenNietNatuurlijkPersoon(betrokkene, identificatie, zaak, toelichting);
            default -> throw new IllegalStateException(
                    String.format("Unexpected value: %s '%s'", identificatieType, identificatie));
        }
    }

    private void addBetrokkenNatuurlijkPersoon(final Roltype roltype, final String bsn, final Zaak zaak,
            String toelichting) {
        final RolNatuurlijkPersoon rol = new RolNatuurlijkPersoon(zaak.getUrl(), roltype, toelichting,
                                                                  new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenVestiging(final Roltype roltype, final String vestigingsnummer, final Zaak zaak,
            String toelichting) {
        final RolVestiging rol = new RolVestiging(zaak.getUrl(), roltype, toelichting,
                                                  new Vestiging(vestigingsnummer));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenNietNatuurlijkPersoon(final Roltype roltype, final String rsin, final Zaak zaak,
            String toelichting) {
        final RolNietNatuurlijkPersoon rol = new RolNietNatuurlijkPersoon(zaak.getUrl(), roltype, toelichting,
                                                                          new NietNatuurlijkPersoon(rsin));
        zrcClientService.createRol(rol, toelichting);
    }

    private int verlengOpenTaken(final UUID zaakUUID, final int duurDagen) {
        final int[] count = new int[1];
        takenService.listOpenTasksForZaak(zaakUUID).stream()
                .filter(task -> task.getDueDate() != null)
                .forEach(task -> {
                    task.setDueDate(convertToDate(convertToLocalDate(task.getDueDate()).plusDays(duurDagen)));
                    takenService.updateTask(task);
                    eventingService.send(TAAK.updated(task));
                    count[0]++;
                });
        return count[0];
    }

    private void koppelHoofdEnDeelzaak(final Zaak hoofdZaak, final Zaak deelZaak) {
        final HoofdzaakZaakPatch zaakPatch = new HoofdzaakZaakPatch(hoofdZaak.getUrl());
        zrcClientService.patchZaak(deelZaak.getUuid(), zaakPatch);
        // Hiervoor wordt door open zaak alleen voor de deelzaak een notificatie verstuurd.
        // Dus zelf het ScreenEvent versturen voor de hoofdzaak!
        indexeerService.addOrUpdateZaak(hoofdZaak.getUuid(), false);
        eventingService.send(ZAAK.updated(hoofdZaak.getUuid()));
    }

    private void ontkoppelHoofdEnDeelzaak(final Zaak hoofdZaak, final Zaak deelZaak, final String reden) {
        final HoofdzaakZaakPatch zaakPatch = new HoofdzaakZaakPatch(null);
        zrcClientService.patchZaak(deelZaak.getUuid(), zaakPatch, reden);
        // Hiervoor wordt door open zaak alleen voor de deelzaak een notificatie verstuurd.
        // Dus zelf het ScreenEvent versturen voor de hoofdzaak!
        indexeerService.addOrUpdateZaak(hoofdZaak.getUuid(), false);
        eventingService.send(ZAAK.updated(hoofdZaak.getUuid()));
    }

    private void koppelRelevantezaken(final Zaak zaak, final Zaak andereZaak, final AardRelatie aardRelatie) {
        final RelevantezaakZaakPatch zaakPatch = new RelevantezaakZaakPatch(
                addRelevanteZaak(zaak.getRelevanteAndereZaken(), andereZaak.getUrl(), aardRelatie));
        zrcClientService.patchZaak(zaak.getUuid(), zaakPatch);
    }

    private void ontkoppelRelevantezaken(final Zaak zaak, final Zaak andereZaak, final AardRelatie aardRelatie,
            final String reden) {
        final RelevantezaakZaakPatch zaakPatch = new RelevantezaakZaakPatch(
                removeRelevanteZaak(zaak.getRelevanteAndereZaken(), andereZaak.getUrl(), aardRelatie));
        zrcClientService.patchZaak(zaak.getUuid(), zaakPatch, reden);
    }

    private List<RelevanteZaak> addRelevanteZaak(final List<RelevanteZaak> relevanteZaken, URI andereZaak,
            final AardRelatie aardRelatie) {
        final RelevanteZaak relevanteZaak = new RelevanteZaak(andereZaak, aardRelatie);
        if (relevanteZaken != null) {
            if (relevanteZaken.stream()
                    .noneMatch(zaak -> zaak.is(andereZaak, aardRelatie))) {
                relevanteZaken.add(relevanteZaak);
            }
            return relevanteZaken;
        } else {
            return List.of(relevanteZaak);
        }
    }

    private List<RelevanteZaak> removeRelevanteZaak(final List<RelevanteZaak> relevanteZaken, URI andereZaak,
            final AardRelatie aardRelatie) {
        if (relevanteZaken != null) {
            relevanteZaken.removeAll(relevanteZaken.stream()
                                             .filter(zaak -> zaak.is(andereZaak, aardRelatie))
                                             .toList());
        }
        return relevanteZaken;
    }
}
