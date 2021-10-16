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
@JsonbTypeAdapter(IndicatieInternExtern.Adapter.class)
public enum IndicatieInternExtern implements AbstractEnum<IndicatieInternExtern> {

    INTERN("intern"),

    EXTERN("extern");

    private final String value;

    IndicatieInternExtern(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static IndicatieInternExtern fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<IndicatieInternExtern> {

        @Override
        protected IndicatieInternExtern[] getEnums() {
            return values();
        }
    }
}
