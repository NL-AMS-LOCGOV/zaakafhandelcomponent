/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 * Aanduiding van de richting van informatieobjecten van het gerelateerde INFORMATIEOBJECTTYPE bij zaken van het gerelateerde ZAAKTYPE.
 */
@JsonbTypeAdapter(Richting.Adapter.class)
public enum Richting implements AbstractEnum<Richting> {

    INKOMEND("inkomend"),

    INTERN("intern"),

    UITGAAND("uitgaand");

    private final String value;

    Richting(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Richting fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Richting> {

        @Override
        protected Richting[] getEnums() {
            return values();
        }
    }
}
