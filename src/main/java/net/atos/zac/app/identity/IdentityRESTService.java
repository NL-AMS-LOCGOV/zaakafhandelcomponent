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

import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

@Singleton
@Path("identity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IdentityRESTService {

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private IdentityService identityService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @GET
    @Path("groepen")
    public List<RESTGroup> listGroepen() {
        final List<Group> groups = identityService.listGroups();
        return groupConverter.convertGroups(groups);
    }

    @GET
    @Path("groepen/{groepId}/medewerkers")
    public List<RESTUser> listMedewerkersInGroep(@PathParam("groepId") final String groepId) {
        final List<User> users = identityService.listUsersInGroup(groepId);
        return userConverter.convertUsers(users);
    }

    @GET
    @Path("medewerkers")
    public List<RESTUser> listMedewerkers() {
        final List<User> users = identityService.listUsers();
        return userConverter.convertUsers(users);
    }

    @GET
    @Path("ingelogdemedewerker")
    public RESTUser readIngelogdeMedewerker() {
        return userConverter.convertUser(loggedInUserInstance.get());
    }
}
