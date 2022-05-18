/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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
import net.atos.zac.app.inboxdocumenten.converter.RESTInboxDocumentConverter;
import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocument;
import net.atos.zac.app.shared.RESTListParameters;
import net.atos.zac.app.shared.RESTListParametersConverter;
import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.documenten.model.InboxDocument;
import net.atos.zac.util.UriUtil;

@Singleton
@Path("inboxdocumenten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InboxDocumentenRESTService {

    @Inject
    private InboxDocumentenService service;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTInboxDocumentConverter converter;

    private static final Logger LOG = Logger.getLogger(InboxDocumentenRESTService.class.getName());

    @GET
    @Path("")
    public RESTResultaat<RESTInboxDocument> list(@BeanParam final RESTListParameters listParameters) {
        return new RESTResultaat<>(converter.convert(service.list(RESTListParametersConverter.convert(listParameters))), service.count());
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        final InboxDocument inboxDocument = service.find(id);
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject =
                drcClientService.readEnkelvoudigInformatieobject(inboxDocument.getEnkelvoudiginformatieobjectUUID());
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject);
        if (!zaakInformatieobjecten.isEmpty()) {
            final UUID zaakUuid = UriUtil.uuidFromURI(zaakInformatieobjecten.get(0).getZaak());
            LOG.warning(
                    String.format(
                            "Het inbox-document is verwijderd maar het informatieobject is niet verwijderd. Reden: informatieobject '%s' is gekoppeld aan zaak '%s'.",
                            enkelvoudigInformatieobject.getIdentificatie(), zaakUuid));
        } else {
            drcClientService.deleteEnkelvoudigInformatieobject(inboxDocument.getEnkelvoudiginformatieobjectUUID());
        }
        service.delete(id);
    }
}
