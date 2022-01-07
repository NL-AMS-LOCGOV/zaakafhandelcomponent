/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditZaakInformatieobjectConverter extends AbstractRESTAuditWijzigingConverter<ZaakInformatieobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK_INFORMATIEOBJECT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final ZaakInformatieobjectWijziging wijziging) {
        final ZaakInformatieobject nieuw = wijziging.getNieuw();
        final ZaakInformatieobject oud = wijziging.getOud();

        if (oud == null) {
            return new RESTWijziging(String.format("Document '%s' toegevoegd", nieuw.getTitel()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Document '%s' verwijderd", oud.getTitel()));
        }
        return new RESTWijziging(String.format("Document '%s' gewijzigd", nieuw.getTitel()));
    }
}
