/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr;

public enum FieldType {

    STRING("string"),
    LOCATION("location"),
    PDATE("pdate"),
    PINT("pint"),
    PLONG("plong"),
    PDOUBLE("pdouble"),
    BOOLEAN("boolean"),
    TEXT_NL("text_nl"),
    TEXT_WS("text_ws"),
    TEXT_GENERAL_REV("text_general_rev");

    private final String value;

    FieldType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
