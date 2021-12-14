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
        final Klantcontact oud = wijziging.getOud();
        final Klantcontact nieuw = wijziging.getNieuw();
        if (oud == null) {
            return new RESTWijziging(String.format("Klant contact '%s' is toegevoegd", nieuw.getIdentificatie()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Klant contact '%s' is verwijderd", oud.getIdentificatie()));
        }
        return new RESTWijziging("Klant contact", oud.getToelichting(), nieuw.getToelichting());
    }
}
