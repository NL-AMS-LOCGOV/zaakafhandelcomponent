/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.StatusWijziging;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditStatusWijzigingConverter extends AbstractRESTAuditWijzigingConverter<StatusWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.STATUS == objectType;
    }

    protected RESTWijziging doConvert(final StatusWijziging statusWijziging) {
        return new RESTWijziging("Status", statusType(statusWijziging.getOud()), statusType(statusWijziging.getNieuw()));
    }

    private String statusType(final Status status) {
        return status == null ? null : ztcClientService.readStatustype(status.getStatustype()).getOmschrijving();
    }
}
