/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum FilterWaarde {
    LEEG("-NULL-"),
    NIET_LEEG("-NOT-NULL-");

    private final String magicValue;

    FilterWaarde(final String value) {
        this.magicValue = value;
    }

    @Override
    public String toString() {
        return magicValue;
    }

    public <TYPE> boolean is(final TYPE value) {
        return value != null && value.toString().equals(magicValue);
    }
}
