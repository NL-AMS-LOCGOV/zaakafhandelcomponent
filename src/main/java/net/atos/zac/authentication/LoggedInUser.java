/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.util.Set;

import net.atos.zac.identity.model.User;

public class LoggedInUser extends User {

    private final Set<String> roles;

    // Lijst van zaaktype.omschrijving waarvoor de ingelogde gebruiker geautoriseerd is.
    // De waarde null betekend dat de gebruiker geautoriseerd is voor elk zaaktype.
    private final Set<String> geautoriseerdeZaaktypen;

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName,
            final String email, final Set<String> roles, final Set<String> geautoriseerdeZaaktypen) {
        super(id, firstName, lastName, displayName, email);
        this.roles = roles;
        this.geautoriseerdeZaaktypen = geautoriseerdeZaaktypen;
    }

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName,
            final String email, final Set<String> roles) {
        this(id, firstName, lastName, displayName, email, roles, null);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getGeautoriseerdeZaaktypen() {
        return geautoriseerdeZaaktypen;
    }

    public boolean isGeautoriseerdZaaktype(final String zaaktypeOmschrijving) {
        return geautoriseerdeZaaktypen == null || geautoriseerdeZaaktypen.contains(zaaktypeOmschrijving);
    }
}
