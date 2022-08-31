/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(Vervalreden.Adapter.class)
public enum Vervalreden implements AbstractEnum<Vervalreden> {

    TIJDELIJK("tijdelijk"),
    INGETROKKEN_OVERHEID("ingetrokken_overheid"),
    INGETROKKEN_BELANGHEBBENDE("ingetrokken_belanghebbende");

    private final String value;

    Vervalreden(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Vervalreden fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Vervalreden> {

        @Override
        protected Vervalreden[] getEnums() {
            return values();
        }
    }
}
