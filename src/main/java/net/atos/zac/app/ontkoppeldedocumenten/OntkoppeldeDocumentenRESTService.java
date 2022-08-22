/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten;

import static net.atos.zac.policy.PolicyService.assertActie;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTOntkoppeldDocumentConverter;
import net.atos.zac.app.ontkoppeldedocumenten.converter.RESTOntkoppeldDocumentListParametersConverter;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocument;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocumentListParameters;
import net.atos.zac.app.ontkoppeldedocumenten.model.RESTOntkoppeldDocumentResultaat;
import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.documenten.model.OntkoppeldDocument;
import net.atos.zac.documenten.model.OntkoppeldDocumentListParameters;
import net.atos.zac.documenten.model.OntkoppeldeDocumentenResultaat;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.UriUtil;

@Singleton
@Path("ontkoppeldedocumenten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OntkoppeldeDocumentenRESTService {

    @Inject
    private OntkoppeldeDocumentenService ontkoppeldeDocumentenService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTOntkoppeldDocumentConverter ontkoppeldDocumentConverter;

    @Inject
    private RESTOntkoppeldDocumentListParametersConverter listParametersConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private PolicyService policyService;

    @PUT
    @Path("")
    public RESTResultaat<RESTOntkoppeldDocument> list(final RESTOntkoppeldDocumentListParameters restListParameters) {
        assertActie(policyService.readAppActies().getDocumenten());
        final OntkoppeldDocumentListParameters listParameters = listParametersConverter.convert(restListParameters);
        final OntkoppeldeDocumentenResultaat resultaat = ontkoppeldeDocumentenService.getResultaat(listParameters);
        final RESTOntkoppeldDocumentResultaat restOntkoppeldDocumentResultaat =
                new RESTOntkoppeldDocumentResultaat(ontkoppeldDocumentConverter.convert(resultaat.getItems()), resultaat.getCount());
        final List<String> ontkoppeldDoor = resultaat.getOntkoppeldDoorFilter();
        if (CollectionUtils.isEmpty(ontkoppeldDoor)) {
            if (restListParameters.ontkoppeldDoor != null) {
                restOntkoppeldDocumentResultaat.filterOntkoppeldDoor = List.of(restListParameters.ontkoppeldDoor);
            }
        } else {
            restOntkoppeldDocumentResultaat.filterOntkoppeldDoor = userConverter.convertUserIds(ontkoppeldDoor);
        }
        return restOntkoppeldDocumentResultaat;
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        assertActie(policyService.readAppActies().getDocumenten());
        final OntkoppeldDocument ontkoppeldDocument = ontkoppeldeDocumentenService.find(id);
        if (ontkoppeldDocument == null) {
            return; // reeds verwijderd
        }
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(ontkoppeldDocument.getDocumentUUID());
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject);
        if (!zaakInformatieobjecten.isEmpty()) {
            final UUID zaakUuid = UriUtil.uuidFromURI(zaakInformatieobjecten.get(0).getZaak());
            throw new IllegalStateException(String.format("Informatieobject is gekoppeld aan zaak '%s'", zaakUuid));
        }
        drcClientService.deleteEnkelvoudigInformatieobject(ontkoppeldDocument.getDocumentUUID());
        ontkoppeldeDocumentenService.delete(ontkoppeldDocument.getId());
    }
}
