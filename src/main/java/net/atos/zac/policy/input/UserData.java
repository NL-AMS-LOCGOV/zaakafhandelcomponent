/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import java.util.Set;

class UserData {

    public String id;

    public Set<String> rollen;

    // Lijst van zaaktype.omschrijving waarvoor de ingelogde gebruiker geautoriseerd is.
    // De waarde null betekend dat de gebruiker geautoriseerd is voor elk zaaktype.
    public Set<String> zaaktypen;
}
