/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTIngelogdeMedewerkerConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTIngelogdeMedewerker;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.IdmService;

@Path("identity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IdentityRESTService {

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    private RESTIngelogdeMedewerkerConverter ingelogdeMedewerkerConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @EJB
    private IdmService idmService;

    @GET
    @Path("groepen")
    public List<RESTGroep> getGroepen() {
        final List<Group> groups = idmService.listGroups();
        return groepConverter.convertGroups(groups);
    }

    @GET
    @Path("groepen/{groepId}/medewerkers")
    public List<RESTMedewerker> getMedewerkersInGroep(@PathParam("groepId") final String groepId) {
        final List<User> users = idmService.listUsersInGroup(groepId);
        return medewerkerConverter.convertUsers(users);
    }

    @GET
    @Path("ingelogdemedewerker")
    public RESTIngelogdeMedewerker getIngelogdeMedewerker() {
        return ingelogdeMedewerkerConverter.convertIngelogdeMedewerker(ingelogdeMedewerker);
    }
}
