/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import net.atos.zac.app.shared.RESTZaakobject;

public class RESTBAGObjectGegevens extends RESTZaakobject<RESTBAGObject> {

    public String redenWijzigen;

    public RESTBAGObject getBagObject() {
        return zaakobject;
    }

}
