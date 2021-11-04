/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;


import static net.atos.zac.websocket.event.SchermObjectTypeEnum.DOCUMENT;
import static net.atos.zac.websocket.event.SchermObjectTypeEnum.ZAAK_DOCUMENTEN;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import net.atos.client.zgw.drc.DRCClient;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectData;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
import net.atos.client.zgw.zrc.model.ZaakinformatieobjectListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieObjectConverter;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjecttypeConverter;
import net.atos.zac.app.informatieobjecten.converter.RESTZaakInformatieObjectConverter;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieObject;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjecttype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieObject;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.service.EventingService;
import net.atos.zac.util.UriUtil;

@Path("informatieobjecten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InformatieObjectenRESTService {

    @Inject
    private DRCClient drcClient;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClient zrcClient;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakInformatieObjectConverter restZaakInformatieObjectConverter;

    @Inject
    private RESTInformatieObjectConverter restInformatieObjectConverter;

    @Inject
    private RESTInformatieobjecttypeConverter restInformatieobjecttypeConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @Inject
    @ActiveSession
    private HttpSession httpSession;

    @EJB
    private EventingService eventingService;

    @GET
    @Path("informatieobject/{uuid}")
    public RESTInformatieObject getObject(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClient.enkelvoudigInformatieobjectRead(uuid);
        return restInformatieObjectConverter.convert(enkelvoudigInformatieObject);
    }

    @POST
    @Path("informatieobject/{zaakUuid}")
    public UUID postObject(@PathParam("zaakUuid") final UUID zaakUuid, final RESTInformatieObject restInformatieObject) {
        final Zaak zaak = zrcClient.zaakRead(zaakUuid);
        final RESTFileUpload file = (RESTFileUpload) httpSession.getAttribute("FILE_" + zaakUuid);
        final EnkelvoudigInformatieobjectData data = restInformatieObjectConverter.convert(restInformatieObject, file);
        final ZaakInformatieObject zaakInformatieObject = zgwApiService.addInformatieobjectToZaak(
                zaak.getUrl(), data, restInformatieObject.titel, restInformatieObject.beschrijving, "-");
        eventingService.versturen(DOCUMENT.toevoeging(zaakInformatieObject.getInformatieobject()));
        eventingService.versturen(ZAAK_DOCUMENTEN.wijziging(zaak));
        return UriUtil.uuidFromURI(zaakInformatieObject.getInformatieobject());
    }

    @GET
    @Path("informatieobjecttypes/{zaakTypeUuid}")
    public List<RESTInformatieobjecttype> getInformatieobjecttypes(@PathParam("zaakTypeUuid") final UUID zaakTypeID) {
        final Zaaktype zaaktype = ztcClientService.getZaaktype(zaakTypeID);
        return restInformatieobjecttypeConverter.convert(zaaktype.getInformatieobjecttypen());
    }


    @POST
    @Path("/informatieobject/upload/{zaakUuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("zaakUuid") final String zaakUuid, @MultipartForm final RESTFileUpload data) {
        httpSession.setAttribute("FILE_" + zaakUuid, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("zaak/{uuid}")
    public List<RESTInformatieObject> getObjectenForZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClient.zaakRead(uuid);
        final List<RESTInformatieObject> restList = new ArrayList<>();
        final ZaakinformatieobjectListParameters parameters = new ZaakinformatieobjectListParameters();
        parameters.setZaak(zaak.getUrl());
        final List<ZaakInformatieObject> zaakInformatieObjects = zrcClient.zaakinformatieobjectList(parameters);
        zaakInformatieObjects.forEach(zaakInformatieObject -> restList.add(restInformatieObjectConverter.convert(zaakInformatieObject)));
        return restList;
    }

    @GET
    @Path("informatieobject/{uuid}/zaken")
    public List<RESTZaakInformatieObject> getGekoppeldeZaken(@PathParam("uuid") final UUID uuid) {
        final List<RESTZaakInformatieObject> restList = new ArrayList<>();
        getZaakInformatieObjects(uuid)
                .forEach(i -> restList.add(restZaakInformatieObjectConverter.convert(i)));
        return restList;
    }

    private List<ZaakInformatieObject> getZaakInformatieObjects(final UUID uuid) {
        final ZaakinformatieobjectListParameters parameters = new ZaakinformatieobjectListParameters();
        parameters.setInformatieobject(drcClient.enkelvoudigInformatieobjectRead(uuid).getUrl());
        return zrcClient.zaakinformatieobjectList(parameters);
    }

    @GET
    @Path("/informatieobject/{uuid}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClient.enkelvoudigInformatieobjectRead(uuid);
        try (final Response response = drcClient.enkelvoudigInformatieobjectDownload(uuid, enkelvoudigInformatieObject.getVersie())) {
            response.bufferEntity(); // Altijd voordat je fromResponse gebruikt.
            return Response.ok(response.getEntity())
                    .header("Content-Disposition", "attachment; filename=\"" + enkelvoudigInformatieObject.getBestandsnaam() + "\"")
                    .build();
        }
    }

    @POST
    @Path("/informatieobject/{uuid}/lock")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response lockDocument(@PathParam("uuid") final UUID uuid) {
        drcClientService.lockEnkelvoudigInformatieobject(uuid, lockEigenaar());
        eventingService.versturen(DOCUMENT.wijziging(uuid));
        getZaakInformatieObjects(uuid)
                .forEach(zaak -> eventingService.versturen(ZAAK_DOCUMENTEN.wijziging(zaak.getZaak())));
        return Response.noContent().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/unlock")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response unlockDocument(@PathParam("uuid") final UUID uuid) {
        drcClientService.unlockEnkelvoudigInformatieobject(uuid, lockEigenaar());
        eventingService.versturen(DOCUMENT.wijziging(uuid));
        getZaakInformatieObjects(uuid)
                .forEach(zaak -> eventingService.versturen(ZAAK_DOCUMENTEN.wijziging(zaak.getZaak())));
        return Response.noContent().build();
    }

    private String lockEigenaar() {
        return ingelogdeMedewerker.getGebruikersnaam();
    }
}
