/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(Geslachtsaanduiding.Adapter.class)
public enum Geslachtsaanduiding implements AbstractEnum<Geslachtsaanduiding> {
    MAN("m"),
    VROUW("v"),
    ONBEKEND("o");

    private final String value;

    Geslachtsaanduiding(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Geslachtsaanduiding fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }


    static class Adapter extends AbstractEnum.Adapter<Geslachtsaanduiding> {

        @Override
        protected Geslachtsaanduiding[] getEnums() {
            return values();
        }
    }
}
