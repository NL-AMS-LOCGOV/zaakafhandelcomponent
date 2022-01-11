/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakobjectWijziging;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditZaakobjectWijzigingConverter extends AbstractRESTAuditWijzigingConverter<ZaakobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAKOBJECT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final ZaakobjectWijziging wijziging) {
        final Zaakobject nieuw = wijziging.getNieuw();
        final Zaakobject oud = wijziging.getOud();
        if (oud == null) {
            return new RESTWijziging(String.format("%s toegevoegd", nieuw.getObjectType().toValue()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("%s verwijderd", oud.getObjectType().toValue()));
        }
        return new RESTWijziging(String.format("%s gewijzigd", nieuw.getObjectType().toValue()));
    }
}
