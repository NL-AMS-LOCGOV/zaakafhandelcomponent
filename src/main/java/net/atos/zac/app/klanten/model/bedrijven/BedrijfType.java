/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.bedrijven;

public enum BedrijfType {
    HOOFDVESTIGING("hoofdvestiging"),
    NEVENVESTIGING("nevenvestiging"),
    RECHTSPERSOON("rechtspersoon");

    private final String type;

    BedrijfType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static BedrijfType getType(final String type) {
        if (type == null) {
            return null;
        }
        for (final BedrijfType bedrijfType : BedrijfType.values()) {
            if (bedrijfType.type.equals(type)) {
                return bedrijfType;
            }
        }
        throw new IllegalStateException(String.format("BedrijfType: '%s' not found", type));
    }
}
