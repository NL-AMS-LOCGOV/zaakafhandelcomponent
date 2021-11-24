/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;

public class RolNatuurlijkPersoonWijziging extends AuditWijziging<RolNatuurlijkPersoon> {

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ROL;
    }
}
