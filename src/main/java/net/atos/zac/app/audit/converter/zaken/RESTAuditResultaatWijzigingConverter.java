/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ResultaatWijziging;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditResultaatWijzigingConverter extends AbstractRESTAuditWijzigingConverter<ResultaatWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.RESULTAAT == objectType;
    }

    protected RESTWijziging doConvert(final ResultaatWijziging resultaatWijziging) {
        final Resultaat nieuw = resultaatWijziging.getNieuw();
        final Resultaat oud = resultaatWijziging.getOud();
        if (oud == null) {
            Resultaattype resultaattype = ztcClientService.readResultaattype(nieuw.getResultaattype());
            return new RESTWijziging("Resultaat", "", resultaattype.getOmschrijving());
        }
        if (nieuw == null) {
            Resultaattype resultaattype = ztcClientService.readResultaattype(oud.getResultaattype());
            return new RESTWijziging("Resultaat", resultaattype.getOmschrijving(), "");
        }
        final Resultaattype resultaattypeOud = ztcClientService.readResultaattype(oud.getResultaattype());
        final Resultaattype resultaattypeNieuw = ztcClientService.readResultaattype(nieuw.getResultaattype());
        return new RESTWijziging("Reslutaat", resultaattypeOud.getOmschrijving(), resultaattypeNieuw.getOmschrijving());
    }
}
