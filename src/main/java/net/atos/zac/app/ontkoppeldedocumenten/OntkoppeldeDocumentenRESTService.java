/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTListParametersConverter;
import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTOntkoppeldDocumentConverter;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocument;
import net.atos.zac.app.shared.RESTListParameters;
import net.atos.zac.app.shared.RESTResult;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.documenten.model.OntkoppeldDocument;
import net.atos.zac.util.UriUtil;

@Singleton
@Path("ontkoppeldedocumenten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OntkoppeldeDocumentenRESTService {

    @Inject
    private OntkoppeldeDocumentenService service;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTOntkoppeldDocumentConverter converter;

    @GET
    @Path("")
    public RESTResult<RESTOntkoppeldDocument> list(@BeanParam final RESTListParameters listParameters) {
        return new RESTResult<>(converter.convert(service.list(RESTListParametersConverter.convert(listParameters))), service.count());
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        final OntkoppeldDocument ontkoppeldDocument = service.read(id);
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(ontkoppeldDocument.getDocumentUUID());
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject);
        if (!zaakInformatieobjecten.isEmpty()) {
            final UUID zaakUuid = UriUtil.uuidFromURI(zaakInformatieobjecten.get(0).getZaak());
            throw new IllegalStateException(String.format("Informatieobject is gekoppeld aan zaak '%s'", zaakUuid));
        }
        drcClientService.deleteEnkelvoudigInformatieobject(ontkoppeldDocument.getDocumentUUID());
        service.delete(ontkoppeldDocument.getId());
    }
}
