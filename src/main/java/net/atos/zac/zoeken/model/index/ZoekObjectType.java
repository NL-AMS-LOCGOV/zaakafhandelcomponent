/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.index;

import net.atos.zac.zoeken.model.TaakZoekObject;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekObject;

public enum ZoekObjectType {
    ZAAK(ZaakZoekObject.class),
    TAAK(TaakZoekObject.class);

    private final Class<? extends ZoekObject> zoekObjectClass;

    ZoekObjectType(final Class<? extends ZoekObject> zoekObjectClass) {
        this.zoekObjectClass = zoekObjectClass;
    }

    public Class<? extends ZoekObject> getZoekObjectClass() {
        return zoekObjectClass;
    }

}
