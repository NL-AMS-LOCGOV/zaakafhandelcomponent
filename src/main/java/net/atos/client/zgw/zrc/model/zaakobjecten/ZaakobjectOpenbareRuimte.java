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
public class ZaakobjectOpenbareRuimte extends ZaakobjectBAGObject<ObjectOpenbareRuimte> {

    public ZaakobjectOpenbareRuimte() {
    }

    public ZaakobjectOpenbareRuimte(final URI zaak, final URI bagobjectURI, final ObjectOpenbareRuimte objectOpenbareRuimte) {
        super(zaak, bagobjectURI, Objecttype.OPENBARE_RUIMTE, objectOpenbareRuimte);
    }
}
