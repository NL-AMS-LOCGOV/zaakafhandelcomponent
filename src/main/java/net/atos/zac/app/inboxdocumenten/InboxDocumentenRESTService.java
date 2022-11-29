/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.inboxdocumenten.converter.RESTInboxDocumentConverter;
import net.atos.zac.app.inboxdocumenten.converter.RESTInboxDocumentListParametersConverter;
import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocument;
import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocumentListParameters;
import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.documenten.model.InboxDocument;
import net.atos.zac.documenten.model.InboxDocumentListParameters;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.UriUtil;

@Singleton
@Path("inboxdocumenten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InboxDocumentenRESTService {

    @Inject
    private InboxDocumentenService inboxDocumentenService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTInboxDocumentConverter inboxDocumentConverter;

    @Inject
    private RESTInboxDocumentListParametersConverter listParametersConverter;

    @Inject
    private PolicyService policyService;

    private static final Logger LOG = Logger.getLogger(InboxDocumentenRESTService.class.getName());

    @PUT
    @Path("")
    public RESTResultaat<RESTInboxDocument> list(final RESTInboxDocumentListParameters restListParameters) {
        PolicyService.assertPolicy(policyService.readWerklijstRechten().getDocumentenInbox());
        final InboxDocumentListParameters listParameters = listParametersConverter.convert(restListParameters);
        return new RESTResultaat<>(inboxDocumentConverter.convert(
                inboxDocumentenService.list(listParameters)), inboxDocumentenService.count(listParameters));
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        PolicyService.assertPolicy(policyService.readWerklijstRechten().getDocumentenInbox());
        final Optional<InboxDocument> inboxDocument = inboxDocumentenService.find(id);
        if (inboxDocument.isEmpty()) {
            return; // reeds verwijderd
        }
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject =
                drcClientService.readEnkelvoudigInformatieobject(
                        inboxDocument.get().getEnkelvoudiginformatieobjectUUID());
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(
                enkelvoudigInformatieobject);
        if (!zaakInformatieobjecten.isEmpty()) {
            final UUID zaakUuid = UriUtil.uuidFromURI(zaakInformatieobjecten.get(0).getZaak());
            LOG.warning(
                    String.format(
                            "Het inbox-document is verwijderd maar het informatieobject is niet verwijderd. Reden: informatieobject '%s' is gekoppeld aan zaak '%s'.",
                            enkelvoudigInformatieobject.getIdentificatie(), zaakUuid));
        } else {
            drcClientService.deleteEnkelvoudigInformatieobject(
                    inboxDocument.get().getEnkelvoudiginformatieobjectUUID());
        }
        inboxDocumentenService.delete(id);
    }
}
