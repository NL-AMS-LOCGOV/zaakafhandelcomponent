/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import java.util.List;

import javax.inject.Inject;

import net.atos.zac.app.identity.model.RESTLoggedInUser;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.User;

public class RESTUserConverter {

    @Inject
    private IdentityService identityService;

    public List<RESTUser> convertUsers(final List<User> users) {
        return users.stream()
                .map(this::convertUser).toList();
    }

    public List<RESTUser> convertUserIds(final List<String> userIds) {
        return userIds.stream()
                .map(this::convertUserId)
                .toList();
    }

    public RESTUser convertUser(final User user) {
        final RESTUser restUser = new RESTUser();
        restUser.id = user.getId();
        restUser.naam = user.getFullName();
        return restUser;
    }

    public RESTLoggedInUser convertLoggedInUser(final LoggedInUser user) {
        final RESTLoggedInUser restUser = new RESTLoggedInUser();
        restUser.id = user.getId();
        restUser.naam = user.getFullName();
        restUser.groupIds = user.getGroupIds();
        return restUser;
    }

    public RESTUser convertUserId(final String userId) {
        if (userId != null) {
            final User user = identityService.readUser(userId);
            return convertUser(user);
        }
        return null;
    }
}
