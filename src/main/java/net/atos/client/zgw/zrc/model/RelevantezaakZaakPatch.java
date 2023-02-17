/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbProperty;

import java.util.List;

public class RelevantezaakZaakPatch extends Zaak {

    @JsonbProperty(nillable = true)
    private final List<RelevanteZaak> relevanteAndereZaken;

    public RelevantezaakZaakPatch(final List<RelevanteZaak> relevanteAndereZaken) {
        this.relevanteAndereZaken = relevanteAndereZaken;
    }

    public List<RelevanteZaak> getRelevanteAndereZaken() {
        return relevanteAndereZaken;
    }
}
