/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakWijziging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditZaakWijzigingConverter extends AbstractRESTAuditWijzigingConverter<ZaakWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final ZaakWijziging zaakWijziging) {
        final Zaak nieuw = zaakWijziging.getNieuw();
        final Zaak oud = zaakWijziging.getOud();
        if (oud == null) {
            return new RESTWijziging("Zaak toegevoegd");
        }
        if (nieuw == null) {
            return new RESTWijziging("Zaak verwijderd");
        }

        if (!StringUtils.equals(nieuw.getToelichting(), oud.getToelichting())) {
            return new RESTWijziging("Toelichting", oud.getToelichting(), nieuw.getToelichting());
        }
        if (!StringUtils.equals(nieuw.getOmschrijving(), oud.getOmschrijving())) {
            return new RESTWijziging("Omschrijving", oud.getOmschrijving(), nieuw.getOmschrijving());
        }
        return new RESTWijziging();
    }
}
