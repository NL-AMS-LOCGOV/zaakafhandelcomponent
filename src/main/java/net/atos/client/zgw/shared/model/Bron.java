/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 * Bron API
 */
@JsonbTypeAdapter(Bron.Adapter.class)
public enum Bron implements AbstractEnum<Bron> {

    /**
     * Autorisaties API.
     */
    AUTORISATIES_API("AC"),

    /**
     * Notificaties API.
     */
    NOTIFICATIES_API("NRC"),

    /**
     * Zaken API
     */
    ZAKEN_API("ZRC"),

    /**
     * Catalogi API
     */
    CATALOGI_API("ZTC"),

    /**
     * Documenten API
     */
    DOCUMENTEN_API("DRC"),

    /**
     * Besluiten API
     */
    BESLUITEN_API("BRC");

    private final String value;

    Bron(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Bron fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Bron> {

        @Override
        protected Bron[] getEnums() {
            return values();
        }
    }
}
