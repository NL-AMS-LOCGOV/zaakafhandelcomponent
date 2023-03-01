/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

public class RESTZaakKoppelGegevens {

    public UUID zaakUuid;

    public UUID teKoppelenZaakUuid;

    public RelatieType relatieType;

    public RelatieType reverseRelatieType;
}
