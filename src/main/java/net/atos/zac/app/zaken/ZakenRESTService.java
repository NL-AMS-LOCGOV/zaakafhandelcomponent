/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static net.atos.zac.websocket.event.ScreenEventType.TAAK;
import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_TAKEN;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.IllegalInstantException;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.client.zgw.zrc.model.Vestiging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.audit.converter.RESTHistorieRegelConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;
import net.atos.zac.app.zaken.converter.RESTCommunicatiekanaalConverter;
import net.atos.zac.app.zaken.converter.RESTGeometryConverter;
import net.atos.zac.app.zaken.converter.RESTZaakConverter;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTCommunicatiekanaal;
import net.atos.zac.app.zaken.model.RESTDocumentOntkoppelGegevens;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakAfbrekenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkeneGegevens;
import net.atos.zac.app.zaken.model.RESTZaakEditMetRedenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakHeropenenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOpschortGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOpschorting;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakToekennenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakVerlengGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.app.zaken.model.RESTZakenVerdeelGegevens;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.util.OpenZaakPaginationUtil;
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

    public static final String INITIATOR_VERWIJDER_REDEN = "Initiator verwijderd door de medewerker tijdens het behandelen van de zaak";

    public static final String INITIATOR_TOEVOEGEN_REDEN = "Initiator toegekend door de medewerker tijdens het behandelen van de zaak";

    public static final String OPSCHORTING = "Opschorting";

    public static final String HERVATTING = "Hervatting";

    public static final String VERLENGING = "Verlenging";

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakConverter zaakConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private FlowableService flowableService;

    @Inject
    private EventingService eventingService;

    @Inject
    private IdentityService identityService;

    @Inject
    private RESTZaakOverzichtConverter zaakOverzichtConverter;

    @Inject
    private RESTHistorieRegelConverter auditTrailConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private SignaleringenService signaleringenService;

    @Inject
    private OntkoppeldeDocumentenService ontkoppeldeDocumentenService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private RESTCommunicatiekanaalConverter communicatiekanaalConverter;

    @Inject
    private RESTGeometryConverter restGeometryConverter;

    @Inject
    private IndexeerService indexeerService;

    @GET
    @Path("zaak/{uuid}")
    public RESTZaak readZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        deleteSignaleringen(zaak);
        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/id/{identificatie}")
    public RESTZaak readZaakById(@PathParam("identificatie") final String identificatie) {
        final Zaak zaak = zrcClientService.readZaakByID(identificatie);
        deleteSignaleringen(zaak);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("initiator")
    public void createInitiator(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        addInitiator(gegevens.betrokkeneIdentificatie, zaak, INITIATOR_TOEVOEGEN_REDEN);
    }

    @DELETE
    @Path("{uuid}/initiator")
    public void deleteInitiator(@PathParam("uuid") final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Rol<?> initiator = zgwApiService.findRolForZaak(zaak, AardVanRol.INITIATOR);
        zrcClientService.deleteRol(zaak.getUrl(), initiator.getBetrokkeneType(), INITIATOR_VERWIJDER_REDEN);
    }

    @POST
    @Path("zaak")
    public RESTZaak createZaak(final RESTZaak restZaak) {
        final Zaak zaak = zaakConverter.convert(restZaak);
        final Zaak nieuweZaak = zgwApiService.createZaak(zaak);
        if (StringUtils.isNotEmpty(restZaak.initiatorIdentificatie)) {
            addInitiator(restZaak.initiatorIdentificatie, nieuweZaak, INITIATOR_TOEVOEGEN_REDEN);
        }
        return zaakConverter.convert(nieuweZaak);
    }

    @PATCH
    @Path("zaak/{uuid}")
    public RESTZaak partialUpdateZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakEditMetRedenGegevens restZaakEditMetRedenGegevens) {
        final Zaak updatedZaak = zrcClientService.updateZaakPartially(zaakUUID, zaakConverter.convertToPatch(restZaakEditMetRedenGegevens.zaak),
                                                                      restZaakEditMetRedenGegevens.reden);
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("{uuid}/zaakgeometrie")
    public RESTZaak updateZaakGeometrie(@PathParam("uuid") final UUID uuid, final RESTZaak restZaak) {
        final Zaak updatedZaak = zrcClientService.updateZaakGeometrie(uuid, restGeometryConverter.convert(restZaak.zaakgeometrie));
        return zaakConverter.convert(updatedZaak);
    }

    @PATCH
    @Path("zaak/{uuid}/opschorting")
    public RESTZaak opschortenZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakOpschortGegevens restZaakOpschortGegevens) {
        final Zaak updatedZaak = zrcClientService.updateZaakPartially(zaakUUID, zaakConverter.convertToPatch(restZaakOpschortGegevens),
                                                                      restZaakOpschortGegevens.indicatieOpschorting ? OPSCHORTING : HERVATTING);
        if (restZaakOpschortGegevens.indicatieOpschorting) {
            flowableService.updateDatumtijdOpgeschortForOpenCase(zaakUUID, ZonedDateTime.now());
            flowableService.updateVerwachteDagenOpgeschortForOpenCase(zaakUUID, restZaakOpschortGegevens.duurDagen);
        } else {
            flowableService.removeDatumtijdOpgeschortForOpenCase(zaakUUID);
            flowableService.removeVerwachteDagenOpgeschortForOpenCase(zaakUUID);
        }
        return zaakConverter.convert(updatedZaak);
    }

    @GET
    @Path("zaak/{uuid}/opschorting")
    public RESTZaakOpschorting getZaakOpschorting(@PathParam("uuid") final UUID zaakUUID) {
        final RESTZaakOpschorting zaakOpschorting = new RESTZaakOpschorting();
        zaakOpschorting.vanafDatumTijd = flowableService.findDatumtijdOpgeschortForCase(zaakUUID);
        zaakOpschorting.duurDagen = flowableService.findVerwachteDagenOpgeschortForCase(zaakUUID);
        return zaakOpschorting;
    }

    @PATCH
    @Path("zaak/{uuid}/verlenging")
    public RESTZaak verlengenZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaakVerlengGegevens restZaakVerlengGegevens) {
        final Zaak updatedZaak = zrcClientService.updateZaakPartially(zaakUUID, zaakConverter.convertToPatch(zaakUUID, restZaakVerlengGegevens), VERLENGING);
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
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(ontkoppelGegevens.documentUUID);
        final Zaak zaak = zrcClientService.readZaak(ontkoppelGegevens.zaakUUID);
        parameters.setInformatieobject(informatieobject.getUrl());
        parameters.setZaak(zaak.getUrl());
        List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(parameters);
        if (zaakInformatieobjecten.isEmpty()) {
            throw new NotFoundException(
                    String.format("Geen ZaakInformatieobject gevonden voor Zaak: '%s' en InformatieObject: '%s'", ontkoppelGegevens.zaakUUID,
                                  ontkoppelGegevens.documentUUID));
        }

        zrcClientService.deleteZaakInformatieobject(zaakInformatieobjecten.get(0).getUuid(), ontkoppelGegevens.reden);
        if (findZakenInformatieobject(ontkoppelGegevens.documentUUID).isEmpty()) {
            ontkoppeldeDocumentenService.create(informatieobject, zaak, ontkoppelGegevens.reden);
        }
    }

    @GET
    @Path("zaken/informatieobject/{informatieObjectUuid}")
    public List<String> findZakenInformatieobject(@PathParam("informatieObjectUuid") UUID informatieobjectUuid) {
        List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(
                drcClientService.readEnkelvoudigInformatieobject(informatieobjectUuid));
        return zaakInformatieobjects.stream()
                .map(zaakInformatieobject -> zrcClientService.readZaak(zaakInformatieobject.getZaak()).getIdentificatie()).collect(Collectors.toList());
    }

    @GET
    @Path("zaken")
    public TableResponse<RESTZaakOverzicht> listZaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);

        final ZaakListParameters zaakListParameters = getZaakListParameters(tableState);
        final Results<Zaak> zaakResults = zrcClientService.listZaken(zaakListParameters);

        final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter
                .convertZaakResults(zaakResults, tableState.getPagination());

        return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
    }

    @GET
    @Path("mijn")
    public TableResponse<RESTZaakOverzicht> listZakenMijn(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);
        final ZaakListParameters zaakListParameters = getZaakListParameters(tableState);
        zaakListParameters.setRolBetrokkeneIdentificatieMedewerkerIdentificatie(loggedInUserInstance.get().getId());
        final Results<Zaak> zaakResults = zrcClientService.listZaken(zaakListParameters);
        final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter.convertZaakResults(zaakResults,
                                                                                                  tableState.getPagination());
        return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
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
    @Path("werkvoorraad")
    public TableResponse<RESTZaakOverzicht> listZakenWerkvoorraad(@Context final HttpServletRequest request) {
        return findZaakOverzichten(request, true);
    }

    @GET
    @Path("afgehandeld")
    public TableResponse<RESTZaakOverzicht> listZakenAfgehandeld(@Context final HttpServletRequest request) {
        return findZaakOverzichten(request, false);
    }

    @GET
    @Path("zaaktypes")
    public List<RESTZaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI()).stream()
                .map(zaaktypeConverter::convert)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("toekennen")
    public RESTZaak toekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);

        if (!StringUtils.isEmpty(toekennenGegevens.behandelaarGebruikersnaam)) {
            // Toekennen of overdragen
            final User user = identityService.readUser(toekennenGegevens.behandelaarGebruikersnaam);
            zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
        } else {
            // Vrijgeven
            zrcClientService.deleteRol(zaak.getUrl(), BetrokkeneType.MEDEWERKER, toekennenGegevens.reden);
        }

        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("verdelen")
    public void verdelen(final RESTZakenVerdeelGegevens verdeelGegevens) {
        final Group group = !StringUtils.isEmpty(verdeelGegevens.groepId) ? identityService.readGroup(verdeelGegevens.groepId) : null;
        final User user = !StringUtils.isEmpty(verdeelGegevens.behandelaarGebruikersnaam) ?
                identityService.readUser(verdeelGegevens.behandelaarGebruikersnaam) : null;
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            if (group != null) {
                zrcClientService.updateRol(zaak.getUrl(), bepaalRolGroep(group, zaak), verdeelGegevens.reden);
            }
            if (user != null) {
                zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak), verdeelGegevens.reden);
            }
        });
        indexeerService.indexeerDirect(verdeelGegevens.uuids, ZoekObjectType.ZAAK);
    }

    @PUT
    @Path("vrijgeven")
    public void vrijgeven(final RESTZakenVerdeelGegevens verdeelGegevens) {
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            zrcClientService.deleteRol(zaak.getUrl(), BetrokkeneType.MEDEWERKER, verdeelGegevens.reden);
        });
        indexeerService.indexeerDirect(verdeelGegevens.uuids, ZoekObjectType.ZAAK);
    }

    @PATCH
    @Path("/zaak/{uuid}/afbreken")
    public void afbreken(@PathParam("uuid") final UUID zaakUUID, final RESTZaakAfbrekenGegevens afbrekenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final ZaakbeeindigParameter zaakbeeindigParameter = zaakafhandelParameterBeheerService.readZaakbeeindigParameter(
                uuidFromURI(zaak.getZaaktype()), afbrekenGegevens.zaakbeeindigRedenId);
        zgwApiService.createResultaatForZaak(zaak, zaakbeeindigParameter.getResultaattype(), zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        zgwApiService.endZaak(zaak, zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        // Terminate case after the zaak is ended in order to prevent the EndCaseLifecycleListener from ending the zaak.
        flowableService.terminateCase(zaakUUID);
    }

    @PATCH
    @Path("/zaak/{uuid}/heropenen")
    public void heropenen(@PathParam("uuid") final UUID zaakUUID, final RESTZaakHeropenenGegevens heropenenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zgwApiService.heropenZaak(zaak);
    }

    @PUT
    @Path("toekennen/mij")
    public RESTZaak toekennenAanIngelogdeMedewerker(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(toekennenGegevens);
        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/mij/lijst")
    public RESTZaakOverzicht toekennenAanIngelogdeMedewerkerVanuitLijst(
            final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(toekennenGegevens);
        indexeerService.indexeerDirect(zaak.getUuid(), ZoekObjectType.ZAAK);
        return zaakOverzichtConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/groep")
    public RESTZaak groepToekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);

        final Group group = identityService.readGroup(toekennenGegevens.groepId);
        zrcClientService.updateRol(zaak.getUrl(), bepaalRolGroep(group, zaak), toekennenGegevens.reden);

        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID uuid) {
        final List<AuditTrailRegel> auditTrail = zrcClientService.listAuditTrail(uuid);
        return auditTrailConverter.convert(auditTrail);
    }

    @GET
    @Path("communicatiekanalen")
    public List<RESTCommunicatiekanaal> listCommunicatiekanalen() {
        final List<CommunicatieKanaal> communicatieKanalen = vrlClientService.listCommunicatiekanalen();
        return communicatiekanaalConverter.convert(communicatieKanalen);
    }

    private TableResponse<RESTZaakOverzicht> findZaakOverzichten(final HttpServletRequest request,
            final boolean getOpenZaken) {
        final TableRequest tableState = TableRequest.getTableState(request);

        if (loggedInUserInstance.get().isInAnyGroup()) {
            final Results<Zaak> zaakResults;
            if (getOpenZaken) {
                zaakResults = zrcClientService.listOpenZaken(getZaakListParameters(tableState));
            } else {
                zaakResults = zrcClientService.listClosedZaken(getZaakListParameters(tableState));
            }
            final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter.convertZaakResults(zaakResults, tableState.getPagination());
            return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
        } else {
            return new TableResponse<>(Collections.emptyList(), 0);
        }
    }

    private Zaak ingelogdeMedewerkerToekennenAanZaak(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);
        final User user = identityService.readUser(loggedInUserInstance.get().getId());
        zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak), toekennenGegevens.reden);
        return zaak;
    }

    private ZaakListParameters getZaakListParameters(final TableRequest tableState) {
        final ZaakListParameters zaakListParameters = new ZaakListParameters();

        zaakListParameters.setPage(OpenZaakPaginationUtil.calculateOpenZaakPageNumber(tableState.getPagination()));

        final boolean desc = "desc".equals(tableState.getSort().getDirection());
        zaakListParameters.setOrdering(desc ? "-" + tableState.getSort().getPredicate() : tableState.getSort().getPredicate());

        for (final Map.Entry<String, String> entry : tableState.getSearch().getPredicateObject().entrySet()) {
            switch (entry.getKey()) {
                case "zaaktype" -> zaakListParameters.setZaaktype(ztcClientService.readZaaktypeUrl(entry.getValue()));
                case "groep" -> zaakListParameters
                        .setRolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie(entry.getValue());
                default -> throw new IllegalInstantException(String.format("Unknown search criteria: '%s'", entry.getKey()));
            }
        }
        return zaakListParameters;
    }

    private RolOrganisatorischeEenheid bepaalRolGroep(final Group group, final Zaak zaak) {
        final OrganisatorischeEenheid groep = new OrganisatorischeEenheid();
        groep.setIdentificatie(group.getId());
        groep.setNaam(group.getName());
        final Roltype roltype = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR);
        return new RolOrganisatorischeEenheid(zaak.getUrl(), roltype.getUrl(), "groep", groep);
    }

    private RolMedewerker bepaalRolMedewerker(final User user, final Zaak zaak) {
        final net.atos.client.zgw.zrc.model.Medewerker medewerker = new net.atos.client.zgw.zrc.model.Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR);
        return new RolMedewerker(zaak.getUrl(), roltype.getUrl(), "behandelaar", medewerker);
    }

    private void deleteSignaleringen(final Zaak zaak) {
        signaleringenService.deleteSignaleringen(
                new SignaleringZoekParameters(loggedInUserInstance.get())
                        .types(SignaleringType.Type.ZAAK_OP_NAAM,
                               SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD)
                        .subject(zaak));
    }

    private void addInitiator(final String identificatienummer, final Zaak zaak, String toelichting) {
        switch (identificatienummer.length()) {
            case 9 -> addInitiatorBurger(identificatienummer, zaak, toelichting);
            case 12 -> addInitiatorBedrijf(identificatienummer, zaak, toelichting);
            default -> throw new IllegalStateException("Unexpected value: '%s'" + identificatienummer);
        }
    }

    private void addInitiatorBurger(final String bsn, final Zaak zaak, String toelichting) {
        final Roltype initiator = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.INITIATOR);
        RolNatuurlijkPersoon rol = new RolNatuurlijkPersoon(zaak.getUrl(), initiator.getUrl(), toelichting, new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rol);
    }

    private void addInitiatorBedrijf(final String vestigingsnummer, final Zaak zaak, String toelichting) {
        final Roltype initiator = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.INITIATOR);
        RolVestiging rol = new RolVestiging(zaak.getUrl(), initiator.getUrl(), toelichting, new Vestiging(vestigingsnummer));
        zrcClientService.createRol(rol);
    }

    private int verlengOpenTaken(final UUID zaakUUID, final int duurDagen) {
        final int[] count = new int[1];
        flowableService.listOpenTasksforCase(zaakUUID).stream()
                .filter(task -> task.getDueDate() != null)
                .forEach(task -> {
                    task.setDueDate(convertToDate(convertToLocalDate(task.getDueDate()).plusDays(duurDagen)));
                    flowableService.updateTask(task);
                    eventingService.send(TAAK.updated(task));
                    count[0]++;
                });
        return count[0];
    }
}
