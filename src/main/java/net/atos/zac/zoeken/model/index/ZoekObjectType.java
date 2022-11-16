/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.index;

import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;
import net.atos.zac.zoeken.model.zoekobject.TaakZoekObject;
import net.atos.zac.zoeken.model.zoekobject.ZaakZoekObject;

public enum ZoekObjectType {
    TAAK(TaakZoekObject.class),
    ZAAK(ZaakZoekObject.class),
    DOCUMENT(DocumentZoekObject.class);

    private final Class<? extends ZoekObject> zoekObjectClass;

    ZoekObjectType(final Class<? extends ZoekObject> zoekObjectClass) {
        this.zoekObjectClass = zoekObjectClass;
    }

    public Class<? extends ZoekObject> getZoekObjectClass() {
        return zoekObjectClass;
    }

}
