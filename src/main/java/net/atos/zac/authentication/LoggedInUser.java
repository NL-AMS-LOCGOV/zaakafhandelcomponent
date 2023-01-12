/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import net.atos.zac.identity.model.User;

import java.util.Collections;
import java.util.Set;

public class LoggedInUser extends User {

    private final Set<String> groupIds;

    private final Set<String> roles;

    // Lijst van zaaktype.omschrijving waarvoor de ingelogde gebruiker geautoriseerd is.
    // De waarde null betekend dat de gebruiker geautoriseerd is voor elk zaaktype.
    private final Set<String> geautoriseerdeZaaktypen;

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName,
                        final String email, final Set<String> roles, final Set<String> groupIds, final Set<String> geautoriseerdeZaaktypen) {
        super(id, firstName, lastName, displayName, email);
        this.roles = Collections.unmodifiableSet(roles);
        this.groupIds = Collections.unmodifiableSet(groupIds);
        this.geautoriseerdeZaaktypen = Collections.unmodifiableSet(geautoriseerdeZaaktypen);
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
        return geautoriseerdeZaaktypen != null ? geautoriseerdeZaaktypen : Collections.emptySet();
    }

    public boolean isGeautoriseerdVoorAlleZaaktypen() {
        return geautoriseerdeZaaktypen == null;
    }

    public boolean isGeautoriseerdZaaktype(final String zaaktypeOmschrijving) {
        return geautoriseerdeZaaktypen == null || geautoriseerdeZaaktypen.contains(zaaktypeOmschrijving);
    }
}
