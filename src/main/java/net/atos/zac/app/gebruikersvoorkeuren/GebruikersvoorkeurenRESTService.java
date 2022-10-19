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
import net.atos.zac.app.gebruikersvoorkeuren.model.RESTZoekopdracht;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.gebruikersvoorkeuren.GebruikersvoorkeurenService;
import net.atos.zac.gebruikersvoorkeuren.model.Werklijst;
import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;
import net.atos.zac.gebruikersvoorkeuren.model.ZoekopdrachtListParameters;

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
    @Path("dasboardcard")
    public List<RESTDashboardCardInstelling> listDashboardCards() {
        return dashboardCardInstellingConverter.convert(
                gebruikersvoorkeurenService.listDashboardCards(loggedInUserInstance.get().getId()));
    }

    @PUT
    @Path("dasboardcard")
    public void updateDashboardCards(final List<RESTDashboardCardInstelling> instellingen) {
        gebruikersvoorkeurenService.updateDashboardCards(
                loggedInUserInstance.get().getId(),
                instellingen.stream().map(dashboardCardInstellingConverter::convert).toList());
    }
}
