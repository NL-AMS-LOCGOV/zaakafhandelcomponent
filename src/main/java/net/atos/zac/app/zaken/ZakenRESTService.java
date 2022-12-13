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

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.BesluitInformatieobject;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.GeometryZaakPatch;
import net.atos.client.zgw.zrc.model.HoofdzaakZaakPatch;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.NietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
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
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.audit.converter.RESTHistorieRegelConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;
import net.atos.zac.app.klanten.KlantenRESTService;
import net.atos.zac.app.klanten.model.klant.IdentificatieType;
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
import net.atos.zac.app.zaken.model.RESTBesluitVastleggenGegevens;
import net.atos.zac.app.zaken.model.RESTBesluitWijzigenGegevens;
import net.atos.zac.app.zaken.model.RESTBesluittype;
import net.atos.zac.app.zaken.model.RESTCommunicatiekanaal;
import net.atos.zac.app.zaken.model.RESTDocumentOntkoppelGegevens;
import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakAfbrekenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakAfsluitenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkene;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkeneGegevens;
import net.atos.zac.app.zaken.model.RESTZaakEditMetRedenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakHeropenenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakKoppelGegevens;
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
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.CaseService;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.healthcheck.HealthCheckService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.util.LocalDateUtil;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
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

    private static final String INITIATOR_VERWIJDER_REDEN = "Initiator verwijderd door de medewerker tijdens het behandelen van de zaak";

    private static final String INITIATOR_TOEVOEGEN_REDEN = "Initiator toegekend door de medewerker tijdens het behandelen van de zaak";

    private static final String BETROKKENE_VERWIJDER_REDEN = "Betrokkene verwijderd door de medewerker tijdens het behandelen van de zaak";

    private static final String AANMAKEN_ZAAK_REDEN = "Aanmaken zaak";

    private static final String OPSCHORTING = "Opschorting";

    private static final String HERVATTING = "Hervatting";

    private static final String VERLENGING = "Verlenging";

    private static final String AANMAKEN_BESLUIT_TOELICHTING = "Aanmaken besluit";

    private static final String WIJZIGEN_BESLUIT_TOELICHTING = "Wijzigen besluit";

    @Inject
    private ZGWApiService zgwApiService;

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
    private CaseService caseService;

    @Inject
    private TaskService taskService;

    @Inject
    private CaseVariablesService caseVariablesService;

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
                .ifPresent(initiator -> removeInitiator(zaak, initiator));
        addInitiator(gegevens.betrokkeneIdentificatieType, gegevens.betrokkeneIdentificatie, zaak);
        return zaakConverter.convert(zaak);
    }

    @DELETE
    @Path("{uuid}/initiator")
    public RESTZaak deleteInitiator(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zgwApiService.findInitiatorForZaak(zaak)
                .ifPresent(initiator -> removeInitiator(zaak, initiator));
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
    public RESTZaak deleteBetrokkene(@PathParam("uuid") final UUID betrokkeneUUID) {
        final Rol<?> betrokkene = zrcClientService.readRol(betrokkeneUUID);
        final Zaak zaak = zrcClientService.readZaak(betrokkene.getZaak());
        removeBetrokkene(zaak, betrokkene);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("zaak")
    public RESTZaak createZaak(final RESTZaak restZaak) {
        assertPolicy(policyService.readOverigeRechten().getStartenZaak() && policyService.isZaaktypeAllowed(
                restZaak.zaaktype.omschrijving));
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
        caseService.startCase(zaak, zaaktype,
                              zaakafhandelParameterService.readZaakafhandelParameters(zaaktype.getUUID()), null);
        return zaakConverter.convert(zaak);
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
    @Path("{uuid}/zaakgeometrie")
    public RESTZaak updateZaakGeometrie(@PathParam("uuid") final UUID zaakUUID, final RESTZaak restZaak) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getWijzigen());
        final GeometryZaakPatch geometryZaakPatch = new GeometryZaakPatch(
                restGeometryConverter.convert(restZaak.zaakgeometrie));
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID, geometryZaakPatch);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("zaak/{uuid}/opschorting")
    public RESTZaak opschortenZaak(@PathParam("uuid") final UUID zaakUUID,
            final RESTZaakOpschortGegevens restZaakOpschortGegevens) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
        if (restZaakOpschortGegevens.indicatieOpschorting) {
            assertPolicy(zaak.isOpen() && !isHeropend(statustype) && !zaak.isOpgeschort() && StringUtils.isEmpty(
                    zaak.getOpschorting().getReden()));
        } else {
            assertPolicy(zaak.isOpgeschort());
        }
        final String toelichting = String.format("%s: %s", restZaakOpschortGegevens.indicatieOpschorting ?
                OPSCHORTING : HERVATTING, restZaakOpschortGegevens.redenOpschorting);
        final Zaak updatedZaak = zrcClientService.patchZaak(zaakUUID,
                                                            zaakConverter.convertToPatch(restZaakOpschortGegevens),
                                                            toelichting);
        if (restZaakOpschortGegevens.indicatieOpschorting) {
            caseVariablesService.setDatumtijdOpgeschort(zaakUUID, ZonedDateTime.now());
            caseVariablesService.setVerwachteDagenOpgeschort(zaakUUID, restZaakOpschortGegevens.duurDagen);
        } else {
            caseVariablesService.removeDatumtijdOpgeschort(zaakUUID);
            caseVariablesService.removeVerwachteDagenOpgeschort(zaakUUID);
        }
        return zaakConverter.convert(updatedZaak, status, statustype);
    }

    @GET
    @Path("zaak/{uuid}/opschorting")
    public RESTZaakOpschorting readOpschortingZaak(@PathParam("uuid") final UUID zaakUUID) {
        assertPolicy(policyService.readZaakRechten(zrcClientService.readZaak(zaakUUID)).getLezen());
        final RESTZaakOpschorting zaakOpschorting = new RESTZaakOpschorting();
        caseVariablesService.findDatumtijdOpgeschort(zaakUUID)
                .ifPresent(datumtijdOpgeschort -> zaakOpschorting.vanafDatumTijd = datumtijdOpgeschort);
        caseVariablesService.findVerwachteDagenOpgeschort(zaakUUID)
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
        final UUID zaaktypeUUID = URIUtil.parseUUIDFromResourceURI(zaak.getZaaktype());
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
        final List<Zaaktype> zaaktypen = ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI())
                .stream()
                .filter(zaaktype -> !zaaktype.getConcept())
                .filter(Zaaktype::isNuGeldig)
                .filter(zaaktype -> healthCheckService.controleerZaaktype(zaaktype.getUrl()).isValide())
                .toList();
        return policyService.filterAllowedZaaktypen(zaaktypen).stream().map(zaaktypeConverter::convert).toList();
    }

    @PUT
    @Path("zaakdata")
    public RESTZaak updateZaakdata(final RESTZaak restZaak) {
        final Zaak zaak = zrcClientService.readZaak(restZaak.uuid);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getWijzigen());

        caseVariablesService.setZaakdata(restZaak.uuid, restZaak.zaakdata);
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
                .getAfbreken() && !zrcClientService.heeftOpenDeelzaken(zaak));
        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                UriUtil.uuidFromURI(zaak.getZaaktype()));
        final ZaakbeeindigParameter zaakbeeindigParameter = zaakafhandelParameters.readZaakbeeindigParameter(
                afbrekenGegevens.zaakbeeindigRedenId);
        zgwApiService.createResultaatForZaak(zaak, zaakbeeindigParameter.getResultaattype(),
                                             zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        zgwApiService.endZaak(zaak, zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        // Terminate case after the zaak is ended in order to prevent the EndCaseLifecycleListener from ending the zaak.
        caseService.terminateCase(zaakUUID);
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
                .getBehandelen() && !zrcClientService.heeftOpenDeelzaken(zaak));
        zgwApiService.updateResultaatForZaak(zaak, afsluitenGegevens.resultaattypeUuid, afsluitenGegevens.reden);
        zgwApiService.closeZaak(zaak, afsluitenGegevens.reden);
    }

    @PATCH
    @Path("/zaak/koppel")
    public void koppelZaak(final RESTZaakKoppelGegevens zaakKoppelGegevens) {
        final Zaak teKoppelenZaak = zrcClientService.readZaak(zaakKoppelGegevens.bronZaakUuid);
        final Zaak koppelenAanZaak = zrcClientService.readZaakByID(zaakKoppelGegevens.identificatie);
        assertPolicy(policyService.readZaakRechten(teKoppelenZaak).getWijzigen() &&
                             policyService.readZaakRechten(koppelenAanZaak).getWijzigen());

        switch (zaakKoppelGegevens.relatieType) {
            case DEELZAAK -> koppelHoofdEnDeelzaak(koppelenAanZaak, teKoppelenZaak.getUuid());
            case HOOFDZAAK -> koppelHoofdEnDeelzaak(teKoppelenZaak, koppelenAanZaak.getUuid());
        }
    }

    @PATCH
    @Path("/zaak/ontkoppel")
    public void ontkoppelZaak(final RESTZaakOntkoppelGegevens zaakOntkoppelGegevens) {
        final Zaak ontkoppelenVanZaak = zrcClientService.readZaakByID(
                zaakOntkoppelGegevens.ontkoppelenVanZaakIdentificatie);
        final Zaak teOntkoppelenZaak = zrcClientService.readZaak(zaakOntkoppelGegevens.teOntkoppelenZaakUUID);
        assertPolicy(policyService.readZaakRechten(ontkoppelenVanZaak).getWijzigen() &&
                             policyService.readZaakRechten(teOntkoppelenZaak).getWijzigen());
        switch (zaakOntkoppelGegevens.zaakRelatietype) {
            case DEELZAAK -> ontkoppelHoofdEnDeelzaak(ontkoppelenVanZaak.getUuid(), teOntkoppelenZaak.getUuid(),
                                                      zaakOntkoppelGegevens.reden);
            case HOOFDZAAK -> ontkoppelHoofdEnDeelzaak(teOntkoppelenZaak.getUuid(), ontkoppelenVanZaak.getUuid(),
                                                       zaakOntkoppelGegevens.reden);
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

    @GET
    @Path("communicatiekanalen")
    public List<RESTCommunicatiekanaal> listCommunicatiekanalen() {
        final List<CommunicatieKanaal> communicatieKanalen = vrlClientService.listCommunicatiekanalen();
        communicatieKanalen.removeIf(
                communicatieKanaal -> communicatieKanaal.getNaam().equals(COMMUNICATIEKANAAL_EFORMULIER));
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
        return resultaat;
    }

    @PUT
    @Path("besluit")
    public RESTBesluit updateBesluit(final RESTBesluitWijzigenGegevens restBesluitWijzgenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(restBesluitWijzgenGegevens.zaakUuid);
        assertPolicy(zaak.isOpen() && policyService.readZaakRechten(zaak).getBehandelen());
        Besluit besluit = brcClientService.readBesluit(restBesluitWijzgenGegevens.besluitUuid);
        besluit.setToelichting(restBesluitWijzgenGegevens.toelichting);
        besluit.setIngangsdatum(restBesluitWijzgenGegevens.ingangsdatum);
        besluit.setVervaldatum(restBesluitWijzgenGegevens.vervaldatum);
        besluit = brcClientService.updateBesluit(besluit, restBesluitWijzgenGegevens.reden);
        if (zaak.getResultaat() != null) {
            final Resultaat zaakResultaat = zrcClientService.readResultaat(zaak.getResultaat());
            final Resultaattype resultaattype = ztcClientService.readResultaattype(
                    restBesluitWijzgenGegevens.resultaattypeUuid);
            if (!UriUtil.equal(zaakResultaat.getResultaattype(), resultaattype.getUrl())) {
                zrcClientService.deleteResultaat(zaakResultaat.getUuid());
                zgwApiService.createResultaatForZaak(zaak, restBesluitWijzgenGegevens.resultaattypeUuid, null);
            }
        }
        updateBesluitInformatieobjecten(besluit, restBesluitWijzgenGegevens.informatieobjecten);
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
        final List<Besluittype> besluittypen = ztcClientService.readBesluittypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()).stream()
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
        return new RolOrganisatorischeEenheid(zaak.getUrl(), roltype.getUrl(), "groep", groep);
    }

    private RolMedewerker bepaalRolMedewerker(final User user, final Zaak zaak) {
        final net.atos.client.zgw.zrc.model.Medewerker medewerker = new net.atos.client.zgw.zrc.model.Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolMedewerker(zaak.getUrl(), roltype.getUrl(), "behandelaar", medewerker);
    }

    private void deleteSignaleringen(final Zaak zaak) {
        signaleringenService.deleteSignaleringen(
                new SignaleringZoekParameters(loggedInUserInstance.get())
                        .types(SignaleringType.Type.ZAAK_OP_NAAM,
                               SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD)
                        .subject(zaak));
    }

    private void removeInitiator(final Zaak zaak, final Rol<?> initiator) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        zrcClientService.deleteRol(initiator.getUuid(), INITIATOR_VERWIJDER_REDEN);
    }

    private void addInitiator(final IdentificatieType identificatieType, final String identificatie, final Zaak zaak) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaak.getZaaktype());
        switch (identificatieType) {
            case BSN -> addBetrokkenNatuurlijkPersoon(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            case VN -> addBetrokkenVestiging(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            case RSIN -> addBetrokkenNietNatuurlijkPersoon(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            default -> throw new IllegalStateException(
                    String.format("Unexpected value: %s '%s'", identificatieType, identificatie));
        }
    }

    private void removeBetrokkene(final Zaak zaak, final Rol<?> betrokkene) {
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        zrcClientService.deleteRol(betrokkene.getUuid(), BETROKKENE_VERWIJDER_REDEN);
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
        final RolNatuurlijkPersoon rol = new RolNatuurlijkPersoon(zaak.getUrl(), roltype.getUrl(), toelichting,
                                                                  new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenVestiging(final Roltype roltype, final String vestigingsnummer, final Zaak zaak,
            String toelichting) {
        final RolVestiging rol = new RolVestiging(zaak.getUrl(), roltype.getUrl(), toelichting,
                                                  new Vestiging(vestigingsnummer));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenNietNatuurlijkPersoon(final Roltype roltype, final String rsin, final Zaak zaak,
            String toelichting) {
        final RolNietNatuurlijkPersoon rol = new RolNietNatuurlijkPersoon(zaak.getUrl(), roltype.getUrl(), toelichting,
                                                                          new NietNatuurlijkPersoon(rsin));
        zrcClientService.createRol(rol, toelichting);
    }

    private int verlengOpenTaken(final UUID zaakUUID, final int duurDagen) {
        final int[] count = new int[1];
        taskService.listOpenTasks(zaakUUID).stream()
                .filter(task -> task.getDueDate() != null)
                .forEach(task -> {
                    task.setDueDate(convertToDate(convertToLocalDate(task.getDueDate()).plusDays(duurDagen)));
                    taskService.updateTask(task);
                    eventingService.send(TAAK.updated(task));
                    count[0]++;
                });
        return count[0];
    }

    private void koppelHoofdEnDeelzaak(final Zaak hoofdzaak, final UUID deelzaakUUID) {
        final HoofdzaakZaakPatch deelzaakPatch = new HoofdzaakZaakPatch(hoofdzaak.getUrl());
        zrcClientService.patchZaak(deelzaakUUID, deelzaakPatch);
        System.out.println("koppelHoofdEnDeelzaak ZAAK.updated " + hoofdzaak.getUuid());
        // Hiervoor wordt door open zaak alleen voor de deelzaak een notificatie verstuurd.
        // Dus zelf het ScreenEvent versturen voor de hoofdzaak!
        indexeerService.addZaak(hoofdzaak.getUuid(), false);
        eventingService.send(ZAAK.updated(hoofdzaak.getUuid()));
    }

    private void ontkoppelHoofdEnDeelzaak(final UUID deelzaakUUID, final UUID hoofdzaakUUID, final String reden) {
        final HoofdzaakZaakPatch deelzaakPatch = new HoofdzaakZaakPatch(null);
        zrcClientService.patchZaak(deelzaakUUID, deelzaakPatch, reden);
        // Hiervoor wordt door open zaak alleen voor de deelzaak een notificatie verstuurd.
        // Dus zelf het ScreenEvent versturen voor de hoofdzaak!
        indexeerService.addZaak(hoofdzaakUUID, false);
        eventingService.send(ZAAK.updated(hoofdzaakUUID));
    }
}
