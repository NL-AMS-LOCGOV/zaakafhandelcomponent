/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class WerklijstRechten {

    private final boolean inbox;

    private final boolean ontkoppeldeDocumentenVerwijderen;

    private final boolean inboxProductaanvragenVerwijderen;

    private final boolean zakenTaken;

    private final boolean zakenTakenVerdelen;

    @JsonbCreator
    public WerklijstRechten(
            @JsonbProperty("inbox") final boolean inbox,
            @JsonbProperty("ontkoppelde_documenten_verwijderen") final boolean ontkoppeldeDocumentenVerwijderen,
            @JsonbProperty("inbox_productaanvragen_verwijderen") final boolean inboxProductaanvragenVerwijderen,
            @JsonbProperty("zaken_taken") final boolean zakenTaken,
            @JsonbProperty("zaken_taken_verdelen") final boolean zakenTakenVerdelen) {
        this.inbox = inbox;
        this.ontkoppeldeDocumentenVerwijderen = ontkoppeldeDocumentenVerwijderen;
        this.inboxProductaanvragenVerwijderen = inboxProductaanvragenVerwijderen;
        this.zakenTaken = zakenTaken;
        this.zakenTakenVerdelen = zakenTakenVerdelen;
    }

    public boolean getInbox() {
        return inbox;
    }

    public boolean getInboxProductaanvragenVerwijderen() {
        return inboxProductaanvragenVerwijderen;
    }

    public boolean getOntkoppeldeDocumentenVerwijderen() {
        return ontkoppeldeDocumentenVerwijderen;
    }

    public boolean getZakenTaken() {
        return zakenTaken;
    }

    public boolean getZakenTakenVerdelen() {
        return zakenTakenVerdelen;
    }


}
