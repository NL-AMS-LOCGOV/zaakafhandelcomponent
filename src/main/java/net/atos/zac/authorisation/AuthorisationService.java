/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authorisation;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.authorisation.model.Action;

@ApplicationScoped
public class AuthorisationService {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public Set<Action> getAllowedActionsZaak(final String zaaktypeOmschrijving) {
        return Set.of(Action.CREATE, Action.UPDATE);
    }
}
