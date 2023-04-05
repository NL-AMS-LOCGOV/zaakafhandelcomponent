/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectProductAanvraag
 */
public class ZaakobjectProductaanvraag extends Zaakobject {

    public static final String OBJECT_TYPE_OVERIGE = "ProductAanvraag";

    /**
     * Constructor for JSONB deserialization
     */
    public ZaakobjectProductaanvraag() {
    }

    /**
     * Constructor with required attributes
     */
    public ZaakobjectProductaanvraag(final URI zaak, final URI productaanvraag) {
        super(zaak, productaanvraag, Objecttype.OVERIGE);
        setObjectTypeOverige(OBJECT_TYPE_OVERIGE);
    }

    @Override
    public String getWaarde() {
        return StringUtils.substringAfterLast(getObject().getPath(), "/");
    }
}
