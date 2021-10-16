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
@JsonbTypeAdapter(Archiefstatus.Adapter.class)
public enum Archiefstatus implements AbstractEnum<Archiefstatus> {

    /**
     * De zaak cq. het zaakdossier is nog niet als geheel gearchiveerd.
     */
    NOG_TE_ARCHIVEREN("nog_te_archiveren"),

    /**
     * De zaak cq. het zaakdossier is als geheel niet-wijzigbaar bewaarbaar gemaakt.
     */
    GEARCHIVEERD("gearchiveerd"),

    /**
     * De zaak cq. het zaakdossier is als geheel niet-wijzigbaar bewaarbaar gemaakt maar de vernietigingsdatum kan nog niet bepaald worden.
     */
    GEARCHIVEERD_PROCESTERMIJN_ONBEKEND("gearchiveerd_procestermijn_onbekend"),

    /**
     * De zaak cq. het zaakdossier is overgebracht naar een archiefbewaarplaats.
     */
    OVERGEDRAGEN("overgedragen");

    private final String value;

    Archiefstatus(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Archiefstatus fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Archiefstatus> {

        @Override
        protected Archiefstatus[] getEnums() {
            return values();
        }
    }
}
