/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Afleidingswijze;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.util.PeriodUtil;
import net.atos.zac.util.UriUtil;

public class RESTResultaattypeConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTResultaattype convertResultaattype(final Resultaattype resultaattype) {
        final RESTResultaattype restResultaattype = new RESTResultaattype();
        restResultaattype.id = UriUtil.uuidFromURI(resultaattype.getUrl());
        restResultaattype.naam = resultaattype.getOmschrijving();
        restResultaattype.vervaldatumBesluitVerplicht =
                Afleidingswijze.VERVALDATUM_BESLUIT.equals(resultaattype.getBrondatumArchiefprocedure().getAfleidingswijze());
        restResultaattype.archiefNominatie = resultaattype.getArchiefnominatie().name();
        restResultaattype.toelichting = resultaattype.getToelichting();
        restResultaattype.naamGeneriek = resultaattype.getOmschrijvingGeneriek();
        restResultaattype.archiefTermijn = PeriodUtil.format(resultaattype.getArchiefactietermijn());
        return restResultaattype;
    }

    public RESTResultaattype convertResultaattypeUri(final URI resultaattypeURI) {
        return convertResultaattype(ztcClientService.readResultaattype(resultaattypeURI));
    }

    public List<RESTResultaattype> convertResultaattypes(final List<Resultaattype> resultaattypes) {
        return resultaattypes.stream()
                .map(this::convertResultaattype)
                .toList();
    }

}
