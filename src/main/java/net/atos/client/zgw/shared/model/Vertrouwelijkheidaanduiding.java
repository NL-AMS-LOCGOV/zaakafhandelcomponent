/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 *
 */
@JsonbTypeAdapter(Vertrouwelijkheidaanduiding.Adapter.class)
public enum Vertrouwelijkheidaanduiding implements AbstractEnum<Vertrouwelijkheidaanduiding> {

    OPENBAAR("openbaar"),
    BEPERKT_OPENBAAR("beperkt_openbaar"),
    INTERN("intern"),
    ZAAKVERTROUWELIJK("zaakvertrouwelijk"),
    VERTROUWELIJK("vertrouwelijk"),
    CONFIDENTIEEL("confidentieel"),
    GEHEIM("geheim"),
    ZEER_GEHEIM("zeer_geheim");

    private final String value;

    Vertrouwelijkheidaanduiding(final String value) {
        this.value = value;
    }

    public static Vertrouwelijkheidaanduiding fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    public String toValue() {
        return value;
    }

    static class Adapter extends AbstractEnum.Adapter<Vertrouwelijkheidaanduiding> {

        @Override
        protected Vertrouwelijkheidaanduiding[] getEnums() {
            return values();
        }
    }
}
