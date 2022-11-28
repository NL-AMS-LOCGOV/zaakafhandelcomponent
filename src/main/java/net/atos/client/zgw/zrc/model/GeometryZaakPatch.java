/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;


import javax.json.bind.annotation.JsonbProperty;

/**
 * Zaak geometry patch data
 */
public class GeometryZaakPatch extends Zaak {

    @JsonbProperty(nillable = true)
    private final Geometry zaakgeometrie;

    public GeometryZaakPatch(final Geometry zaakgeometrie) {
        this.zaakgeometrie = zaakgeometrie;
    }

    public Geometry getZaakgeometrie() {
        return zaakgeometrie;
    }
}
