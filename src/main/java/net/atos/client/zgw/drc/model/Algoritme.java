/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(Algoritme.Adapter.class)
public enum Algoritme implements AbstractEnum<Algoritme> {

    crc_16("crc_16"),
    crc_32("crc_32"),
    crc_64("crc_64"),
    fletcher_4("fletcher_4"),
    fletcher_8("fletcher_8"),
    fletcher_16("fletcher_16"),
    fletcher_32("fletcher_32"),
    hmac("hmac"),
    md5("md5"),
    sha_1("sha_1"),
    sha_256("sha_256"),
    sha_512("sha_512"),
    sha_3("sha_3");

    private final String value;

    Algoritme(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Algoritme fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Algoritme> {

        @Override
        protected Algoritme[] getEnums() {
            return values();
        }
    }
}
