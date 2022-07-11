/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.policy.PolicyService.assertActie;
import static net.atos.zac.websocket.event.ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.URIUtil;
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
import net.atos.zac.app.informatieobjecten.model.RESTDocumentCreatieGegevens;
import net.atos.zac.app.informatieobjecten.model.RESTDocumentCreatieResponse;
import net.atos.zac.app.informatieobjecten.model.RESTDocumentVerplaatsGegevens;
import net.atos.zac.app.informatieobjecten.model.RESTDocumentVerwijderenGegevens;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieObjectVersieGegevens;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;
import net.atos.zac.app.informatieobjecten.model.RESTFileUpload;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieObjectZoekParameters;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjecttype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieobject;
import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.documentcreatie.DocumentCreatieService;
import net.atos.zac.documentcreatie.model.DocumentCreatieGegevens;
import net.atos.zac.documentcreatie.model.DocumentCreatieResponse;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.documenten.OntkoppeldeDocumentenService;
import net.atos.zac.documenten.model.InboxDocument;
import net.atos.zac.documenten.model.OntkoppeldDocument;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.TaskVariablesService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.exception.FoutmeldingException;
import net.atos.zac.webdav.WebdavHelper;

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
    private TaskVariablesService taskVariablesService;

    @Inject
    private OntkoppeldeDocumentenService ontkoppeldeDocumentenService;

    @Inject
    private InboxDocumentenService inboxDocumentenService;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private EventingService eventingService;

    @Inject
    private RESTZaakInformatieobjectConverter restZaakInformatieobjectConverter;

    @Inject
    private RESTInformatieobjectConverter restInformatieobjectConverter;

    @Inject
    private RESTInformatieobjecttypeConverter restInformatieobjecttypeConverter;

    @Inject
    private RESTHistorieRegelConverter restHistorieRegelConverter;

    @Inject
    private DocumentCreatieService documentCreatieService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private WebdavHelper webdavHelper;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @Inject
    private PolicyService policyService;

    @GET
    @Path("informatieobject/{uuid}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (zaakUUID != null) {
            final Zaak zaak = zrcClientService.readZaak(zaakUUID);
            restEnkelvoudigInformatieobject = restInformatieobjectConverter.convertToREST(enkelvoudigInformatieObject, zaak);
        } else {
            restEnkelvoudigInformatieobject = restInformatieobjectConverter.convertToREST(enkelvoudigInformatieObject);
        }
        assertActie(restEnkelvoudigInformatieobject.acties.lezen);
        return restEnkelvoudigInformatieobject;
    }

    @GET
    @Path("informatieobject/versie/{uuid}/{versie}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid, @PathParam("versie") final int versie) {
        final EnkelvoudigInformatieobject huidigeVersie = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (versie < huidigeVersie.getVersie()) {
            final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobjectVersie(uuid, versie);
            restEnkelvoudigInformatieobject = restInformatieobjectConverter.convertToREST(enkelvoudigInformatieObject);
        } else {
            restEnkelvoudigInformatieobject = restInformatieobjectConverter.convertToREST(huidigeVersie);
        }
        assertActie(restEnkelvoudigInformatieobject.acties.lezen);
        return restEnkelvoudigInformatieobject;
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
                restList.add(restInformatieobjectConverter.convertToREST(informatieobject));
            }
            return restList;
        }
        throw new IllegalStateException("Zoekparameters hebben geen waarde");
    }

    @POST
    @Path("informatieobject/{zaakUuid}/{documentReferentieId}")
    public RESTEnkelvoudigInformatieobject createEnkelvoudigInformatieobject(@PathParam("zaakUuid") final UUID zaakUuid,
            @PathParam("documentReferentieId") final String documentReferentieId, @QueryParam("taakObject") final boolean taakObject,
            final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject) {
        final Zaak zaak = zrcClientService.readZaak(zaakUuid);
        assertActie(policyService.readZaakActies(zaak).getToevoegenDocument());
        final RESTFileUpload file = (RESTFileUpload) httpSession.get().getAttribute("FILE_" + documentReferentieId);
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = taakObject ?
                restInformatieobjectConverter.convertTaakObject(restEnkelvoudigInformatieobject, file) :
                restInformatieobjectConverter.convertZaakObject(restEnkelvoudigInformatieobject, file);

        final ZaakInformatieobject zaakInformatieobject =
                zgwApiService.createZaakInformatieobjectForZaak(zaak, enkelvoudigInformatieobjectWithInhoud, enkelvoudigInformatieobjectWithInhoud.getTitel(),
                                                                enkelvoudigInformatieobjectWithInhoud.getBeschrijving(),
                                                                OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
        if (taakObject) {
            List<UUID> taakdocumenten = taskVariablesService.findTaakdocumenten(documentReferentieId);
            if (taakdocumenten == null) {
                taakdocumenten = new LinkedList<>();
            }
            taakdocumenten.add(URIUtil.parseUUIDFromResourceURI(zaakInformatieobject.getInformatieobject()));
            taskVariablesService.setTaakdocumenten(documentReferentieId, taakdocumenten);
        }
        return restInformatieobjectConverter.convertToREST(zaakInformatieobject);
    }

    @POST
    @Path("informatieobject/verplaats")
    public void verplaatsEnkelvoudigInformatieobject(final RESTDocumentVerplaatsGegevens documentVerplaatsGegevens) {
        final UUID enkelvoudigInformatieobjectUUID = documentVerplaatsGegevens.documentUUID;
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        final Zaak nieuweZaak = zrcClientService.readZaakByID(documentVerplaatsGegevens.nieuweZaakID);
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(informatieobject).getKoppelen());
        assertActie(policyService.readZaakActies(nieuweZaak).getKoppelen());
        final String toelichting = "Verplaatst: %s -> %s".formatted(documentVerplaatsGegevens.bron, nieuweZaak.getIdentificatie());
        if (documentVerplaatsGegevens.vanuitOntkoppeldeDocumenten()) {
            final OntkoppeldDocument ontkoppeldDocument = ontkoppeldeDocumentenService.read(enkelvoudigInformatieobjectUUID);
            zrcClientService.koppelInformatieobject(informatieobject, nieuweZaak, toelichting);
            ontkoppeldeDocumentenService.delete(ontkoppeldDocument.getId());
        } else if (documentVerplaatsGegevens.vanuitInboxDocumenten()) {
            final InboxDocument inboxDocument = inboxDocumentenService.find(enkelvoudigInformatieobjectUUID);
            zrcClientService.koppelInformatieobject(informatieobject, nieuweZaak, toelichting);
            inboxDocumentenService.delete(inboxDocument.getId());
        } else {
            final Zaak oudeZaak = zrcClientService.readZaakByID(documentVerplaatsGegevens.bron);
            zrcClientService.verplaatsInformatieobject(informatieobject, oudeZaak, nieuweZaak);
        }
    }

    @GET
    @Path("informatieobjecttypes/{zaakTypeUuid}")
    public List<RESTInformatieobjecttype> listInformatieobjecttypes(@PathParam("zaakTypeUuid") final UUID zaakTypeID) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaakTypeID);
        return restInformatieobjecttypeConverter.convert(zaaktype.getInformatieobjecttypen());
    }

    @GET
    @Path("informatieobjecttypes/zaak/{zaakUuid}")
    public List<RESTInformatieobjecttype> listInformatieobjecttypesForZaak(@PathParam("zaakUuid") final UUID zaakID) {
        final Zaak zaak = zrcClientService.readZaak(zaakID);
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        return restInformatieobjecttypeConverter.convert(zaaktype.getInformatieobjecttypen());
    }

    @POST
    @Path("informatieobject/upload/{uuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("uuid") final UUID uuid, @MultipartForm final RESTFileUpload data) {
        httpSession.get().setAttribute("FILE_" + uuid, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("zaak/{uuid}")
    public List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(@PathParam("uuid") final UUID uuid) {
        final Zaak zaak = zrcClientService.readZaak(uuid);
        assertActie(policyService.readZaakActies(zaak).getLezen());
        return listEnkelvoudigInformatieobjectenVoorZaak(zaak.getUrl());
    }

    @GET
    @Path("informatieobject/{uuid}/zaken")
    public List<RESTZaakInformatieobject> listZaakInformatieobjecten(@PathParam("uuid") final UUID uuid) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(uuid).getLezen());
        final List<RESTZaakInformatieobject> restList = new ArrayList<>();
        zrcClientService.listZaakinformatieobjecten(drcClientService.readEnkelvoudigInformatieobject(uuid))
                .forEach(zaakInformatieobject -> restList.add(restZaakInformatieobjectConverter.convert(zaakInformatieobject)));
        return restList;
    }

    @GET
    @Path("informatieobject/{uuid}/edit")
    public Response editEnkelvoudigInformatieobjectInhoud(@PathParam("uuid") final UUID uuid, @Context final UriInfo uriInfo) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(uuid).getBewerken());
        final URI redirectURI = webdavHelper.createRedirectURL(uuid, uriInfo);
        return Response.ok(redirectURI).build();
    }

    @GET
    @Path("/informatieobject/{uuid}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response readFile(@PathParam("uuid") final UUID uuid) {
        return readFile(uuid, null);
    }

    @DELETE
    @Path("/informatieobject/{uuid}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response deleteEnkelvoudigInformatieObject(@PathParam("uuid") final UUID uuid, final RESTDocumentVerwijderenGegevens documentVerwijderenGegevens) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(uuid, documentVerwijderenGegevens.zaakUuid).getVerwijderen());
        zgwApiService.removeEnkelvoudigInformatieObjectFromZaak(documentVerwijderenGegevens.zaakUuid, uuid, documentVerwijderenGegevens.reden);
        return Response.noContent().build();
    }

    @GET
    @Path("/informatieobject/{uuid}/{versie}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response readFile(@PathParam("uuid") final UUID uuid, @PathParam("versie") final Integer versie) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieObject).getDownloaden());
        try (final ByteArrayInputStream inhoud = (versie != null) ?
                drcClientService.downloadEnkelvoudigInformatieobjectVersie(uuid, versie) :
                drcClientService.downloadEnkelvoudigInformatieobject(uuid)) {
            return Response.ok(inhoud)
                    .header("Content-Disposition", "attachment; filename=\"" + enkelvoudigInformatieObject.getBestandsnaam() + "\"")
                    .build();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("informatieobject/{uuid}/huidigeversie")
    public RESTEnkelvoudigInformatieObjectVersieGegevens readHuidigeVersieInformatieObject(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieObject).getLezen());
        return restInformatieobjectConverter.convertToRESTEnkelvoudigInformatieObjectVersieGegevens(enkelvoudigInformatieObject);
    }

    @POST
    @Path("/informatieobject/update")
    public RESTEnkelvoudigInformatieobject updateEnkelvoudigInformatieobject(
            final RESTEnkelvoudigInformatieObjectVersieGegevens enkelvoudigInformatieObjectVersieGegevens) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                enkelvoudigInformatieObjectVersieGegevens.uuid);
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobject).getToevoegenNieuweVersie());
        final String loggedInUserId = loggedInUserInstance.get().getId();
        boolean tempLock = false;

        try {
            final EnkelvoudigInformatieObjectLock lock;
            if (enkelvoudigInformatieobject.getLocked()) {
                lock = enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieobject.getUUID());
                if (lock == null) {
                    throw new FoutmeldingException("Document kan niet worden aangepast omdat het is gelocked met onbekende lock.");
                }
            } else {
                lock = enkelvoudigInformatieObjectLockService.createLock(enkelvoudigInformatieobject.getUUID(), loggedInUserId);
                tempLock = true;
            }
            final RESTFileUpload file = (RESTFileUpload) httpSession.get().getAttribute("FILE_" + enkelvoudigInformatieObjectVersieGegevens.zaakUuid);
            final EnkelvoudigInformatieobjectWithInhoudAndLock returnObject = drcClientService.updateEnkelvoudigInformatieobject(
                    enkelvoudigInformatieobject.getUUID(), enkelvoudigInformatieObjectVersieGegevens.toelichting,
                    restInformatieobjectConverter.convert(enkelvoudigInformatieObjectVersieGegevens, lock.getLock(), file));
            return restInformatieobjectConverter.convertToREST(returnObject);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(enkelvoudigInformatieobject.getUUID());
            }
        }
    }

    @POST
    @Path("/informatieobject/{uuid}/lock")
    public Response lockDocument(@PathParam("uuid") final UUID uuid) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(uuid).getVergrendelen());
        enkelvoudigInformatieObjectLockService.createLock(uuid, loggedInUserInstance.get().getId());
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/unlock")
    public Response unlockDocument(@PathParam("uuid") final UUID uuid) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(uuid).getOntgrendelen());
        enkelvoudigInformatieObjectLockService.deleteLock(uuid);
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @GET
    @Path("informatieobject/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID enkelvoudigInformatieobjectUUID) {
        assertActie(policyService.readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobjectUUID).getLezen());
        List<AuditTrailRegel> auditTrail = drcClientService.listAuditTrail(enkelvoudigInformatieobjectUUID);
        return restHistorieRegelConverter.convert(auditTrail);
    }

    @POST
    @Path("/documentcreatie")
    public RESTDocumentCreatieResponse createDocument(final RESTDocumentCreatieGegevens restDocumentCreatieGegevens) {
        assertActie(policyService.readZaakActies(restDocumentCreatieGegevens.zaakUUID).getCreeerenDocument());
        final DocumentCreatieGegevens documentCreatieGegevens = new DocumentCreatieGegevens(restDocumentCreatieGegevens.zaakUUID,
                                                                                            restDocumentCreatieGegevens.informatieobjecttypeUUID,
                                                                                            restDocumentCreatieGegevens.taskId);
        documentCreatieGegevens.setTitel(restDocumentCreatieGegevens.titel);
        final DocumentCreatieResponse documentCreatieResponse = documentCreatieService.creeerDocumentAttendedSD(documentCreatieGegevens);
        return new RESTDocumentCreatieResponse(documentCreatieResponse.getRedirectUrl(), documentCreatieResponse.getMessage());
    }

    private List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(final URI zaakURI) {
        final List<RESTEnkelvoudigInformatieobject> restList = new ArrayList<>();
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setZaak(zaakURI);
        final List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(parameters);
        zaakInformatieobjects.forEach(zaakInformatieObject -> restList.add(restInformatieobjectConverter.convertToREST(zaakInformatieObject)));
        return restList;
    }
}
