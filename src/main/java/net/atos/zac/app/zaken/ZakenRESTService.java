/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import static net.atos.zac.util.UriUtil.uuidFromURI;

import java.util.Collections;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.joda.time.IllegalInstantException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.audit.converter.RESTHistorieRegelConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;
import net.atos.zac.app.zaken.converter.RESTZaakConverter;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakAfbrekenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkeneGegevens;
import net.atos.zac.app.zaken.model.RESTZaakEditMetRedenGegevens;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakToekennenGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.app.zaken.model.RESTZakenVerdeelGegevens;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.util.OpenZaakPaginationUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;

/**
 *
 */
@Path("zaken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ZakenRESTService {

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
    private RESTZaakOverzichtConverter zaakOverzichtConverter;

    @Inject
    private RESTHistorieRegelConverter auditTrailConverter;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

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

    @GET
    @Path("zaak/{uuid}")
    public RESTZaak readZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        deleteSignalering(zaak);
        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/id/{identificatie}")
    public RESTZaak readZaakById(@PathParam("identificatie") final String identificatie) {
        final ZaakListParameters zaakListParameters = new ZaakListParameters();
        zaakListParameters.setIdentificatie(identificatie);
        final Results<Zaak> zaakResults = zrcClientService.listZaken(zaakListParameters);
        if (zaakResults.getCount() == 0) {
            throw new NotFoundException(String.format("Zaak met identificatie '%s' niet gevonden", identificatie));
        } else if (zaakResults.getCount() > 1) {
            throw new IllegalStateException(String.format("Meerdere zaken met identificatie '%s' gevonden", identificatie));
        }
        final Zaak zaak = zaakResults.getResults().get(0);
        deleteSignalering(zaak);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("initiator")
    public void createInitiator(final RESTZaakBetrokkeneGegevens gegevens) {
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        addInitiator(gegevens.betrokkeneIdentificatie, zaak, gegevens.reden);
    }

    @DELETE
    @Path("initiator")
    public void deleteInitiator(@QueryParam("gegevens") final String jsonGegevens) {
        final RESTZaakBetrokkeneGegevens gegevens;
        try {
            gegevens = new ObjectMapper().readValue(jsonGegevens, RESTZaakBetrokkeneGegevens.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e); //invalid data
        }
        final Zaak zaak = zrcClientService.readZaak(gegevens.zaakUUID);
        zrcClientService.deleteRol(zaak.getUrl(), gegevens.betrokkeneType, gegevens.reden);
    }

    @POST
    @Path("zaak")
    public RESTZaak createZaak(final RESTZaak restZaak) {
        final Zaak zaak = zaakConverter.convert(restZaak);
        final Zaak nieuweZaak = zgwApiService.createZaak(zaak);
        if (StringUtils.isNotEmpty(restZaak.initiatorBSN)) {
            addInitiator(restZaak.initiatorBSN, nieuweZaak, "Initiator");
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

    @DELETE
    @Path("zaakinformatieobjecten/{informatieObjectUuid}/{zaakUuid}")
    public void ontkoppelInformatieObject(@PathParam("informatieObjectUuid") final UUID uuid, @PathParam("zaakUuid") final UUID zaakUuid) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        parameters.setInformatieobject(informatieobject.getUrl());
        parameters.setZaak(zrcClientService.readZaak(zaakUuid).getUrl());
        List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(parameters);
        if (zaakInformatieobjecten.isEmpty()) {
            throw new NotFoundException(String.format("Geen ZaakInformatieobject gevonden voor Zaak: '%s' en InformatieObject: '%s'", zaakUuid, uuid));
        }
        zrcClientService.deleteZaakInformatieobject(zaakInformatieobjecten.get(0).getUuid());
        if (findZakenInformatieobject(uuid).isEmpty()) {
            ontkoppeldeDocumentenService.create(informatieobject);
        }
    }

    @GET
    @Path("zaken/informatieobject/{informatieObjectUuid}")
    public List<String> findZakenInformatieobject(@PathParam("informatieObjectUuid") UUID uuid) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(drcClientService.readEnkelvoudigInformatieobject(uuid).getUrl());
        List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(parameters);
        return zaakInformatieobjects.stream()
                .map(zaakInformatieobject -> zrcClientService.readZaak(zaakInformatieobject.getZaak()).getIdentificatie()).collect(Collectors.toList());
    }

    @GET
    @Path("zaken")
    public TableResponse<RESTZaakOverzicht> getZaken(@Context final HttpServletRequest request) {
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
        zaakListParameters.setRolBetrokkeneIdentificatieMedewerkerIdentificatie(
                ingelogdeMedewerker.get().getGebruikersnaam());
        final Results<Zaak> zaakResults = zrcClientService.listZaken(zaakListParameters);
        final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter.convertZaakResults(zaakResults,
                                                                                                  tableState.getPagination());
        return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
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
            final User user = flowableService.readUser(toekennenGegevens.behandelaarGebruikersnaam);
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
        final Group group = !StringUtils.isEmpty(verdeelGegevens.groepId) ? flowableService.readGroup(verdeelGegevens.groepId) : null;
        final User user = !StringUtils.isEmpty(verdeelGegevens.behandelaarGebruikersnaam) ?
                flowableService.readUser(verdeelGegevens.behandelaarGebruikersnaam) : null;
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            if (group != null) {
                zrcClientService.updateRol(zaak.getUrl(), bepaalRolGroep(group, zaak), verdeelGegevens.reden);
            }
            if (user != null) {
                zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak), verdeelGegevens.reden);
            }
        });
    }

    @PUT
    @Path("vrijgeven")
    public void vrijgeven(final RESTZakenVerdeelGegevens verdeelGegevens) {
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            zrcClientService.deleteRol(zaak.getUrl(), BetrokkeneType.MEDEWERKER, verdeelGegevens.reden);
        });
    }

    @PUT
    @Path("afbreken")
    public void afbreken(final RESTZaakAfbrekenGegevens zaakAfbrekenGegevens) {
        Zaak zaak = zrcClientService.readZaak(zaakAfbrekenGegevens.zaakUUID);
        final ZaakbeeindigParameter zaakbeeindigParameter = zaakafhandelParameterBeheerService.readZaakbeeindigParameter(
                uuidFromURI(zaak.getZaaktype()), zaakAfbrekenGegevens.zaakbeeindigRedenId);
        zgwApiService.createResultaatForZaak(zaak, zaakbeeindigParameter.getResultaattype(), zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        zgwApiService.endZaak(zaak, zaakbeeindigParameter.getZaakbeeindigReden().getNaam());
        // Terminate case after the zaak is ended in order to prevent the EndCaseLifecycleListener from ending the zaak.
        flowableService.terminateCase(zaakAfbrekenGegevens.zaakUUID);
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
        return zaakOverzichtConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/groep")
    public RESTZaak groepToekennen(final RESTZaakToekennenGegevens toekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(toekennenGegevens.zaakUUID);

        final Group group = flowableService.readGroup(toekennenGegevens.groepId);
        zrcClientService.updateRol(zaak.getUrl(), bepaalRolGroep(group, zaak), toekennenGegevens.reden);

        return zaakConverter.convert(zaak);
    }

    @GET
    @Path("zaak/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID uuid) {
        final List<AuditTrailRegel> auditTrail = zrcClientService.listAuditTrail(uuid);
        return auditTrailConverter.convert(auditTrail);
    }

    private TableResponse<RESTZaakOverzicht> findZaakOverzichten(final HttpServletRequest request,
            final boolean getOpenZaken) {
        final TableRequest tableState = TableRequest.getTableState(request);

        if (ingelogdeMedewerker.get().isInAnyGroup()) {
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
        final User user = flowableService.readUser(ingelogdeMedewerker.get().getGebruikersnaam());
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

    private void deleteSignalering(final Zaak zaak) {
        final SignaleringZoekParameters parameters = new SignaleringZoekParameters();
        parameters.type(SignaleringType.Type.ZAAK_OP_NAAM);
        parameters.target(ingelogdeMedewerker.get());
        parameters.subject(zaak);
        signaleringenService.deleteSignalering(parameters);
    }

    private void addInitiator(final String bsn, final Zaak zaak, String toelichting) {
        final Roltype initiator = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.INITIATOR);
        RolNatuurlijkPersoon rol = new RolNatuurlijkPersoon(zaak.getUrl(), initiator.getUrl(), toelichting, new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rol);
    }
}
