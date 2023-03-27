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
public class ZaakobjectNummeraanduiding extends ZaakobjectBAGObject<ObjectNummeraanduiding> {

    public static final String OBJECT_TYPE_OVERIGE = "nummeraanduiding";

    public ZaakobjectNummeraanduiding() {
    }

    public ZaakobjectNummeraanduiding(final URI zaak, final URI bagObjectUri, final ObjectNummeraanduiding nummeraanduiding) {
        super(zaak, bagObjectUri, Objecttype.OVERIGE, nummeraanduiding);
        setObjectTypeOverige(OBJECT_TYPE_OVERIGE);
    }

}
