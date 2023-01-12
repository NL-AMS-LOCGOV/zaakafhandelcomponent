/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import net.atos.zac.identity.model.User;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class LoggedInUser extends User {

    private final Set<String> groupIds;

    private final Set<String> roles;

    // Lijst van zaaktype.omschrijving waarvoor de ingelogde gebruiker geautoriseerd is.
    // De waarde null betekend dat de gebruiker geautoriseerd is voor elk zaaktype.
    private final Set<String> geautoriseerdeZaaktypen;

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName,
                        final String email, final Set<String> roles, final Set<String> groupIds, final Set<String> geautoriseerdeZaaktypen) {
        super(id, firstName, lastName, displayName, email);
        this.roles = unmodifiableSet(roles);
        this.groupIds = unmodifiableSet(groupIds);
        this.geautoriseerdeZaaktypen = geautoriseerdeZaaktypen != null ? unmodifiableSet(geautoriseerdeZaaktypen) : null;
    }

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName,
                        final String email, final Set<String> roles, final Set<String> groupIds) {
        this(id, firstName, lastName, displayName, email, roles, groupIds, null);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getGroupIds() {
        return groupIds;
    }

    public Set<String> getGeautoriseerdeZaaktypen() {
        if (geautoriseerdeZaaktypen != null) {
            return geautoriseerdeZaaktypen;
        } else {
            throw new IllegalStateException("Ingelogde gebruiker is geautoriseerd voor alle zaaktypen. Deze kunnen echter niet worden opgevraagd.");
        }
    }

    public boolean isGeautoriseerdVoorAlleZaaktypen() {
        return geautoriseerdeZaaktypen == null;
    }

    public boolean isGeautoriseerdZaaktype(final String zaaktypeOmschrijving) {
        return geautoriseerdeZaaktypen == null || geautoriseerdeZaaktypen.contains(zaaktypeOmschrijving);
    }
}
