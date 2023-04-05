/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectPand
 */
public class ZaakobjectPand extends ZaakobjectMetObjectIdentificatie<ObjectPand> {

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectPand() {
    }

    /**
     * Constructor with all required fields.
     */
    public ZaakobjectPand(final URI zaak, final URI bagobjectUri, final ObjectPand pand) {
        super(zaak, bagobjectUri, Objecttype.PAND, pand);
    }

    @Override
    public String getWaarde() {
        return getObjectIdentificatie().getIdentificatie();
    }
}
