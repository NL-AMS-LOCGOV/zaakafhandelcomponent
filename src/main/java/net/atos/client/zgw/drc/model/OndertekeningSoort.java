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
@JsonbTypeAdapter(OndertekeningSoort.Adapter.class)
public enum OndertekeningSoort implements AbstractEnum<OndertekeningSoort> {

    ANALOOG("analoog"),

    DIGITAAL("digitaal"),

    PKI("pki");

    private final String value;

    OndertekeningSoort(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static OndertekeningSoort fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<OndertekeningSoort> {

        @Override
        protected OndertekeningSoort[] getEnums() {
            return values();
        }
    }
}
