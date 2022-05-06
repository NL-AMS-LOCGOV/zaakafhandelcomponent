/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.User;

public class RESTMedewerkerConverter {

    @Inject
    private IdentityService identityService;

    public RESTMedewerker convertGebruikersnaam(final String gebruikersnaam) {
        if (gebruikersnaam != null) {
            final User user = identityService.readUser(gebruikersnaam);
            return convertUser(user);
        } else {
            return null;
        }
    }

    public List<RESTMedewerker> convertUsers(final List<User> users) {
        return users.stream()
                .map(this::convertUser)
                .collect(Collectors.toList());
    }

    public RESTMedewerker convertUser(final User user) {
        final RESTMedewerker restMedewerker = new RESTMedewerker();
        restMedewerker.gebruikersnaam = user.getId();
        restMedewerker.naam = convertToNaam(user);
        return restMedewerker;
    }

    public RESTMedewerker convertUserId(final String medewerkerId) {
        if (medewerkerId != null) {
            final User user = identityService.readUser(medewerkerId);
            final RESTMedewerker restMedewerker = convertUser(user);
            return restMedewerker;
        }
        return null;
    }

    public static String convertToNaam(final User user) {
        if (user.getDisplayName() != null) {
            return user.getDisplayName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            return user.getLastName();
        }
    }
}
