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
@JsonbTypeAdapter(AardRelatie.Adapter.class)
public enum AardRelatie implements AbstractEnum<AardRelatie> {

    /**
     * De andere zaak gaf aanleiding tot het starten van de onderhanden zaak.
     */
    VERVOLG("vervolg"),

    /**
     * De andere zaak is relevant voor cq. is onderwerp van de onderhanden zaak.
     */
    ONDERWERP("onderwerp"),

    /**
     * Aan het bereiken van de uitkomst van de andere zaak levert de onderhanden zaak een bijdrage.'
     */
    BIJDRAGE("bijdrage");

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
