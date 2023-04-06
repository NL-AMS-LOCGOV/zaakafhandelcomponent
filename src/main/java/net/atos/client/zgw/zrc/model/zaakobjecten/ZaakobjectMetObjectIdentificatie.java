/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectMetObjectIdentificatie
 */
public abstract class ZaakobjectMetObjectIdentificatie<OBJECT> extends Zaakobject {

    /**
     * Het generieke object
     * - Required
     */
    private OBJECT objectIdentificatie;

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectMetObjectIdentificatie() {
    }

    /**
     * Constructor with required attributes
     */
    public ZaakobjectMetObjectIdentificatie(final URI zaak, final URI objectUri, final Objecttype objectType, final OBJECT objectIdentificatie) {
        super(zaak, objectUri, objectType);
        this.objectIdentificatie = objectIdentificatie;
    }

    public OBJECT getObjectIdentificatie() {
        return objectIdentificatie;
    }
}
