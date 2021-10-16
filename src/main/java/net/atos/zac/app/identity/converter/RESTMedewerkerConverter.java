/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.idm.api.User;

import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.flowable.idm.IdmService;

public class RESTMedewerkerConverter {

    @Inject
    private IdmService idmService;

    public RESTMedewerker convertGebruikersnaam(final String gebruikersnaam) {
        if (gebruikersnaam != null) {
            final User user = idmService.getUser(gebruikersnaam);
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
        final User user = idmService.getUser(medewerkerId);
        return convertUser(user);
    }

    public static String convertToNaam(final User user) {
        if (user.getDisplayName() != null) {
            return user.getDisplayName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            return user.getId();
        }
    }
}
