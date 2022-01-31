/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.StatusWijziging;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditStatusWijzigingConverter extends AbstractAuditWijzigingConverter<StatusWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.STATUS == objectType;
    }

    protected Stream<RESTHistorieRegel> doConvert(final StatusWijziging statusWijziging) {
        return Stream.of(new RESTHistorieRegel("status", toWaarde(statusWijziging.getOud()), toWaarde(statusWijziging.getNieuw())));
    }

    private String toWaarde(final Status status) {
        return status != null ? ztcClientService.readStatustype(status.getStatustype()).getOmschrijving() : null;
    }
}
