/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import static net.atos.zac.configuratie.ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER;
import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;
import static net.atos.zac.policy.PolicyService.assertActie;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

import net.atos.client.zgw.brc.model.BesluitInformatieobject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.HoofdzaakPatch;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.NietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolNietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.client.zgw.zrc.model.Vestiging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakGeometriePatch;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
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
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
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

    private static final String AANMAKEN_BESLUIT = "Aanmaken besluit";

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
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

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
        assertActie(restZaak.acties.lezen);
        deleteSignaleringen(zaak);
        return restZaak;
    }

    @GET
    @Path("zaak/id/{identificatie}")
    public RESTZaak readZaakById(@PathParam("identificatie") final String identificatie) {
        final Zaak zaak = zrcClientService.readZaakByID(identificatie);
        final RESTZaak restZaak = zaakConverter.convert(zaak);
        assertActie(restZaak.acties.lezen);
        deleteSignaleringen(zaak);
        return restZaak;
    }

    @PUT
    @Path("initiator")
    public RESTZaak updateInitiator(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        final Rol<?> initiator = zgwApiService.findInitiatorForZaak(zaak);
        if (initiator != null) {
            removeInitiator(zaak, initiator);
        }
        addInitiator(gegevens.betrokkeneIdentificatieType, gegevens.betrokkeneIdentificatie, zaak);
        return zaakConverter.convert(zaak);
    }

    @DELETE
    @Path("{uuid}/initiator")
    public RESTZaak deleteInitiator(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Rol<?> initiator = zgwApiService.findInitiatorForZaak(zaak);
        removeInitiator(zaak, initiator);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("betrokkene")
    public RESTZaak createBetrokken(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        addBetrokkene(gegevens.roltypeUUID, gegevens.betrokkeneIdentificatieType, gegevens.betrokkeneIdentificatie, gegevens.roltoelichting, zaak);
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
        assertActie(policyService.readAppActies().getAanmakenZaak() && policyService.isZaaktypeAllowed(restZaak.zaaktype.omschrijving));
        restZaak.registratiedatum = LocalDate.now();
        final Zaak zaak = zaakConverter.convert(restZaak);
        final Zaak nieuweZaak = zgwApiService.createZaak(zaak);
        if (StringUtils.isNotEmpty(restZaak.initiatorIdentificatie)) {
            addInitiator(restZaak.initiatorIdentificatieType, restZaak.initiatorIdentificatie, nieuweZaak);
        }
        if (restZaak.groep != null) {
            final Group group = identityService.readGroup(restZaak.groep.id);
            zrcClientService.updateRol(nieuweZaak.getUuid(), bepaalRolGroep(group, nieuweZaak), AANMAKEN_ZAAK_REDEN);
        }
        if (restZaak.behandelaar != null) {
            final User user = identityService.readUser(restZaak.behandelaar.id);
            zrcClientService.updateRol(nieuweZaak.getUuid(), bepaalRolMedewerker(user, nieuweZaak), AANMAKEN_ZAAK_REDEN);
        }
        return zaakConverter.convert(nieuweZaak);
    }

    @PATCH
    @Path("zaak/{uuid}")
    public RESTZaak updateZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakEditMetRedenGegevens restZaakEditMetRedenGegevens) {
        assertActie(policyService.readZaakActies(zaakUUID).getWijzigenOverig());
        final Zaak updatedZaak = zrcClientService.updateZaak(zaakUUID, zaakConverter.convertToPatch(restZaakEditMetRedenGegevens.zaak),
                                                             restZaakEditMetRedenGegevens.reden);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("{uuid}/zaakgeometrie")
    public RESTZaak updateZaakGeometrie(@PathParam("uuid") final UUID zaakUUID, final RESTZaak restZaak) {
        assertActie(policyService.readZaakActies(zaakUUID).getWijzigenOverig());
        final ZaakGeometriePatch zaakGeometriePatch = new ZaakGeometriePatch(restGeometryConverter.convert(restZaak.zaakgeometrie));
        final Zaak updatedZaak = zrcClientService.updateZaak(zaakUUID, zaakGeometriePatch);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("zaak/{uuid}/opschorting")
    public RESTZaak opschortenZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakOpschortGegevens restZaakOpschortGegevens) {
        if (restZaakOpschortGegevens.indicatieOpschorting) {
            assertActie(policyService.readZaakActies(zaakUUID).getOpschorten());
        } else {
            assertActie(policyService.readZaakActies(zaakUUID).getHervatten());
        }
        final String toelichting = String.format("%s: %s", restZaakOpschortGegevens.indicatieOpschorting ?
                OPSCHORTING : HERVATTING, restZaakOpschortGegevens.redenOpschorting);
        final Zaak updatedZaak = zrcClientService.updateZaak(zaakUUID, zaakConverter.convertToPatch(restZaakOpschortGegevens), toelichting);
        if (restZaakOpschortGegevens.indicatieOpschorting) {
            caseVariablesService.setDatumtijdOpgeschort(zaakUUID, ZonedDateTime.now());
            caseVariablesService.setVerwachteDagenOpgeschort(zaakUUID, restZaakOpschortGegevens.duurDagen);
        } else {
            caseVariablesService.removeDatumtijdOpgeschort(zaakUUID);
            caseVariablesService.removeVerwachteDagenOpgeschort(zaakUUID);
        }
        return zaakConverter.convert(updatedZaak);
    }

    @GET
    @Path("zaak/{uuid}/opschorting")
    public RESTZaakOpschorting readOpschortingZaak(@PathParam("uuid") final UUID zaakUUID) {
        assertActie(policyService.readZaakActies(zaakUUID).getLezen());
        final RESTZaakOpschorting zaakOpschorting = new RESTZaakOpschorting();
        zaakOpschorting.vanafDatumTijd = caseVariablesService.findDatumtijdOpgeschort(zaakUUID);
        zaakOpschorting.duurDagen = caseVariablesService.findVerwachteDagenOpgeschort(zaakUUID);
        return zaakOpschorting;
    }

    @PATCH
    @Path("zaak/{uuid}/verlenging")
    public RESTZaak verlengenZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakVerlengGegevens restZaakVerlengGegevens) {
        assertActie(policyService.readZaakActies(zaakUUID).getVerlengen());
        final String toelichting = String.format("%s: %s", VERLENGING, restZaakVerlengGegevens.redenVerlenging);
        final Zaak updatedZaak = zrcClientService.updateZaak(zaakUUID, zaakConverter.convertToPatch(zaakUUID, restZaakVerlengGegevens), toelichting);
        if (restZaakVerlengGegevens.takenVerlengen) {
            final int aantalTakenVerlengd = verlengOpenTaken(zaakUUID, restZaakVerlengGegevens.duurDagen);
            if (aantalTakenVerlengd > 0) {
                eventingService.send(ZAAK_TAKEN.updated(updatedZaak));
            }
        }
        return zaakConverter.convert(updatedZaak);
    }

    @PUT
    @Path("zaakinformatieobjecten/ontkoppel")
    public void ontkoppelInformatieObject(final RESTDocumentOntkoppelGegevens ontkoppelGegevens) {
        final Zaak zaak = zrcClientService.readZaak(ontkoppelGegevens.zaakUUID);
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(ontkoppelGegevens.documentUUID);
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(informatieobject, zaak).getKoppelen());
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(informatieobject.getUrl());
        parameters.setZaak(zaak.getUrl());
        List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(parameters);
        if (zaakInformatieobjecten.isEmpty()) {
            throw new NotFoundException(
                    String.format("Geen ZaakInformatieobject gevonden voor Zaak: '%s' en InformatieObject: '%s'", ontkoppelGegevens.zaakUUID,
                                  ontkoppelGegevens.documentUUID));
        }
        zaakInformatieobjecten.forEach(zaakInformatieobject ->
                                               zrcClientService.deleteZaakInformatieobject(zaakInformatieobject.getUuid(),
                                                                                           ontkoppelGegevens.reden,
                                                                                           "Ontkoppeld"));
        if (zrcClientService.listZaakinformatieobjecten(informatieobject.getUrl()).isEmpty()) {
            ontkoppeldeDocumentenService.create(informatieobject, zaak, ontkoppelGegevens.reden);
        }
    }

    @GET
    @Path("waarschuwing")
    public List<RESTZaakOverzicht> listZaakWaarschuwingen() {
        final LocalDate vandaag = LocalDate.now();
        final Map<UUID, LocalDate> einddatumGeplandWaarschuwing = new HashMap<>();
        final Map<UUID, LocalDate> uiterlijkeEinddatumAfdoeningWaarschuwing = new HashMap<>();
        zaakafhandelParameterBeheerService.listZaakafhandelParameters().forEach(parameters -> {
            if (parameters.getEinddatumGeplandWaarschuwing() != null) {
                einddatumGeplandWaarschuwing.put(parameters.getZaakTypeUUID(),
                                                 datumWaarschuwing(vandaag, parameters.getEinddatumGeplandWaarschuwing()));
            }
            if (parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing() != null) {
                uiterlijkeEinddatumAfdoeningWaarschuwing.put(parameters.getZaakTypeUUID(),
                                                             datumWaarschuwing(vandaag, parameters.getUiterlijkeEinddatumAfdoeningWaarschuwing()));
            }
        });
        final ZaakListParameters zaakListParameters = new ZaakListParameters();
        zaakListParameters.setRolBetrokkeneIdentificatieMedewerkerIdentificatie(loggedInUserInstance.get().getId());
        return zrcClientService.listZaken(zaakListParameters).getResults().stream()
                .filter(Zaak::isOpen)
                .filter(zaak -> isWaarschuwing(zaak, vandaag, einddatumGeplandWaarschuwing, uiterlijkeEinddatumAfdoeningWaarschuwing))
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
                isWaarschuwing(vandaag, zaak.getUiterlijkeEinddatumAfdoening(), uiterlijkeEinddatumAfdoeningWaarschuwing.get(zaaktypeUUID));
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
        final List<Zaaktype> zaaktypen = ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI()).stream()
                .filter(zaaktype -> !zaaktype.getConcept())
                .filter(Zaaktype::isNuGeldig)
                .filter(zaaktype -> healthCheckService.controleerZaaktype(zaaktype.getUrl()).isValide())
                .toList();
        return policyService.filterAllowedZaaktypen(zaaktypen).stream().map(zaaktypeConverter::convert).toList();
    }

    @PUT
    @Path("toekennen")
    public RESTZaak toekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        assertActie(policyService.readZaakActies(zaak).getWijzigenToekenning());
        if (!StringUtils.isEmpty(toekennenGegevens.behandelaarGebruikersnaam)) {
            // Toekennen of overdragen
            final User user = identityService.readUser(toekennenGegevens.behandelaarGebruikersnaam);
            zrcClientService.updateRol(zaak.getUuid(), bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
        } else {
            // Vrijgeven
            zrcClientService.deleteRol(zaak.getUuid(), BetrokkeneType.MEDEWERKER, toekennenGegevens.reden);
        }
        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("verdelen")
    public void verdelen(final RESTZakenVerdeelGegevens verdeelGegevens) {
        assertActie(policyService.readZakenActies().getVerdelenEnVrijgeven());
        final Group group = !StringUtils.isEmpty(verdeelGegevens.groepId) ? identityService.readGroup(verdeelGegevens.groepId) : null;
        final User user = !StringUtils.isEmpty(verdeelGegevens.behandelaarGebruikersnaam) ?
                identityService.readUser(verdeelGegevens.behandelaarGebruikersnaam) : null;
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            if (group != null) {
                zrcClientService.updateRol(zaak.getUuid(), bepaalRolGroep(group, zaak), verdeelGegevens.reden);
            }
            if (user != null) {
                zrcClientService.updateRol(zaak.getUuid(), bepaalRolMedewerker(user, zaak), verdeelGegevens.reden);
            }
        });
        indexeerService.indexeerDirect(verdeelGegevens.uuids.stream().map(UUID::toString).collect(Collectors.toList()), ZoekObjectType.ZAAK);
    }

    @PUT
    @Path("vrijgeven")
    public void vrijgeven(final RESTZakenVerdeelGegevens verdeelGegevens) {
        assertActie(policyService.readZakenActies().getVerdelenEnVrijgeven());
        verdeelGegevens.uuids.forEach(uuid -> zrcClientService.deleteRol(uuid, BetrokkeneType.MEDEWERKER, verdeelGegevens.reden));
        indexeerService.indexeerDirect(verdeelGegevens.uuids.stream().map(UUID::toString).toList(), ZoekObjectType.ZAAK);
    }

    @PATCH
    @Path("/zaak/{uuid}/afbreken")
    public void afbreken(@PathParam("uuid") final UUID zaakUUID, final RESTZaakAfbrekenGegevens afbrekenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertActie(policyService.readZaakActies(zaak).getAfbreken());
        policyService.valideerAlleDeelzakenGesloten(zaak);
        final ZaakbeeindigParameter zaakbeeindigParameter = zaakafhandelParameterBeheerService.readZaakbeeindigParameter(
                uuidFromURI(zaak.getZaaktype()), afbrekenGegevens.zaakbeeindigRedenId);
        zgwApiService.createResultaatForZaak(zaak, zaakbeeindigParameter.getResultaattype(), zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        zgwApiService.endZaak(zaak, zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        // Terminate case after the zaak is ended in order to prevent the EndCaseLifecycleListener from ending the zaak.
        caseService.terminateCase(zaakUUID);
    }

    @PATCH
    @Path("/zaak/{uuid}/heropenen")
    public void heropenen(@PathParam("uuid") final UUID zaakUUID, final RESTZaakHeropenenGegevens heropenenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertActie(policyService.readZaakActies(zaakUUID).getHeropenen());
        zgwApiService.createStatusForZaak(zaak, STATUSTYPE_OMSCHRIJVING_HEROPEND, heropenenGegevens.reden);
    }

    @PATCH
    @Path("/zaak/{uuid}/afsluiten")
    public void afsluiten(@PathParam("uuid") final UUID zaakUUID, final RESTZaakAfsluitenGegevens afsluitenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertActie(policyService.readZaakActies(zaak).getAfsluiten());
        policyService.valideerAlleDeelzakenGesloten(zaak);
        zgwApiService.updateResultaatForZaak(zaak, afsluitenGegevens.resultaattypeUuid, afsluitenGegevens.reden);
        zgwApiService.closeZaak(zaak, afsluitenGegevens.reden);
    }

    @PATCH
    @Path("/zaak/koppel")
    public void koppelZaak(final RESTZaakKoppelGegevens zaakKoppelGegevens) {
        final Zaak teKoppelenZaak = zrcClientService.readZaak(zaakKoppelGegevens.bronZaakUuid);
        final Zaak koppelenAanZaak = zrcClientService.readZaakByID(zaakKoppelGegevens.identificatie);
        assertActie(policyService.readZaakActies(teKoppelenZaak).getKoppelen());
        assertActie(policyService.readZaakActies(koppelenAanZaak).getKoppelen());

        switch (zaakKoppelGegevens.relatieType) {
            case DEELZAAK -> koppelHoofdEnDeelzaak(koppelenAanZaak.getUuid(), teKoppelenZaak.getUuid());
            case HOOFDZAAK -> koppelHoofdEnDeelzaak(teKoppelenZaak.getUuid(), koppelenAanZaak.getUuid());
        }
    }

    @PATCH
    @Path("/zaak/ontkoppel")
    public void ontkoppelZaak(final RESTZaakOntkoppelGegevens zaakOntkoppelGegevens) {
        final Zaak ontkoppelenVanZaak = zrcClientService.readZaakByID(zaakOntkoppelGegevens.ontkoppelenVanZaakIdentificatie);
        assertActie(policyService.readZaakActies(ontkoppelenVanZaak).getKoppelen());
        assertActie(policyService.readZaakActies(zaakOntkoppelGegevens.teOntkoppelenZaakUUID).getKoppelen());
        switch (zaakOntkoppelGegevens.zaakRelatietype) {
            case DEELZAAK -> ontkoppelHoofdEnDeelzaak(ontkoppelenVanZaak.getUuid(),
                                                      zaakOntkoppelGegevens.teOntkoppelenZaakUUID, zaakOntkoppelGegevens.reden);
            case HOOFDZAAK -> ontkoppelHoofdEnDeelzaak(zaakOntkoppelGegevens.teOntkoppelenZaakUUID,
                                                       ontkoppelenVanZaak.getUuid(), zaakOntkoppelGegevens.reden);
        }
    }

    @PUT
    @Path("toekennen/mij")
    public RESTZaak toekennenAanIngelogdeMedewerker(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        assertActie(policyService.readZaakActies(zaak).getWijzigenToekenning());
        ingelogdeMedewerkerToekennenAanZaak(zaak, toekennenGegevens);
        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/mij/lijst")
    public RESTZaakOverzicht toekennenAanIngelogdeMedewerkerVanuitLijst(final RESTZaakToekennenGegevens toekennenGegevens) {
        assertActie(policyService.readZakenActies().getToekennenAanMijzelf());
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        ingelogdeMedewerkerToekennenAanZaak(zaak, toekennenGegevens);
        indexeerService.indexeerDirect(zaak.getUuid().toString(), ZoekObjectType.ZAAK);
        return zaakOverzichtConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/groep")
    public RESTZaak groepToekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        assertActie(policyService.readZaakActies(zaak).getWijzigenToekenning());
        final Group group = identityService.readGroup(toekennenGegevens.groepId);
        zrcClientService.updateRol(zaak.getUuid(), bepaalRolGroep(group, zaak), toekennenGegevens.reden);
        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID zaakUUID) {
        assertActie(policyService.readZaakActies(zaakUUID).getLezen());
        final List<AuditTrailRegel> auditTrail = zrcClientService.listAuditTrail(zaakUUID);
        return auditTrailConverter.convert(auditTrail);
    }

    @GET
    @Path("zaak/{uuid}/betrokkene")
    public List<RESTZaakBetrokkene> listBetrokkenenVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        assertActie(policyService.readZaakActies(zaakUUID).getLezen());
        return zaakBetrokkeneConverter.convert(
                zrcClientService.listRollen(zaakUUID).stream()
                        .filter(rol -> KlantenRESTService.betrokkenen.contains(AardVanRol.fromValue(rol.getOmschrijvingGeneriek()))));
    }

    @GET
    @Path("communicatiekanalen")
    public List<RESTCommunicatiekanaal> listCommunicatiekanalen() {
        final List<CommunicatieKanaal> communicatieKanalen = vrlClientService.listCommunicatiekanalen();
        communicatieKanalen.removeIf(communicatieKanaal -> communicatieKanaal.getNaam().equals(COMMUNICATIEKANAAL_EFORMULIER));
        return communicatiekanaalConverter.convert(communicatieKanalen);
    }

    @POST
    @Path("besluit")
    public RESTBesluit createBesluit(final RESTBesluitVastleggenGegevens besluitToevoegenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(besluitToevoegenGegevens.zaakUuid);
        assertActie(policyService.readZaakActies(zaak).getVastleggenBesluit());
        final Besluit besluit = besluitConverter.convertToBesluit(zaak, besluitToevoegenGegevens);
        zgwApiService.createResultaatForZaak(zaak, besluitToevoegenGegevens.resultaattypeUuid, StringUtils.EMPTY);
        final RESTBesluit resultaat = besluitConverter.convertToRESTBesluit(brcClientService.createBesluit(besluit));
        besluitToevoegenGegevens.informatieobjecten.forEach(documentUri -> {
            final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(documentUri);
            final BesluitInformatieobject besluitInformatieobject = new BesluitInformatieobject(resultaat.url, informatieobject.getUrl());
            brcClientService.createBesluitInformatieobject(besluitInformatieobject, AANMAKEN_BESLUIT);
        });
        return resultaat;
    }

    @GET
    @Path("listBesluitInformatieobjecten/{besluit}")
    public List<EnkelvoudigInformatieobject> listBesluitInformatieobjecten(@PathParam("besluit") final UUID besluit) {
        //TODO: Vervang UUID -> URI d.m.v. readBesluit voor URI-opbouw-methode van issue #1413
        final URI besluitUri = brcClientService.readBesluit(besluit).getUrl();
        return brcClientService.listBesluitInformatieobjecten(besluitUri).stream()
                .map(x -> drcClientService.readEnkelvoudigInformatieobject(x.getInformatieobject()))
                .collect(Collectors.toList());
    }

    @GET
    @Path("besluittypes/{zaaktypeUUID}")
    public List<RESTBesluittype> listBesluittypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        AppActies appActies = policyService.readAppActies();
        assertActie(appActies.getZaken());
        return besluittypeConverter.convertToRESTBesluittypes(ztcClientService.readBesluittypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    @GET
    @Path("resultaattypes/{zaaktypeUUID}")
    public List<RESTResultaattype> listResultaattypes(@PathParam("zaaktypeUUID") final UUID zaaktypeUUID) {
        assertActie(policyService.readAppActies().getZaken());
        return resultaattypeConverter.convertResultaattypes(ztcClientService.readResultaattypen(ztcClientService.readZaaktype(zaaktypeUUID).getUrl()));
    }

    private void ingelogdeMedewerkerToekennenAanZaak(final Zaak zaak, final RESTZaakToekennenGegevens toekennenGegevens) {
        final User user = identityService.readUser(loggedInUserInstance.get().getId());
        zrcClientService.updateRol(zaak.getUuid(), bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
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
        assertActie(policyService.readZaakActies(zaak).getVerwijderenInitiator());
        zrcClientService.deleteRol(initiator.getUuid(), INITIATOR_VERWIJDER_REDEN);
    }

    private void addInitiator(final IdentificatieType identificatieType, final String identificatie, final Zaak zaak) {
        final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaak.getZaaktype());
        switch (identificatieType) {
            case BSN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenInitiatorPersoon());
                addBetrokkenNatuurlijkPersoon(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            }
            case VN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenInitiatorBedrijf());
                addBetrokkenVestiging(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            }
            case RSIN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenInitiatorBedrijf());
                addBetrokkenNietNatuurlijkPersoon(initiator, identificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
            }
            default -> throw new IllegalStateException(String.format("Unexpected value: %s '%s'", identificatieType, identificatie));
        }
    }

    private void removeBetrokkene(final Zaak zaak, final Rol<?> betrokkene) {
        assertActie(policyService.readZaakActies(zaak).getVerwijderenBetrokkene());
        zrcClientService.deleteRol(betrokkene.getUuid(), BETROKKENE_VERWIJDER_REDEN);
    }

    private void addBetrokkene(final UUID roltype, IdentificatieType identificatieType, final String identificatie, final String toelichting,
            final Zaak zaak) {
        final Roltype betrokkene = ztcClientService.readRoltype(roltype);
        switch (identificatieType) {
            case BSN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenBetrokkenePersoon());
                addBetrokkenNatuurlijkPersoon(betrokkene, identificatie, zaak, toelichting);
            }
            case VN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenBetrokkeneBedrijf());
                addBetrokkenVestiging(betrokkene, identificatie, zaak, toelichting);
            }
            case RSIN -> {
                assertActie(policyService.readZaakActies(zaak).getToevoegenBetrokkeneBedrijf());
                addBetrokkenNietNatuurlijkPersoon(betrokkene, identificatie, zaak, toelichting);
            }
            default -> throw new IllegalStateException(String.format("Unexpected value: %s '%s'", identificatieType, identificatie));
        }
    }

    private void addBetrokkenNatuurlijkPersoon(final Roltype roltype, final String bsn, final Zaak zaak, String toelichting) {
        final RolNatuurlijkPersoon rol = new RolNatuurlijkPersoon(zaak.getUrl(), roltype.getUrl(), toelichting, new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenVestiging(final Roltype roltype, final String vestigingsnummer, final Zaak zaak, String toelichting) {
        final RolVestiging rol = new RolVestiging(zaak.getUrl(), roltype.getUrl(), toelichting, new Vestiging(vestigingsnummer));
        zrcClientService.createRol(rol, toelichting);
    }

    private void addBetrokkenNietNatuurlijkPersoon(final Roltype roltype, final String rsin, final Zaak zaak, String toelichting) {
        final RolNietNatuurlijkPersoon rol = new RolNietNatuurlijkPersoon(zaak.getUrl(), roltype.getUrl(), toelichting, new NietNatuurlijkPersoon(rsin));
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

    private void koppelHoofdEnDeelzaak(final UUID hoofdzaakUUID, final UUID deelzaakUUID) {
        final HoofdzaakPatch deelzaak = new HoofdzaakPatch(zrcClientService.createUrlZaak(hoofdzaakUUID));
        zrcClientService.updateZaak(deelzaakUUID, deelzaak);
        eventingService.send(ZAAK.updated(hoofdzaakUUID));
    }

    private void ontkoppelHoofdEnDeelzaak(final UUID deelzaakUUID, final UUID hoofdzaakUUID, final String reden) {
        final HoofdzaakPatch deelzaak = new HoofdzaakPatch(null);
        zrcClientService.updateZaak(deelzaakUUID, deelzaak, reden);
        eventingService.send(ZAAK.updated(hoofdzaakUUID));
    }
}
