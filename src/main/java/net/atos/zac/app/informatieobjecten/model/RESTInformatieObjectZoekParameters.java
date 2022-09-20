/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.net.URI;
import java.util.UUID;

public class RESTInformatieObjectZoekParameters {

    public URI zaakURI;

    public UUID zaakUUID;

    public UUID[] UUIDs;

    public boolean toonGekoppeldeZaakDocumenten;

    public boolean searchGekoppeldeZaak;
}
