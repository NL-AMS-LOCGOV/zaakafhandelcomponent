/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.zac.app.zaken.model.RESTBesluittype;

public class RESTBesluittypeConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTBesluittype convertToRESTBesluittype(final Besluittype besluittype) {
        final RESTBesluittype restBesluittype = new RESTBesluittype();
        restBesluittype.url = besluittype.getUrl();
        restBesluittype.naam = besluittype.getOmschrijving();
        restBesluittype.toelichting = besluittype.getToelichting();
        return restBesluittype;
    }

    public RESTBesluittype convertToRESTBesluittype(final URI besluittypeURI) {
        return convertToRESTBesluittype(ztcClientService.readBesluittype(besluittypeURI));
    }

    public List<RESTBesluittype> convertToRESTBesluittypes(final List<Besluittype> besluittypes) {
        return besluittypes.stream()
                .map(this::convertToRESTBesluittype)
                .toList();
    }

}
