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
@JsonbTypeAdapter(InformatieobjectStatus.Adapter.class)
public enum InformatieobjectStatus implements AbstractEnum<InformatieobjectStatus> {

    /**
     * Aan het informatieobject wordt nog gewerkt.
     */
    IN_BEWERKING("in_bewerking"),

    /**
     * Informatieobject gereed maar moet nog vastgesteld worden.
     */
    TER_VASTSTELLING("ter_vaststelling"),

    /**
     * Informatieobject door bevoegd iets of iemand vastgesteld dan wel ontvangen.
     */
    DEFINITIEF("definitief"),

    /**
     * Informatieobject duurzaam bewaarbaar gemaakt; een gearchiveerd informatie-element.
     */
    GEARCHIVEERD("gearchiveerd");

    private final String value;

    InformatieobjectStatus(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static InformatieobjectStatus fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<InformatieobjectStatus> {

        @Override
        protected InformatieobjectStatus[] getEnums() {
            return values();
        }
    }
}
