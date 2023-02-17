/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.UUID;

public class RESTZaakKoppelGegevens {
    public UUID bronZaakUuid;

    public UUID doelZaakUuid;

    public RelatieType bronRelatieType;

    public RelatieType doelRelatieType;
}
