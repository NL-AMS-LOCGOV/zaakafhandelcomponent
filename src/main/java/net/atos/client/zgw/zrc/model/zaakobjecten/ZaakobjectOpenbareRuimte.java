/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectOpenbareRuimte
 */
public class ZaakobjectOpenbareRuimte extends ZaakobjectMetObjectIdentificatie<ObjectOpenbareRuimte> {

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectOpenbareRuimte() {
    }

    /**
     * Constructor with required attributes
     */
    public ZaakobjectOpenbareRuimte(final URI zaak, final URI bagobjectURI, final ObjectOpenbareRuimte objectOpenbareRuimte) {
        super(zaak, bagobjectURI, Objecttype.OPENBARE_RUIMTE, objectOpenbareRuimte);
    }

    @Override
    public String getWaarde() {
        return getObjectIdentificatie().getIdentificatie();
    }
}
