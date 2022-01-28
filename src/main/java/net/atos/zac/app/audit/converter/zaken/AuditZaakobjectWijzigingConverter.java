/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakobjectWijziging;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditZaakobjectWijzigingConverter extends AbstractAuditWijzigingConverter<ZaakobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAKOBJECT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final ZaakobjectWijziging wijziging) {
        return Stream.of(new RESTHistorieRegel("zaakObject", toWaarde(wijziging.getOud()), toWaarde(wijziging.getNieuw())));
    }

    private String toWaarde(final Zaakobject zaakobject) {
        return zaakobject != null ? zaakobject.getObjectType().toValue() : null;
    }
}
