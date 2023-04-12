/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectWoonplaats
 */
public class ZaakobjectWoonplaats extends ZaakobjectMetObjectIdentificatie<ObjectWoonplaats> {

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectWoonplaats() {
    }

    /**
     * Constructor with required attributes
     */
    public ZaakobjectWoonplaats(final URI zaak, final URI bagobjectUri, final ObjectWoonplaats woonplaats) {
        super(zaak, bagobjectUri, Objecttype.WOONPLAATS, woonplaats);
    }

    @Override
    public String getWaarde() {
        return getObjectIdentificatie().getIdentificatie();
    }

}
