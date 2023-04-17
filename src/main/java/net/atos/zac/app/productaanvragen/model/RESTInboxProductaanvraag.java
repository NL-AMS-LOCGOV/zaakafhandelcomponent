/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.productaanvragen.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTInboxProductaanvraag {

    public long id;

    public UUID productaanvraagObjectUUID;

    public UUID aanvraagdocumentUUID;

    public int aantalBijlagen;

    public String type;

    public LocalDate ontvangstdatum;

    public String initiatorID;

}
