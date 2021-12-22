/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.joda.time.IllegalInstantException;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.audit.converter.RESTAuditTrailConverter;
import net.atos.zac.app.audit.model.RESTAuditTrailRegel;
import net.atos.zac.app.zaken.converter.RESTZaakConverter;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakToekennenGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.app.zaken.model.RESTZakenVerdeelGegevens;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.OpenZaakPaginationUtil;

/**
 *
 */
@Path("zaken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
    private RESTAuditTrailConverter auditTrailConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Path("zaak/{uuid}")
    public RESTZaak readZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("zaak")
    public RESTZaak createZaak(final RESTZaak restZaak) {
        final Zaak zaak = zaakConverter.convert(restZaak);
        final Zaak nieuweZaak = zgwApiService.createZaak(zaak);
        return zaakConverter.convert(nieuweZaak);
    }

    @PATCH
    @Path("zaak/{uuid}")
    public RESTZaak partialUpdateZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaak restZaak) {
        final Zaak updatedZaak = zrcClientService.updateZaakPartially(zaakUUID, zaakConverter.convertToPatch(restZaak));
        return zaakConverter.convert(updatedZaak);
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
        zaakListParameters
                .setRolBetrokkeneIdentificatieMedewerkerIdentificatie(ingelogdeMedewerker.getGebruikersnaam());
        final Results<Zaak> zaakResults = zrcClientService.listZaken(zaakListParameters);

        final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter
                .convertZaakResults(zaakResults, tableState.getPagination());

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
        return ztcClientService.listZaaktypen(configurationService.readDefaultCatalogusURI()).stream()
                .map(zaaktypeConverter::convert)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("toekennen")
    public RESTZaak toekennen(final RESTZaakToekennenGegevens restZaak) {
        // ToDo: ESUITEDEV-25820 rechtencheck met solrZaak
        final Zaak zaak = zrcClientService.readZaak(restZaak.zaakUUID);

        // Toekennen of overdragen
        if (!StringUtils.isEmpty(restZaak.behandelaarGebruikersnaam)) {
            final User user = flowableService.readUser(restZaak.behandelaarGebruikersnaam);
            zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak));
        } else {
            // Vrijgeven
            zrcClientService.deleteRol(zaak.getUrl(), BetrokkeneType.MEDEWERKER);
        }

        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("verdelen")
    public void verdelen(final RESTZakenVerdeelGegevens verdeelGegevens) {
        if (StringUtils.isEmpty(verdeelGegevens.groepId)) {
            verdeelNaarMedewerker(verdeelGegevens);
        } else if (StringUtils.isEmpty(verdeelGegevens.behandelaarGebruikersnaam)) {
            verdeelNaarGroep(verdeelGegevens);
        } else {
            verdeelNaarGroep(verdeelGegevens);
            verdeelNaarMedewerker(verdeelGegevens);
        }
    }

    private void verdeelNaarMedewerker(final RESTZakenVerdeelGegevens verdeelGegevens) {
        final User user = flowableService.readUser(verdeelGegevens.behandelaarGebruikersnaam);
        verdeelGegevens.uuids.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak));
        });
    }

    private void verdeelNaarGroep(final RESTZakenVerdeelGegevens verdeelGegevens) {
        verdeelGegevens.uuids.forEach(uuid -> {
            final RESTZaakToekennenGegevens restZaakToekennenGegevens = new RESTZaakToekennenGegevens();
            restZaakToekennenGegevens.zaakUUID = uuid;
            restZaakToekennenGegevens.groepId = verdeelGegevens.groepId;
            restZaakToekennenGegevens.behandelaarGebruikersnaam = verdeelGegevens.behandelaarGebruikersnaam;
            groepToekennen(restZaakToekennenGegevens);
        });
    }

    @PUT
    @Path("vrijgeven")
    public void vrijgeven(final List<UUID> zaakIds) {
        zaakIds.forEach(uuid -> {
            final Zaak zaak = zrcClientService.readZaak(uuid);
            zrcClientService.deleteRol(zaak.getUrl(), BetrokkeneType.MEDEWERKER);
        });
    }

    @PUT
    @Path("toekennen/mij")
    public RESTZaak toekennenAanIngelogdeMedewerker(final RESTZaakToekennenGegevens restZaak) {
        // ToDo: ESUITEDEV-25820 rechtencheck met solrZaak
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(restZaak);
        return zaakConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/mij/lijst")
    public RESTZaakOverzicht toekennenAanIngelogdeMedewerkerVanuitLijst(final RESTZaakToekennenGegevens restZaak) {
        // ToDo: ESUITEDEV-25820 rechtencheck met solrZaak
        final Zaak zaak = ingelogdeMedewerkerToekennenAanZaak(restZaak);
        return zaakOverzichtConverter.convert(zaak);
    }

    @PUT
    @Path("toekennen/groep")
    public RESTZaak groepToekennen(final RESTZaakToekennenGegevens restZaakToekennenGegevens) {
        final Zaak zaak = zrcClientService.readZaak(restZaakToekennenGegevens.zaakUUID);

        final Group group = flowableService.readGroup(restZaakToekennenGegevens.groepId);
        zrcClientService.updateRol(zaak.getUrl(), bepaalRolGroep(group, zaak));

        return zaakConverter.convert(zaak);
    }

    private TableResponse<RESTZaakOverzicht> findZaakOverzichten(final HttpServletRequest request,
            final boolean getOpenZaken) {
        final TableRequest tableState = TableRequest.getTableState(request);

        if (ingelogdeMedewerker.isInAnyGroup()) {
            final Results<Zaak> zaakResults;
            if (getOpenZaken) {
                zaakResults = zrcClientService.listOpenZaken(getZaakListParameters(tableState));
            } else {
                zaakResults = zrcClientService.listClosedZaken(getZaakListParameters(tableState));
            }
            final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter
                    .convertZaakResults(zaakResults, tableState.getPagination());
            return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
        } else {
            return new TableResponse<>(Collections.emptyList(), 0);
        }
    }

    private Zaak ingelogdeMedewerkerToekennenAanZaak(final RESTZaakToekennenGegevens restZaak) {
        final Zaak zaak = zrcClientService.readZaak(restZaak.zaakUUID);
        final User user = flowableService.readUser(ingelogdeMedewerker.getGebruikersnaam());
        zrcClientService.updateRol(zaak.getUrl(), bepaalRolMedewerker(user, zaak));

        return zaak;
    }

    private ZaakListParameters getZaakListParameters(final TableRequest tableState) {
        final ZaakListParameters zaakListParameters = new ZaakListParameters();

        zaakListParameters.setPage(OpenZaakPaginationUtil.calculateOpenZaakPageNumber(tableState.getPagination()));

        final boolean desc = "desc".equals(tableState.getSort().getDirection());
        zaakListParameters
                .setOrdering(desc ? "-" + tableState.getSort().getPredicate() : tableState.getSort().getPredicate());

        for (final Map.Entry<String, String> entry : tableState.getSearch().getPredicateObject().entrySet()) {
            switch (entry.getKey()) {
                case "zaaktype":
                    zaakListParameters.setZaaktype(ztcClientService.readZaaktypeUrl(entry.getValue()));
                    break;
                case "groep":
                    zaakListParameters
                            .setRolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie(entry.getValue());
                    break;
                default:
                    throw new IllegalInstantException(String.format("Unknown search criteria: '%s'", entry.getKey()));
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

    @GET
    @Path("zaak/{uuid}/auditTrail")
    public List<RESTAuditTrailRegel> listAuditTrailVoorZaak(@PathParam("uuid") final UUID uuid) {
        final List<AuditTrailRegel> auditTrail = zrcClientService.listAuditTrail(uuid);
        return auditTrailConverter.convert(auditTrail);
    }
}
