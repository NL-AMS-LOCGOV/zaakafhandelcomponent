/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectNummeraanduiding
 */
public class ZaakobjectNummeraanduiding extends ZaakobjectMetObjectIdentificatie<ObjectOverige<ObjectNummeraanduiding>> {

    public static final String OBJECT_TYPE_OVERIGE = "nummeraanduiding";

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectNummeraanduiding() {}

    /**
     * Constructor with required attributes
     */
    public ZaakobjectNummeraanduiding(final URI zaak, final URI bagObjectUri, final ObjectNummeraanduiding nummeraanduiding) {
        super(zaak, bagObjectUri, Objecttype.OVERIGE, new ObjectOverige<>(nummeraanduiding));
        setObjectTypeOverige(OBJECT_TYPE_OVERIGE);
    }

    @Override
    public String getWaarde() {
        return getObjectIdentificatie().overigeData.getIdentificatie();
    }
}
