/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit.zaken.objecten;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;

public class ZaakobjectWoonplaatsWijziging extends AuditWijziging<ZaakobjectWoonplaats> {

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ZAAKOBJECT;
    }
}
