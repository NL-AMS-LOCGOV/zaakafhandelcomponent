/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.productaanvragen;

import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.zac.aanvraag.InboxProductaanvraagService;
import net.atos.zac.aanvraag.model.InboxProductaanvraagListParameters;
import net.atos.zac.aanvraag.model.InboxProductaanvraagResultaat;
import net.atos.zac.app.productaanvragen.converter.RESTInboxProductaanvraagConverter;
import net.atos.zac.app.productaanvragen.converter.RESTInboxProductaanvraagListParametersConverter;
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraag;
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraagListParameters;
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraagResultaat;
import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.policy.PolicyService;

@Singleton
@Path("inbox-productaanvragen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InboxProductaanvragenRESTService {

    @Inject
    private DRCClientService drcClientService;


    @Inject
    private PolicyService policyService;

    @Inject
    private InboxProductaanvraagService inboxProductaanvraagService;

    @Inject
    private RESTInboxProductaanvraagConverter inboxProductaanvraagConverter;

    @Inject
    private RESTInboxProductaanvraagListParametersConverter listParametersConverter;

    @PUT
    @Path("")
    public RESTResultaat<RESTInboxProductaanvraag> list(final RESTInboxProductaanvraagListParameters restListParameters) {
        assertPolicy(policyService.readWerklijstRechten().getInbox());
        final InboxProductaanvraagListParameters listParameters = listParametersConverter.convert(restListParameters);
        final InboxProductaanvraagResultaat resultaat = inboxProductaanvraagService.list(listParameters);
        final RESTInboxProductaanvraagResultaat restInboxProductaanvraagResultaat =
                new RESTInboxProductaanvraagResultaat(inboxProductaanvraagConverter.convert(resultaat.getItems()), resultaat.getCount());
        final List<String> types = resultaat.getTypeFilter();
        if (CollectionUtils.isEmpty(types)) {
            if (restListParameters.type != null) {
                restInboxProductaanvraagResultaat.filterType = List.of(restListParameters.type);
            }
        } else {
            restInboxProductaanvraagResultaat.filterType = types;
        }
        return restInboxProductaanvraagResultaat;
    }

    @GET
    @Path("/{uuid}/pdfPreview")
    public Response pdfPreview(@PathParam("uuid") final UUID uuid) {
        assertPolicy(policyService.readWerklijstRechten().getInbox());
        EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        try (ByteArrayInputStream is = drcClientService.downloadEnkelvoudigInformatieobject(uuid)) {
            return Response.ok(is)
                    .header("Content-Disposition",
                            "inline; filename=\"%s\"".formatted(enkelvoudigInformatieobject.getBestandsnaam()))
                    .header("Content-Type", "application/pdf").build();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        PolicyService.assertPolicy(policyService.readWerklijstRechten().getInboxProductaanvragenVerwijderen());
        inboxProductaanvraagService.delete(id);
    }
}
