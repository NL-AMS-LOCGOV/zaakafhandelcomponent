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
import java.util.stream.Collectors;

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
import javax.ws.rs.core.StreamingOutput;
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
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
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
import net.atos.zac.app.informatieobjecten.model.RESTGekoppeldeZaakEnkelvoudigInformatieObject;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieObjectZoekParameters;
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjecttype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieobject;
import net.atos.zac.app.zaken.converter.RESTGerelateerdeZaakConverter;
import net.atos.zac.app.zaken.model.RelatieType;
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
import net.atos.zac.util.UriUtil;
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
    private RESTZaakInformatieobjectConverter zaakInformatieobjectConverter;

    @Inject
    private RESTInformatieobjectConverter informatieobjectConverter;

    @Inject
    private RESTInformatieobjecttypeConverter informatieobjecttypeConverter;

    @Inject
    private RESTHistorieRegelConverter historieRegelConverter;

    @Inject
    private RESTGerelateerdeZaakConverter gerelateerdeZaakConverter;

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

    @Inject
    private EnkelvoudigInformatieObjectOndertekenService enkelvoudigInformatieObjectOndertekenService;

    @Inject
    private EnkelvoudigInformatieObjectDownloadService enkelvoudigInformatieObjectDownloadService;

    @GET
    @Path("informatieobject/{uuid}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (zaakUUID != null) {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(enkelvoudigInformatieObject, zrcClientService.readZaak(zaakUUID));
        } else {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(enkelvoudigInformatieObject);
        }
        return restEnkelvoudigInformatieobject;
    }

    @GET
    @Path("informatieobject/versie/{uuid}/{versie}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid, @PathParam("versie") final int versie) {
        final EnkelvoudigInformatieobject huidigeVersie = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (versie < huidigeVersie.getVersie()) {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(drcClientService.readEnkelvoudigInformatieobjectVersie(uuid, versie));
        } else {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(huidigeVersie);
        }
        return restEnkelvoudigInformatieobject;
    }

    @PUT
    @Path("informatieobjectenList")
    public List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjecten(final RESTInformatieObjectZoekParameters zoekParameters) {
        List<RESTEnkelvoudigInformatieobject> result;
        if (zoekParameters.zaakUUID != null) {
            result = listEnkelvoudigInformatieobjectenVoorZaak(zrcClientService.createUrlZaak(zoekParameters.zaakUUID));
        } else if (zoekParameters.zaakURI != null) {
            result = listEnkelvoudigInformatieobjectenVoorZaak(zoekParameters.zaakURI);
        } else if (zoekParameters.UUIDs != null) {
            return informatieobjectConverter.convertToREST(zoekParameters.UUIDs, zoekParameters.zaakOphalenVoorPolicyCheck);
        } else {
            throw new IllegalStateException("Zoekparameters hebben geen waarde");
        }
        if (zoekParameters.toonGekoppeldeZaakDocumenten) {
            final Zaak zaak = zoekParameters.zaakUUID != null ? zrcClientService.readZaak(
                    zoekParameters.zaakUUID) : zrcClientService.readZaak(zoekParameters.zaakURI);
            final List<RESTEnkelvoudigInformatieobject> list = new ArrayList<>(result);
            list.addAll(listGekoppeldeZaakInformatieObjectenVoorZaak(zaak));
            result = list;
        }
        if (zoekParameters.ophalenVoorBesluitType != null) {
            final Besluittype besluittype = ztcClientService.readBesluittype(zoekParameters.ophalenVoorBesluitType);
            final List<UUID> compareList = besluittype.getInformatieobjecttypen().stream().map(UriUtil::uuidFromURI).toList();
            result = result.stream()
                    .filter(enkelvoudigInformatieObject -> compareList.contains(enkelvoudigInformatieObject.informatieobjectTypeUUID))
                    .collect(Collectors.toList());
        }
        return result;
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
                informatieobjectConverter.convertTaakObject(restEnkelvoudigInformatieobject, file) :
                informatieobjectConverter.convertZaakObject(restEnkelvoudigInformatieobject, file);

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
        return informatieobjectConverter.convertToREST(zaakInformatieobject);
    }

    @POST
    @Path("informatieobject/verplaats")
    public void verplaatsEnkelvoudigInformatieobject(final RESTDocumentVerplaatsGegevens documentVerplaatsGegevens) {
        final UUID enkelvoudigInformatieobjectUUID = documentVerplaatsGegevens.documentUUID;
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        final Zaak nieuweZaak = zrcClientService.readZaakByID(documentVerplaatsGegevens.nieuweZaakID);
        assertActie(policyService.readDocumentActies(informatieobject).getKoppelen());
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
        return informatieobjecttypeConverter.convert(zaaktype.getInformatieobjecttypen());
    }

    @GET
    @Path("informatieobjecttypes/zaak/{zaakUuid}")
    public List<RESTInformatieobjecttype> listInformatieobjecttypesForZaak(@PathParam("zaakUuid") final UUID zaakID) {
        final Zaak zaak = zrcClientService.readZaak(zaakID);
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final List<Informatieobjecttype> informatieObjectTypes = zaaktype.getInformatieobjecttypen().stream()
                .map(uri -> ztcClientService.readInformatieobjecttype(uri))
                .filter(Informatieobjecttype::isNuGeldig)
                .collect(Collectors.toList());
        return informatieobjecttypeConverter.convert(informatieObjectTypes);
    }

    @POST
    @Path("informatieobject/upload/{uuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("uuid") final UUID uuid, @MultipartForm final RESTFileUpload data) {
        httpSession.get().setAttribute("FILE_" + uuid, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("informatieobject/{uuid}/zaakinformatieobjecten")
    public List<RESTZaakInformatieobject> listZaakInformatieobjecten(@PathParam("uuid") final UUID uuid) {
        assertActie(policyService.readDocumentActies(uuid).getLezen());
        return zrcClientService.listZaakinformatieobjecten(drcClientService.readEnkelvoudigInformatieobject(uuid)).stream()
                .map(zaakInformatieobjectConverter::convert).toList();
    }

    @GET
    @Path("informatieobject/{uuid}/edit")
    public Response editEnkelvoudigInformatieobjectInhoud(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID,
            @Context final UriInfo uriInfo) {
        assertActie(policyService.readDocumentActies(uuid, zaakUUID).getWijzigen());
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
        assertActie(policyService.readDocumentActies(uuid, documentVerwijderenGegevens.zaakUuid).getVerwijderen());
        zgwApiService.removeEnkelvoudigInformatieObjectFromZaak(documentVerwijderenGegevens.zaakUuid, uuid, documentVerwijderenGegevens.reden);
        return Response.noContent().build();
    }

    @GET
    @Path("/informatieobject/{uuid}/{versie}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response readFile(@PathParam("uuid") final UUID uuid, @PathParam("versie") final Integer versie) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        assertActie(policyService.readDocumentActies(enkelvoudigInformatieObject).getDownloaden());
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

    @POST
    @Path("/download/zip")
    public Response readFilesAsZip(final List<String> uuids) {
        final List<UUID> uuidList = uuids.stream().map(UUID::fromString).toList();
        uuidList.forEach(uuid -> assertActie(policyService.readDocumentActies(uuid).getDownloaden()));
        final StreamingOutput streamingOutput = enkelvoudigInformatieObjectDownloadService.getZipStreamOutput(uuidList);
        return Response.ok(streamingOutput)
                .header("Content-Type","application/zip")
                .build();
    }

    @GET
    @Path("informatieobject/{uuid}/huidigeversie")
    public RESTEnkelvoudigInformatieObjectVersieGegevens readHuidigeVersieInformatieObject(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(uuid);
        assertActie(policyService.readDocumentActies(enkelvoudigInformatieObject).getLezen());
        return informatieobjectConverter.convertToRESTEnkelvoudigInformatieObjectVersieGegevens(enkelvoudigInformatieObject);
    }

    @POST
    @Path("/informatieobject/update")
    public RESTEnkelvoudigInformatieobject updateEnkelvoudigInformatieobject(
            final RESTEnkelvoudigInformatieObjectVersieGegevens enkelvoudigInformatieObjectVersieGegevens) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                enkelvoudigInformatieObjectVersieGegevens.uuid);
        assertActie(policyService.readDocumentActies(enkelvoudigInformatieobject, enkelvoudigInformatieObjectVersieGegevens.zaakUuid)
                            .getToevoegenNieuweVersie());
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
                    informatieobjectConverter.convert(enkelvoudigInformatieObjectVersieGegevens, lock.getLock(), file));
            return informatieobjectConverter.convertToREST(returnObject);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(enkelvoudigInformatieobject.getUUID());
            }
        }
    }

    @POST
    @Path("/informatieobject/{uuid}/lock")
    public Response lockDocument(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        assertActie(policyService.readDocumentActies(uuid, zaakUUID).getVergrendelen());
        enkelvoudigInformatieObjectLockService.createLock(uuid, loggedInUserInstance.get().getId());
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/unlock")
    public Response unlockDocument(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        assertActie(policyService.readDocumentActies(uuid, zaakUUID).getOntgrendelen());
        enkelvoudigInformatieObjectLockService.deleteLock(uuid);
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @GET
    @Path("informatieobject/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID enkelvoudigInformatieobjectUUID) {
        assertActie(policyService.readDocumentActies(enkelvoudigInformatieobjectUUID).getLezen());
        List<AuditTrailRegel> auditTrail = drcClientService.listAuditTrail(enkelvoudigInformatieobjectUUID);
        return historieRegelConverter.convert(auditTrail);
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

    @GET
    @Path("informatieobject/{informatieObjectUuid}/zaakidentificaties")
    public List<String> listZaakIdentificatiesForInformatieobject(@PathParam("informatieObjectUuid") UUID informatieobjectUuid) {
        assertActie(policyService.readDocumentActies(informatieobjectUuid).getLezen());
        List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(
                drcClientService.createEnkelvoudigInformatieObjectURL(informatieobjectUuid));
        return zaakInformatieobjects.stream()
                .map(zaakInformatieobject -> zrcClientService.readZaak(zaakInformatieobject.getZaak()).getIdentificatie()).toList();
    }

    @POST
    @Path("/informatieobject/{uuid}/onderteken")
    public Response ondertekenInformatieObject(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        assertActie(policyService.readDocumentActies(uuid, zaakUUID).getOndertekenen());
        enkelvoudigInformatieObjectOndertekenService.ondertekenEnkelvoudigInformatieObject(uuid);
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    private List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(final URI zaakURL) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setZaak(zaakURL);
        final List<ZaakInformatieobject> zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(parameters);
        return informatieobjectConverter.convertToREST(zaakInformatieobjecten);
    }

    private List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(final URI zaakURI,
            final RelatieType relatieType) {
        final Zaak zaak = zrcClientService.readZaak(zaakURI);
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setZaak(zaak.getUrl());
        final List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(parameters);
        return zaakInformatieobjects.stream()
                .map(zaakInformatieobject -> informatieobjectConverter.convertToREST(zaakInformatieobject, relatieType, zaak))
                .toList();
    }

    private List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> listGekoppeldeZaakInformatieObjectenVoorZaak(final Zaak zaak) {
        final List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> enkelvoudigInformatieobjectList = new ArrayList<>();
        zaak.getDeelzaken().forEach(deelzaak -> {
            enkelvoudigInformatieobjectList.addAll(listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(deelzaak, RelatieType.DEELZAAK));
        });
        if (zaak.getHoofdzaak() != null) {
            enkelvoudigInformatieobjectList.addAll(listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(zaak.getHoofdzaak(), RelatieType.HOOFDZAAK));
        }
        zaak.getRelevanteAndereZaken().forEach(relevanteAndereZaak -> {
            enkelvoudigInformatieobjectList.addAll(
                    listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(relevanteAndereZaak.getUrl(), gerelateerdeZaakConverter.convertToRelatieType(
                            relevanteAndereZaak.getAardRelatie())));
        });
        return enkelvoudigInformatieobjectList;
    }
}
