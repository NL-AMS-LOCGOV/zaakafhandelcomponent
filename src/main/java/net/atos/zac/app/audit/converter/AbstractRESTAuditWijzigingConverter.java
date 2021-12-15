/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.zac.app.audit.model.RESTWijziging;

public abstract class AbstractRESTAuditWijzigingConverter<W extends AuditWijziging<?>> {

    public RESTWijziging convert(final AuditWijziging<?> wijziging) {
        return doConvert((W) wijziging);
    }

    public abstract boolean supports(final ObjectType objectType);

    protected abstract RESTWijziging doConvert(final W wijziging);
}
