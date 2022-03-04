/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

import net.atos.client.zgw.zrc.model.BetrokkeneType;

public class RESTZaakBetrokkeneGegevens {

    public UUID zaakUUID;

    public BetrokkeneType betrokkeneType;

    public String betrokkeneIdentificatie;

    public String reden;
}
