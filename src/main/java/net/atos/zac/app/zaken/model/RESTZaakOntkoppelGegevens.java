/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

public class RESTZaakOntkoppelGegevens {

    public UUID zaakUuid;

    public String gekoppeldeZaakIdentificatie;

    public RelatieType relatietype;

    public String reden;
}
