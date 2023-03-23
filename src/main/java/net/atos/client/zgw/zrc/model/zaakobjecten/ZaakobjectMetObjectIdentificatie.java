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


    public ZaakobjectMetObjectIdentificatie() {
    }

    /**
     * Constructor with required attributes
     */
    public ZaakobjectMetObjectIdentificatie(final URI zaak, Objecttype objectType, OBJECT objectIdentificatie) {
        super(zaak, objectType);
        this.objectIdentificatie = objectIdentificatie;

    }


    public OBJECT getObjectIdentificatie() {
        return objectIdentificatie;
    }

    protected abstract boolean equalObjectIdentificatie(OBJECT other);

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ZaakobjectMetObjectIdentificatie<OBJECT> zaakobject = (ZaakobjectMetObjectIdentificatie<OBJECT>) other;
        return equalObjectIdentificatie(zaakobject.getObjectIdentificatie());
    }

}
