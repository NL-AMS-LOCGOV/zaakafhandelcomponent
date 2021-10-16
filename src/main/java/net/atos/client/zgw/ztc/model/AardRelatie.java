/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(AardRelatie.Adapter.class)
public enum AardRelatie implements AbstractEnum<AardRelatie> {

    VERVOLG("vervolg"),
    BIJDRAGE("bijdrage"),
    ONDERWERP("onderwerp");

    private final String value;

    AardRelatie(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static AardRelatie fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<AardRelatie> {

        @Override
        protected AardRelatie[] getEnums() {
            return values();
        }
    }
}
