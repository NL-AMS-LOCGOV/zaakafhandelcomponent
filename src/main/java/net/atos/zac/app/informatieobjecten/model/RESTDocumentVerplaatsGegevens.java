/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.util.UUID;

public class RESTDocumentVerplaatsGegevens {

    public final String INBOX_DOCUMENTEN = "inbox-documenten";

    public final String ONTKOPPELDE_DOCUMENTEN = "ontkoppelde-documenten";

    public UUID documentUUID;

    public String bron;

    public String nieuweZaakID;

    public boolean vanuitInboxDocumenten() {
        return INBOX_DOCUMENTEN.equals(bron);
    }

    public boolean vanuitOntkoppeldeDocumenten() {
        return ONTKOPPELDE_DOCUMENTEN.equals(bron);
    }

}
