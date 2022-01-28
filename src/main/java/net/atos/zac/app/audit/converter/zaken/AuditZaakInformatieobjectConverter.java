/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditZaakInformatieobjectConverter extends AbstractAuditWijzigingConverter<ZaakInformatieobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK_INFORMATIEOBJECT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final ZaakInformatieobjectWijziging wijziging) {
        return Stream.of(new RESTHistorieRegel("zaakInformatieobject", toWaarde(wijziging.getOud()), toWaarde(wijziging.getNieuw())));
    }

    private String toWaarde(final ZaakInformatieobject zaakInformatieobject) {
        return zaakInformatieobject != null ? zaakInformatieobject.getTitel() : null;
    }
}
