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
@JsonbTypeAdapter(BetrokkeneType.Adapter.class)
public enum BetrokkeneType implements AbstractEnum<BetrokkeneType> {

    NATUURLIJK_PERSOON("natuurlijk_persoon"),

    NIET_NATUURLIJK_PERSOON("niet_natuurlijk_persoon"),

    VESTIGING("vestiging"),

    ORGANISATORISCHE_EENHEID("organisatorische_eenheid"),

    MEDEWERKER("medewerker");

    private final String value;

    BetrokkeneType(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static BetrokkeneType fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<BetrokkeneType> {

        @Override
        protected BetrokkeneType[] getEnums() {
            return values();
        }
    }
}
