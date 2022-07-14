/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class TaakActies {

    private final boolean lezen;

    private final boolean wijzigenToekenning;

    private final boolean wijzigenFormulier;

    private final boolean wijzigenOverig;

    private final boolean creeerenDocument;

    private final boolean toevoegenDocument;

    @JsonbCreator
    public TaakActies(@JsonbProperty("lezen") final boolean lezen,
            @JsonbProperty("wijzigen_toekenning") final boolean wijzigenToekenning,
            @JsonbProperty("wijzigen_formulier") final boolean wijzigenFormulier,
            @JsonbProperty("wijzigen_overig") final boolean wijzigenOverig,
            @JsonbProperty("creeeren_document") final boolean creeerenDocument,
            @JsonbProperty("toevoegen_document") final boolean toevoegenDocument) {
        this.lezen = lezen;
        this.wijzigenToekenning = wijzigenToekenning;
        this.wijzigenFormulier = wijzigenFormulier;
        this.wijzigenOverig = wijzigenOverig;
        this.creeerenDocument = creeerenDocument;
        this.toevoegenDocument = toevoegenDocument;
    }

    public boolean getLezen() {
        return lezen;
    }

    public boolean getWijzigenToekenning() {
        return wijzigenToekenning;
    }

    public boolean getWijzigenFormulier() {
        return wijzigenFormulier;
    }

    public boolean getWijzigenOverig() {
        return wijzigenOverig;
    }

    public boolean getCreeerenDocument() {
        return creeerenDocument;
    }

    public boolean getToevoegenDocument() {
        return toevoegenDocument;
    }
}
