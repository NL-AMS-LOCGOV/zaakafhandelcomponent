/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTIngelogdeMedewerkerConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTIngelogdeMedewerker;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

@Singleton
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
    private IdentityService identityService;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    @GET
    @Path("groepen")
    public List<RESTGroep> listGroepen() {
        final List<Group> groups = identityService.listGroups();
        return groepConverter.convertGroups(groups);
    }

    @GET
    @Path("groepen/{groepId}/medewerkers")
    public List<RESTMedewerker> listMedewerkersInGroep(@PathParam("groepId") final String groepId) {
        final List<User> users = identityService.listUsersInGroup(groepId);
        return medewerkerConverter.convertUsers(users);
    }

    @GET
    @Path("medewerkers")
    public List<RESTMedewerker> listMedewerkers() {
        final List<User> users = identityService.listUsers();
        return medewerkerConverter.convertUsers(users);
    }

    @GET
    @Path("ingelogdemedewerker")
    public RESTIngelogdeMedewerker readIngelogdeMedewerker() {
        return ingelogdeMedewerkerConverter.convertIngelogdeMedewerker(ingelogdeMedewerker.get());
    }
}
