/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.flowable.idm.api.User;
import org.joda.time.IllegalInstantException;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.zaken.converter.RESTZaakConverter;
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter;
import net.atos.zac.app.zaken.converter.RESTZaaktypeConverter;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakToekennenGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.TableRequest;
import net.atos.zac.datatable.TableResponse;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.PaginationUtil;

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
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Path("zaak/{uuid}")
    public RESTZaak getZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        return zaakConverter.convert(zaak);
    }

    @POST
    @Path("zaak")
    public RESTZaak postZaak(final RESTZaak restZaak) {
        final Zaak zaak = zaakConverter.convert(restZaak);
        final Zaak nieuweZaak = zgwApiService.createZaak(zaak);
        return zaakConverter.convert(nieuweZaak);
    }

    @PATCH
    @Path("zaak/{uuid}")
    public RESTZaak updateZaak(@PathParam("uuid") final UUID zaakUUID, final RESTZaak restZaak) {
        final Zaak zaak = new Zaak();
        zaak.setToelichting(restZaak.toelichting);
        zaak.setOmschrijving(restZaak.omschrijving);
        final Zaak updatedZaak = zrcClientService.updateZaakPartially(zaakUUID, zaak);
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
    public TableResponse<RESTZaakOverzicht> getMijnZaken(@Context final HttpServletRequest request) {
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
    public TableResponse<RESTZaakOverzicht> getWerkvoorraadZaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);

        if (ingelogdeMedewerker.isInAnyGroup()) {
            final Results<Zaak> zaakResults = zrcClientService.listOpenZaken(getZaakListParameters(tableState));
            final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter
                    .convertZaakResults(zaakResults, tableState.getPagination());
            return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
        } else {
            return new TableResponse<>(Collections.emptyList(), 0);
        }
    }

    @GET
    @Path("afgehandeld")
    public TableResponse<RESTZaakOverzicht> getAfgerondeZaken(@Context final HttpServletRequest request) {
        final TableRequest tableState = TableRequest.getTableState(request);

        if (ingelogdeMedewerker.isInAnyGroup()) {
            final Results<Zaak> zaakResults = zrcClientService.listClosedZaken(getZaakListParameters(tableState));
            final List<RESTZaakOverzicht> zaakOverzichten = zaakOverzichtConverter
                    .convertZaakResults(zaakResults, tableState.getPagination());
            return new TableResponse<>(zaakOverzichten, zaakResults.getCount());
        } else {
            return new TableResponse<>(Collections.emptyList(), 0);
        }
    }

    @GET
    @Path("zaaktypes")
    public List<RESTZaaktype> getZaaktypes() {
        return ztcClientService.listZaaktypen(configurationService.readDefaultCatalogusURI()).stream()
                .map(zaaktypeConverter::convert)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("toekennen")
    public RESTZaak toekennen(final RESTZaakToekennenGegevens restZaak) {
        // ToDo: ESUITEDEV-25820 rechtencheck met solrZaak
        final Zaak zaak = zrcClientService.readZaak(restZaak.uuid);
        final List<Rol<?>> rollen = zrcClientService.listRollen(zaak.getUrl());

        // Toekennen of overdragen
        if (!StringUtils.isEmpty(restZaak.behandelaarGebruikersnaam)) {
            final User user = flowableService.readUser(restZaak.behandelaarGebruikersnaam);
            rollen.add(bepaalRolMedewerker(user, zaak));
        } else {
            // Vrijgeven
            final Optional<Rol<?>> rolMedewerker =
                    rollen.stream().filter(rol -> rol.getBetrokkeneType() == BetrokkeneType.MEDEWERKER).findFirst();
            rolMedewerker.ifPresent(medewerker -> rollen.removeIf(rol -> rol.equalBetrokkeneRol(medewerker)));
        }

        zrcClientService.updateRollen(zaak.getUrl(), rollen);
        return zaakConverter.convert(zaak);
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

    @GET
    @Path("caches/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public String clearCaches() {
        ztcClientService.clearStatustypeCache();
        ztcClientService.clearZaaktypeStatustypeManagedCache();
        ztcClientService.clearResultaattypeCache();
        ztcClientService.clearZaaktypeResultaattypeManagedCache();
        ztcClientService.clearZaaktypeRoltypeCache();
        ztcClientService.clearZaaktypeUrlCache();
        ztcClientService.clearZaaktypeCache();
        ztcClientService.clearZaaktypeManagedCache();
        zrcClientService.clearZaakstatusManagedCache();
        zgwApiService.clearZaakBehandelaarManagedCache();
        zgwApiService.clearZaakGroepManagedCache();
        return "all caches cleared";
    }

    private Zaak ingelogdeMedewerkerToekennenAanZaak(final RESTZaakToekennenGegevens restZaak) {
        final Zaak zaak = zrcClientService.readZaak(restZaak.uuid);
        final List<Rol<?>> rollen = zrcClientService.listRollen(zaak.getUrl());
        final User user = flowableService.readUser(ingelogdeMedewerker.getGebruikersnaam());

        rollen.add(bepaalRolMedewerker(user, zaak));

        zrcClientService.updateRollen(zaak.getUrl(), rollen);

        return zaak;
    }

    private ZaakListParameters getZaakListParameters(final TableRequest tableState) {
        final ZaakListParameters zaakListParameters = new ZaakListParameters();

        zaakListParameters.setPage(PaginationUtil.getZGWClientPage(tableState.getPagination()));

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

    private RolMedewerker bepaalRolMedewerker(final User user, final Zaak zaak) {
        final net.atos.client.zgw.zrc.model.Medewerker medewerker = new net.atos.client.zgw.zrc.model.Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR);
        return new RolMedewerker(zaak.getUrl(), roltype.getUrl(), "behandelaar", medewerker);
    }
}
