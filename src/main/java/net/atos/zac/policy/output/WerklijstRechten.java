/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class WerklijstRechten {

    private final boolean documentenInbox;

    private final boolean documentenOntkoppeld;

    private final boolean documentenOntkoppeldVerwijderen;

    private final boolean productaanvragenInbox;

    private final boolean zakenTaken;

    private final boolean zakenTakenVerdelen;

    @JsonbCreator
    public WerklijstRechten(
            @JsonbProperty("documenten_inbox") final boolean documentenInbox,
            @JsonbProperty("documenten_ontkoppeld") final boolean documentenOntkoppeld,
            @JsonbProperty("documenten_ontkoppeld_verwijderen") final boolean documentenOntkoppeldVerwijderen,
            @JsonbProperty("productaanvragen_inbox") final boolean productaanvragenInbox,
            @JsonbProperty("zaken_taken") final boolean zakenTaken,
            @JsonbProperty("zaken_taken_verdelen") final boolean zakenTakenVerdelen) {
        this.documentenInbox = documentenInbox;
        this.documentenOntkoppeld = documentenOntkoppeld;
        this.documentenOntkoppeldVerwijderen = documentenOntkoppeldVerwijderen;
        this.productaanvragenInbox = productaanvragenInbox;
        this.zakenTaken = zakenTaken;
        this.zakenTakenVerdelen = zakenTakenVerdelen;
    }

    public boolean getDocumentenInbox() {
        return documentenInbox;
    }

    public boolean getDocumentenOntkoppeld() {
        return documentenOntkoppeld;
    }

    public boolean getDocumentenOntkoppeldVerwijderen() {
        return documentenOntkoppeldVerwijderen;
    }

    public boolean getZakenTaken() {
        return zakenTaken;
    }

    public boolean getZakenTakenVerdelen() {
        return zakenTakenVerdelen;
    }

    public boolean getProductaanvragenInbox() {
        return productaanvragenInbox;
    }
}
