/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(ToelichtingPrefix.Adapter.class)
public enum ToelichtingPrefix implements AbstractEnum<ToelichtingPrefix> {
    ONTKOPPELD("Ontkoppeld"),
    VERWIJDERD("Verwijderd"),
    VERPLAATST("Verplaatst");

    private final String value;

    ToelichtingPrefix(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static ToelichtingPrefix fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }


    static class Adapter extends AbstractEnum.Adapter<ToelichtingPrefix> {

        @Override
        protected ToelichtingPrefix[] getEnums() {
            return values();
        }
    }
}
