/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class DocumentRechten {

    private final boolean lezen;

    private final boolean wijzigen;

    private final boolean verwijderen;

    private final boolean vergrendelen;

    private final boolean ontgrendelen;

    private final boolean ondertekenen;

    @JsonbCreator
    public DocumentRechten(
            @JsonbProperty("lezen") final boolean lezen,
            @JsonbProperty("wijzigen") final boolean wijzigen,
            @JsonbProperty("verwijderen") final boolean verwijderen,
            @JsonbProperty("vergrendelen") final boolean vergrendelen,
            @JsonbProperty("ontgrendelen") final boolean ontgrendelen,
            @JsonbProperty("ondertekenen") final boolean ondertekenen) {
        this.lezen = lezen;
        this.verwijderen = verwijderen;
        this.wijzigen = wijzigen;
        this.vergrendelen = vergrendelen;
        this.ontgrendelen = ontgrendelen;
        this.ondertekenen = ondertekenen;
    }

    public boolean getLezen() {
        return lezen;
    }

    public boolean getVerwijderen() {
        return verwijderen;
    }

    public boolean getWijzigen() {
        return wijzigen;
    }

    public boolean getVergrendelen() {
        return vergrendelen;
    }

    public boolean getOntgrendelen() {
        return ontgrendelen;
    }

    public boolean getOndertekenen() {
        return ondertekenen;
    }
}
