/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
public enum ObjectStatusFilter implements AbstractEnum<ObjectStatusFilter> {

    /**
     * Toon objecten waarvan het attribuut `concept` true is.
     */
    CONCEPT("concept"),

    /**
     * Toon objecten waarvan het attribuut `concept` false is (standaard).
     */
    DEFINITIEF("definitief"),

    /**
     * Toon objecten waarvan het attribuut `concept` true of false is.
     */
    ALLES("alles");

    private final String value;

    ObjectStatusFilter(final String value) {
        this.value = value;
    }

    public static ObjectStatusFilter fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    @Override
    public String toValue() {
        return value;
    }
}
