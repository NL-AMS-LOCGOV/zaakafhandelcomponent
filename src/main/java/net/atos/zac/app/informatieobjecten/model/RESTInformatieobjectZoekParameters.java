/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.util.List;
import java.util.UUID;

public class RESTInformatieobjectZoekParameters {

    public List<UUID> informatieobjectUUIDs;

    public UUID zaakUUID;

    public UUID besluittypeUUID;

    public boolean gekoppeldeZaakDocumenten;
}
