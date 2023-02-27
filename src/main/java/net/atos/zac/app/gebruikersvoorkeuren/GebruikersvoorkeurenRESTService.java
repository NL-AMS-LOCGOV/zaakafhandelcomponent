/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.gebruikersvoorkeuren;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.gebruikersvoorkeuren.converter.RESTDashboardCardInstellingConverter;
import net.atos.zac.app.gebruikersvoorkeuren.converter.RESTZoekopdrachtConverter;
import net.atos.zac.app.gebruikersvoorkeuren.model.RESTDashboardCardInstelling;
import net.atos.zac.app.gebruikersvoorkeuren.model.RESTTabelGegevens;
import net.atos.zac.app.gebruikersvoorkeuren.model.RESTZoekopdracht;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.gebruikersvoorkeuren.GebruikersvoorkeurenService;
import net.atos.zac.gebruikersvoorkeuren.model.TabelInstellingen;
import net.atos.zac.gebruikersvoorkeuren.model.Werklijst;
import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;
import net.atos.zac.gebruikersvoorkeuren.model.ZoekopdrachtListParameters;
import net.atos.zac.policy.PolicyService;

@Singleton
@Path("gebruikersvoorkeuren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GebruikersvoorkeurenRESTService {

    @Inject
    private GebruikersvoorkeurenService gebruikersvoorkeurenService;

    @Inject
    private RESTZoekopdrachtConverter zoekopdrachtConverter;

    @Inject
    private RESTDashboardCardInstellingConverter dashboardCardInstellingConverter;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private PolicyService policyService;

    @Inject
    private RESTRechtenConverter rechtenConverter;

    @GET
    @Path("zoekopdracht/{lijstID}")
    public List<RESTZoekopdracht> listZoekopdrachten(@PathParam("lijstID") final Werklijst lijstID) {
        final List<Zoekopdracht> zoekopdrachten = gebruikersvoorkeurenService.listZoekopdrachten(
                new ZoekopdrachtListParameters(lijstID, loggedInUserInstance.get().getId()));
        return zoekopdrachtConverter.convert(zoekopdrachten);
    }

    @DELETE
    @Path("zoekopdracht/{id}")
    public void deleteZoekopdracht(@PathParam("id") final long id) {
        gebruikersvoorkeurenService.deleteZoekopdracht(id);
    }

    @POST
    @Path("zoekopdracht")
    public RESTZoekopdracht createOrUpdateZoekopdracht(final RESTZoekopdracht restZoekopdracht) {
        final Zoekopdracht zoekopdracht = zoekopdrachtConverter.convert(restZoekopdracht);
        return zoekopdrachtConverter.convert(gebruikersvoorkeurenService.createZoekopdracht(zoekopdracht));
    }

    @PUT
    @Path("zoekopdracht/actief")
    public void setZoekopdrachtActief(final RESTZoekopdracht restZoekopdracht) {
        final Zoekopdracht zoekopdracht = zoekopdrachtConverter.convert(restZoekopdracht);
        gebruikersvoorkeurenService.setActief(zoekopdracht);
    }

    @DELETE
    @Path("zoekopdracht/{werklijst}/actief")
    public void removeZoekopdrachtActief(@PathParam("werklijst") final Werklijst werklijst) {
        gebruikersvoorkeurenService.removeActief(new ZoekopdrachtListParameters(werklijst, loggedInUserInstance.get().getId()));
    }

    @GET
    @Path("tabel-gegevens/{werklijst}")
    public RESTTabelGegevens readTabelGegevens(@PathParam("werklijst") final Werklijst werklijst) {
        final RESTTabelGegevens restTabelGegevens = new RESTTabelGegevens();
        final TabelInstellingen tabelInstellingen = gebruikersvoorkeurenService.readTabelInstellingen(werklijst, loggedInUserInstance.get().getId());
        restTabelGegevens.aantalPerPagina = tabelInstellingen.getAantalPerPagina();
        restTabelGegevens.werklijstRechten = rechtenConverter.convert(policyService.readWerklijstRechten());
        return restTabelGegevens;
    }

    @PUT
    @Path("aantal-per-pagina/{werklijst}/{aantal}")
    public void updateAantalItemsPerPagina(@PathParam("werklijst") final Werklijst werklijst, @PathParam("aantal") final int aantal) {
        if (aantal <= 100 && aantal > 0) {
            final TabelInstellingen tabelInstellingen = new TabelInstellingen();
            tabelInstellingen.setAantalPerPagina(aantal);
            tabelInstellingen.setLijstID(werklijst);
            tabelInstellingen.setMedewerkerID(loggedInUserInstance.get().getId());
            gebruikersvoorkeurenService.updateTabelInstellingen(tabelInstellingen);
        }
    }

    @GET
    @Path("dasboardcard/actief")
    public List<RESTDashboardCardInstelling> listDashboardCards() {
        return dashboardCardInstellingConverter.convert(
                gebruikersvoorkeurenService.listDashboardCards(loggedInUserInstance.get().getId()));
    }

    @PUT
    @Path("dasboardcard/actief")
    public List<RESTDashboardCardInstelling> updateDashboardCards(final List<RESTDashboardCardInstelling> instellingen) {
        gebruikersvoorkeurenService.updateDashboardCards(
                loggedInUserInstance.get().getId(),
                instellingen.stream().map(dashboardCardInstellingConverter::convert).toList());
        return listDashboardCards();
    }

    @PUT
    @Path("dasboardcard")
    public List<RESTDashboardCardInstelling> addDashboardCard(RESTDashboardCardInstelling instelling) {
        gebruikersvoorkeurenService.addDashboardCard(
                loggedInUserInstance.get().getId(),
                dashboardCardInstellingConverter.convert(instelling));
        return listDashboardCards();
    }

    @DELETE
    @Path("dasboardcard")
    public List<RESTDashboardCardInstelling> deleteDashboardCard(final RESTDashboardCardInstelling instelling) {
        gebruikersvoorkeurenService.deleteDashboardCard(
                loggedInUserInstance.get().getId(),
                dashboardCardInstellingConverter.convert(instelling));
        return listDashboardCards();
    }
}
