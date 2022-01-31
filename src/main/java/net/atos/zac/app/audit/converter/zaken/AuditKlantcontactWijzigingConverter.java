/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.KlantcontactWijziging;
import net.atos.client.zgw.zrc.model.Klantcontact;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditKlantcontactWijzigingConverter extends AbstractAuditWijzigingConverter<KlantcontactWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.KLANTCONTACT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final KlantcontactWijziging wijziging) {
        return Stream.of(new RESTHistorieRegel("klantcontact", toWaarde(wijziging.getOud()), toWaarde(wijziging.getNieuw())));
    }

    private String toWaarde(final Klantcontact klantcontact) {
        return klantcontact != null ? klantcontact.getToelichting() : null;
    }
}
