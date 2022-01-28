/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ResultaatWijziging;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditResultaatWijzigingConverter extends AbstractAuditWijzigingConverter<ResultaatWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.RESULTAAT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final ResultaatWijziging resultaatWijziging) {
        return Stream.of(new RESTHistorieRegel("resultaat", toWaarde(resultaatWijziging.getOud()), toWaarde(resultaatWijziging.getNieuw())));
    }

    private String toWaarde(final Resultaat resultaat) {
        return resultaat != null ? ztcClientService.readResultaattype(resultaat.getResultaattype()).getOmschrijving() : null;
    }
}
