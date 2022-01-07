/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.KlantcontactWijziging;
import net.atos.client.zgw.zrc.model.Klantcontact;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditKlantcontactWijzigingConverter extends AbstractRESTAuditWijzigingConverter<KlantcontactWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.KLANTCONTACT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final KlantcontactWijziging wijziging) {
        return new RESTWijziging("Klantcontact", klantContact(wijziging.getOud()), klantContact(wijziging.getNieuw()));
    }

    private String klantContact(final Klantcontact klantcontact) {
        return klantcontact == null ? null : klantcontact.getToelichting();
    }
}
