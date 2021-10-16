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
@JsonbTypeAdapter(IndicatieMachtiging.Adapter.class)
public enum IndicatieMachtiging implements AbstractEnum<IndicatieMachtiging> {

    /**
     * De betrokkene in de rol bij de zaak is door een andere betrokkene bij dezelfde zaak gemachtigd om namens hem of haar te handelen
     */
    GEMACHTIGDE("gemachtigde"),

    /**
     * De betrokkene in de rol bij de zaak heeft een andere betrokkene bij dezelfde zaak gemachtigd om namens hem of haar te handelen
     */
    MACHTIGINGGEVER("machtiginggever");

    private final String value;

    IndicatieMachtiging(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static IndicatieMachtiging fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }


    static class Adapter extends AbstractEnum.Adapter<IndicatieMachtiging> {

        @Override
        protected IndicatieMachtiging[] getEnums() {
            return values();
        }
    }
}
