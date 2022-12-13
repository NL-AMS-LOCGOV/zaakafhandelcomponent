/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.util.UUID;

import net.atos.zac.app.zaken.model.RelatieType;

public class RESTGekoppeldeZaakEnkelvoudigInformatieObject extends RESTEnkelvoudigInformatieobject {

    public RelatieType relatieType;

    public String zaakIdentificatie;

    public UUID zaakUUID;
}
