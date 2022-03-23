/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model;

public enum KlantType {
    PERSOON,
    BEDRIJF;

    KlantType() {
    }

    public static KlantType getType(final String identificatieNummer) {
        return switch (identificatieNummer.length()) {
            case 9 -> PERSOON;
            case 12 -> BEDRIJF;
            default -> throw new IllegalStateException("Unexpected value: " + identificatieNummer.length());
        };
    }
}
