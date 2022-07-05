/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

public class RESTZaakOntkoppelGegevens {
    public UUID teOntkoppelenZaakUUID;
    public String ontkoppelenVanZaakIdentificatie;
    public String reden;
    public RelatieType zaakRelatietype;
}
