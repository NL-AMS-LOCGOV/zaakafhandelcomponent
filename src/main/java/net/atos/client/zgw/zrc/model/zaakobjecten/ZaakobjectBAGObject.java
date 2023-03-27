/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;
import java.util.Objects;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectWoonplaats
 */
public abstract class ZaakobjectBAGObject<OBJECT extends ObjectBAGObject> extends ZaakobjectMetObjectIdentificatie<OBJECT> {

    public ZaakobjectBAGObject() {
    }

    public ZaakobjectBAGObject(final URI zaak, final URI bagobjectUri, final Objecttype type, final OBJECT bagObject) {
        super(zaak, type, bagObject);
        setObject(bagobjectUri);
    }

    @Override
    protected boolean equalObjectIdentificatie(final OBJECT otherBagObject) {
        final OBJECT bagObject = getObjectIdentificatie();
        if (bagObject == otherBagObject) {
            return true;
        }
        if (otherBagObject == null) {
            return false;
        }
        return Objects.equals(bagObject.getIdentificatie(), otherBagObject.getIdentificatie());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectType(), getObjectIdentificatie().getIdentificatie());
    }

}
