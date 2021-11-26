/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.model.ObjectType;

public class EnkelvoudigInformatieobjectWijziging extends AuditWijziging<EnkelvoudigInformatieobject> {

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ENKELVOUDIG_INFORMATIEOBJECT;
    }
}
