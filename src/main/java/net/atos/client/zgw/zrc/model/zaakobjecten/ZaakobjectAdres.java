/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;

import net.atos.client.zgw.zrc.model.Objecttype;

/**
 * ZaakobjectAdres
 */
public class ZaakobjectAdres extends ZaakobjectBAGObject<ObjectAdres> {

    public ZaakobjectAdres() {
    }

    public ZaakobjectAdres(final URI zaak, final URI bagobjectURI, final ObjectAdres adres) {
        super(zaak, bagobjectURI, Objecttype.ADRES, adres);
    }
}
