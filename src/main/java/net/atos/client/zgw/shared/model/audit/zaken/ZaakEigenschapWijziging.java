/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.ZaakEigenschap;

public class ZaakEigenschapWijziging extends AuditWijziging<ZaakEigenschap> {

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ZAAKEIGENSCHAP;
    }
}
