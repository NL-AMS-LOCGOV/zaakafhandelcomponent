/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjecttype;

public class RESTInformatieobjecttypeConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTInformatieobjecttype convert(final Informatieobjecttype type) {
        final RESTInformatieobjecttype restType = new RESTInformatieobjecttype();
        restType.url = type.getUrl();
        restType.concept = type.getConcept();
        restType.omschrijving = type.getOmschrijving();
        restType.vertrouwelijkheidaanduiding = type.getVertrouwelijkheidaanduiding();
        return restType;
    }

    public List<RESTInformatieobjecttype> convert(final Set<URI> informatieobjecttypen) {
        return informatieobjecttypen.stream().map(ztcClientService::readInformatieobjecttype).map(this::convert).collect(Collectors.toList());

    }
}
