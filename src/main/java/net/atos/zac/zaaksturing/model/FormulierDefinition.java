/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

public enum FormulierDefinition {

    AANVULLENDE_INFORMATIE("Aanvullende informatie"),
    ADVIES("Advies");

    private final String naam;

    FormulierDefinition(final String naam) {
        this.naam = naam;
    }

    public String getNaam() {
        return naam;
    }
}
