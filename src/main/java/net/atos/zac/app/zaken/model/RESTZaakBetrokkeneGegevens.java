/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

import net.atos.zac.app.klanten.model.klant.IdentificatieType;

public class RESTZaakBetrokkeneGegevens {

    public UUID zaakUUID;

    public UUID roltypeUUID;

    public String roltoelichting;

    public IdentificatieType betrokkeneIdentificatieType;

    public String betrokkeneIdentificatie;
}
