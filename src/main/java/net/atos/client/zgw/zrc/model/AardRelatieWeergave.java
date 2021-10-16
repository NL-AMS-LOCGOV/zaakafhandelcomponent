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
@JsonbTypeAdapter(AardRelatieWeergave.Adapter.class)
public enum AardRelatieWeergave implements AbstractEnum<AardRelatieWeergave> {

    HOORT_BIJ("Hoort bij, omgekeerd: kent"),

    LEGT_VAST("Legt vast, omgekeerd: kan vastgelegd zijn als");

    private final String value;

    AardRelatieWeergave(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static AardRelatieWeergave fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<AardRelatieWeergave> {

        @Override
        protected AardRelatieWeergave[] getEnums() {
            return values();
        }
    }
}
