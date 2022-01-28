/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import static net.atos.zac.util.ConfigurationService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.audit.converter.RESTHistorieRegelConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjecttypeConverter;
import net.atos.zac.app.informatieobjecten.converter.RESTZaakInformatieobjectConverter;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieObjectZoekParameters;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjecttype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieobject;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.util.UriUtil;

@Singleton
@Path("informatieobjecten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InformatieObjectenRESTService {

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakInformatieobjectConverter restZaakInformatieobjectConverter;

    @Inject
    private RESTInformatieobjectConverter restInformatieobjectConverter;

    @Inject
    private RESTInformatieobjecttypeConverter restInformatieobjecttypeConverter;

    @Inject
    private RESTHistorieRegelConverter restHistorieRegelConverter;

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @GET
    @Path("informatieobject/{uuid}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        return restInformatieobjectConverter.convert(enkelvoudigInformatieObject);
    }

    @PUT
    @Path("informatieobjectenList")
    public List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjecten(final RESTInformatieObjectZoekParameters zoekParameters) {
        if (zoekParameters.zaakUUID != null) {
            final Zaak zaak = zrcClientService.readZaak(zoekParameters.zaakUUID);
            return listEnkelvoudigInformatieobjectenVoorZaak(zaak.getUrl());
        } else if (zoekParameters.zaakURI != null) {
            return listEnkelvoudigInformatieobjectenVoorZaak(zoekParameters.zaakURI);
        } else if (zoekParameters.UUIDs != null) {
            final List<RESTEnkelvoudigInformatieobject> restList = new ArrayList<>();
            for (UUID uuid : zoekParameters.UUIDs) {
                final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
                restList.add(restInformatieobjectConverter.convert(informatieobject));
            }
            return restList;
        }
        throw new IllegalStateException("Zoekparameters hebben geen waarde");
    }

    @POST
    @Path("informatieobject/{zaakUuid}")
    public UUID createEnkelvoudigInformatieobject(@PathParam("zaakUuid") final UUID zaakUuid,
            final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject) {
        final Zaak zaak = zrcClientService.readZaak(zaakUuid);
        final RESTFileUpload file = (RESTFileUpload) httpSession.get().getAttribute("FILE_" + zaakUuid);
        final EnkelvoudigInformatieobjectWithInhoud data = restInformatieobjectConverter.convert(restEnkelvoudigInformatieobject, file);
        final ZaakInformatieobject zaakInformatieobject = zgwApiService.createZaakInformatieobjectForZaak(zaak, data, restEnkelvoudigInformatieobject.titel,
                                                                                                          restEnkelvoudigInformatieobject.beschrijving,
                                                                                                          OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
        return UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject());
    }

    @GET
    @Path("informatieobjecttypes/{zaakTypeUuid}")
    public List<RESTInformatieobjecttype> listInformatieobjecttypes(@PathParam("zaakTypeUuid") final UUID zaakTypeID) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaakTypeID);
        return restInformatieobjecttypeConverter.convert(zaaktype.getInformatieobjecttypen());
    }


    @POST
    @Path("informatieobject/upload/{zaakUuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("zaakUuid") final String zaakUuid, @MultipartForm final RESTFileUpload data) {
        httpSession.get().setAttribute("FILE_" + zaakUuid, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("zaak/{uuid}")
    public List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        return listEnkelvoudigInformatieobjectenVoorZaak(zaak.getUrl());
    }

    @GET
    @Path("informatieobject/{uuid}/zaken")
    public List<RESTZaakInformatieobject> listZaakInformatieobjecten(@PathParam("uuid") final UUID uuid) {
        final List<RESTZaakInformatieobject> restList = new ArrayList<>();
        listZaakInformatieobjectenHelper(uuid)
                .forEach(i -> restList.add(restZaakInformatieobjectConverter.convert(i)));
        return restList;
    }

    @GET
    @Path("/informatieobject/{uuid}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response readFile(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        try (final ByteArrayInputStream inhoud = drcClientService.downloadEnkelvoudigInformatieobject(uuid, enkelvoudigInformatieObject.getVersie())) {
            return Response.ok(inhoud)
                    .header("Content-Disposition", "attachment; filename=\"" + enkelvoudigInformatieObject.getBestandsnaam() + "\"")
                    .build();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/informatieobject/{uuid}/lock")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response lockDocument(@PathParam("uuid") final UUID uuid) {
        drcClientService.lockEnkelvoudigInformatieobject(uuid, lockEigenaar());
        return Response.noContent().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/unlock")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response unlockDocument(@PathParam("uuid") final UUID uuid) {
        drcClientService.unlockEnkelvoudigInformatieobject(uuid, lockEigenaar());
        return Response.noContent().build();
    }

    @GET
    @Path("informatieobject/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID uuid) {
        List<AuditTrailRegel> auditTrail = drcClientService.listAuditTrail(uuid);
        return restHistorieRegelConverter.convert(auditTrail);
    }

    private List<ZaakInformatieobject> listZaakInformatieobjectenHelper(final UUID uuid) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(drcClientService.readEnkelvoudigInformatieobject(uuid).getUrl());
        return zrcClientService.listZaakinformatieobjecten(parameters);
    }

    private String lockEigenaar() {
        return ingelogdeMedewerker.get().getGebruikersnaam();
    }

    private List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(final URI zaakURI) {
        final List<RESTEnkelvoudigInformatieobject> restList = new ArrayList<>();
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setZaak(zaakURI);
        final List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(parameters);
        zaakInformatieobjects.forEach(zaakInformatieObject -> restList.add(restInformatieobjectConverter.convert(zaakInformatieObject)));
        return restList;
    }
}
