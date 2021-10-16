/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(Betalingsindicatie.Adapter.class)
public enum Betalingsindicatie implements AbstractEnum<Betalingsindicatie> {

    /**
     * Er is geen sprake van te betalen, met de zaak gemoeide, kosten.
     */
    NVT("nvt"),

    /**
     * De met de zaak gemoeide kosten zijn (nog) niet betaald.
     */
    NOG_NIET("nog_niet"),

    /**
     * De met de zaak gemoeide kosten zijn gedeeltelijk betaald.
     */
    GEDEELTELIJK("gedeeltelijk"),

    /**
     * De met de zaak gemoeide kosten zijn geheel betaald.
     */
    GEHEEL("geheel");

    private final String value;

    Betalingsindicatie(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Betalingsindicatie fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Betalingsindicatie> {

        @Override
        protected Betalingsindicatie[] getEnums() {
            return values();
        }
    }
}
