/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import static net.atos.zac.configuratie.ConfiguratieService.OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN;
import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.websocket.event.ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import net.atos.client.officeconverter.OfficeConverterClientService;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
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
import net.atos.zac.app.informatieobjecten.model.RESTInformatieobjectZoekParameters;
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
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.exception.FoutmeldingException;
import net.atos.zac.util.UriUtil;
import net.atos.zac.webdav.WebdavHelper;
import net.atos.zac.zoeken.IndexeerService;

@Singleton
@Path("informatieobjecten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InformatieObjectenRESTService {

    private static final String MEDIA_TYPE_PDF = "application/pdf";

    private static final String TOELICHTING_PDF = "Geconverteerd naar PDF";

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private TakenService takenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private OntkoppeldeDocumentenService ontkoppeldeDocumentenService;

    @Inject
    private InboxDocumentenService inboxDocumentenService;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private EventingService eventingService;

    @Inject
    private IndexeerService indexeerService;

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

    @Inject
    private OfficeConverterClientService officeConverterClientService;

    @GET
    @Path("informatieobject/{uuid}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid,
            @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (zaakUUID != null) {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(enkelvoudigInformatieObject,
                                                                                      zrcClientService.readZaak(
                                                                                              zaakUUID));
        } else {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(enkelvoudigInformatieObject);
        }
        return restEnkelvoudigInformatieobject;
    }

    @GET
    @Path("informatieobject/versie/{uuid}/{versie}")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobject(@PathParam("uuid") final UUID uuid,
            @PathParam("versie") final int versie) {
        final EnkelvoudigInformatieobject huidigeVersie = drcClientService.readEnkelvoudigInformatieobject(uuid);
        final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject;
        if (versie < huidigeVersie.getVersie()) {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(
                    drcClientService.readEnkelvoudigInformatieobjectVersie(uuid, versie));
        } else {
            restEnkelvoudigInformatieobject = informatieobjectConverter.convertToREST(huidigeVersie);
        }
        return restEnkelvoudigInformatieobject;
    }

    @PUT
    @Path("informatieobjectenList")
    public List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjecten(
            final RESTInformatieobjectZoekParameters zoekParameters) {
        final Zaak zaak = zoekParameters.zaakUUID != null ? zrcClientService.readZaak(zoekParameters.zaakUUID) : null;
        if (zoekParameters.informatieobjectUUIDs != null) {
            return informatieobjectConverter.convertUUIDsToREST(zoekParameters.informatieobjectUUIDs, zaak);
        } else if (zaak != null) {
            assertPolicy(policyService.readZaakRechten(zaak).getLezen());
            List<RESTEnkelvoudigInformatieobject> enkelvoudigInformatieobjectenVoorZaak = listEnkelvoudigInformatieobjectenVoorZaak(
                    zaak);
            if (zoekParameters.gekoppeldeZaakDocumenten) {
                enkelvoudigInformatieobjectenVoorZaak = new ArrayList<>(enkelvoudigInformatieobjectenVoorZaak);
                enkelvoudigInformatieobjectenVoorZaak.addAll(listGekoppeldeZaakInformatieObjectenVoorZaak(zaak));
            }
            if (zoekParameters.besluittypeUUID != null) {
                final Besluittype besluittype = ztcClientService.readBesluittype(zoekParameters.besluittypeUUID);
                final List<UUID> compareList = besluittype.getInformatieobjecttypen().stream().map(UriUtil::uuidFromURI)
                        .toList();
                return enkelvoudigInformatieobjectenVoorZaak.stream()
                        .filter(enkelvoudigInformatieObject -> compareList.contains(
                                enkelvoudigInformatieObject.informatieobjectTypeUUID))
                        .toList();
            } else {
                return enkelvoudigInformatieobjectenVoorZaak;
            }
        } else {
            throw new IllegalStateException("Zoekparameters hebben geen waarde");
        }
    }

    @POST
    @Path("informatieobject/{zaakUuid}/{documentReferentieId}")
    public RESTEnkelvoudigInformatieobject createEnkelvoudigInformatieobject(
            @PathParam("zaakUuid") final UUID zaakUuid,
            @PathParam("documentReferentieId") final String documentReferentieId,
            @QueryParam("taakObject") final boolean taakObject,
            final RESTEnkelvoudigInformatieobject restEnkelvoudigInformatieobject) {
        final Zaak zaak = zrcClientService.readZaak(zaakUuid);
        assertPolicy(policyService.readZaakRechten(zaak).getWijzigen());
        final RESTFileUpload file = (RESTFileUpload) httpSession.get().getAttribute("FILE_" + documentReferentieId);
        final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectWithInhoud = taakObject ?
                informatieobjectConverter.convertTaakObject(restEnkelvoudigInformatieobject, file) :
                informatieobjectConverter.convertZaakObject(restEnkelvoudigInformatieobject, file);

        final ZaakInformatieobject zaakInformatieobject =
                zgwApiService.createZaakInformatieobjectForZaak(zaak, enkelvoudigInformatieobjectWithInhoud,
                                                                enkelvoudigInformatieobjectWithInhoud.getTitel(),
                                                                enkelvoudigInformatieobjectWithInhoud.getBeschrijving(),
                                                                OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN);
        if (taakObject) {
            final Task task = takenService.readOpenTask(documentReferentieId);
            final List<UUID> taakdocumenten = taakVariabelenService.readTaakdocumenten(task);
            taakdocumenten.add(UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject()));
            taakVariabelenService.setTaakdocumenten(task, taakdocumenten);
        }
        return informatieobjectConverter.convertToREST(zaakInformatieobject);
    }

    @POST
    @Path("informatieobject/verplaats")
    public void verplaatsEnkelvoudigInformatieobject(final RESTDocumentVerplaatsGegevens documentVerplaatsGegevens) {
        final UUID enkelvoudigInformatieobjectUUID = documentVerplaatsGegevens.documentUUID;
        final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(
                enkelvoudigInformatieobjectUUID);
        final Zaak nieuweZaak = zrcClientService.readZaakByID(documentVerplaatsGegevens.nieuweZaakID);
        assertPolicy(policyService.readDocumentRechten(informatieobject).getWijzigen() &&
                             policyService.readZaakRechten(nieuweZaak).getWijzigen());
        final String toelichting = "Verplaatst: %s -> %s".formatted(documentVerplaatsGegevens.bron,
                                                                    nieuweZaak.getIdentificatie());
        if (documentVerplaatsGegevens.vanuitOntkoppeldeDocumenten()) {
            final OntkoppeldDocument ontkoppeldDocument = ontkoppeldeDocumentenService.read(
                    enkelvoudigInformatieobjectUUID);
            zrcClientService.koppelInformatieobject(informatieobject, nieuweZaak, toelichting);
            ontkoppeldeDocumentenService.delete(ontkoppeldDocument.getId());
        } else if (documentVerplaatsGegevens.vanuitInboxDocumenten()) {
            final InboxDocument inboxDocument = inboxDocumentenService.read(enkelvoudigInformatieobjectUUID);
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

    /**
     * Zet een {@link RESTFileUpload} bestand in de HTTP sessie.
     *
     * @param documentReferentieId Zaak-UUID of taak-ID van gerelateerde zaak/taak.
     * @return Success response
     */
    @POST
    @Path("informatieobject/upload/{documentReferentieId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("documentReferentieId") final String documentReferentieId, @MultipartForm final RESTFileUpload data) {
        httpSession.get().setAttribute("FILE_" + documentReferentieId, data);
        return Response.ok("\"Success\"").build();
    }

    @GET
    @Path("zaakinformatieobject/{uuid}/informatieobject")
    public RESTEnkelvoudigInformatieobject readEnkelvoudigInformatieobjectByZaakInformatieobjectUUID(
            @PathParam("uuid") final UUID uuid) {
        return informatieobjectConverter.convertToREST(
                drcClientService.readEnkelvoudigInformatieobject(
                        zrcClientService.readZaakinformatieobject(uuid).getInformatieobject()));
    }

    @GET
    @Path("informatieobject/{uuid}/zaakinformatieobjecten")
    public List<RESTZaakInformatieobject> listZaakInformatieobjecten(@PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        assertPolicy(policyService.readDocumentRechten(enkelvoudigInformatieobject).getLezen());
        return zrcClientService.listZaakinformatieobjecten(enkelvoudigInformatieobject).stream()
                .map(zaakInformatieobjectConverter::convert)
                .toList();
    }

    @GET
    @Path("informatieobject/{uuid}/edit")
    public Response editEnkelvoudigInformatieobjectInhoud(@PathParam("uuid") final UUID uuid,
            @QueryParam("zaak") final UUID zaakUUID,
            @Context final UriInfo uriInfo) {
        assertPolicy(
                policyService.readDocumentRechten(drcClientService.readEnkelvoudigInformatieobject(uuid),
                                                  zrcClientService.readZaak(zaakUUID)).getWijzigen());
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
    public Response deleteEnkelvoudigInformatieObject(@PathParam("uuid") final UUID uuid,
            final RESTDocumentVerwijderenGegevens documentVerwijderenGegevens) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        final Zaak zaak = documentVerwijderenGegevens.zaakUuid != null ?
                zrcClientService.readZaak(documentVerwijderenGegevens.zaakUuid) : null;
        assertPolicy(policyService.readDocumentRechten(enkelvoudigInformatieobject, zaak).getVerwijderen());
        zgwApiService.removeEnkelvoudigInformatieObjectFromZaak(enkelvoudigInformatieobject,
                                                                documentVerwijderenGegevens.zaakUuid,
                                                                documentVerwijderenGegevens.reden);

        // In geval van een ontkoppeld document
        if (documentVerwijderenGegevens.zaakUuid == null) {
            ontkoppeldeDocumentenService.delete(uuid);
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/informatieobject/{uuid}/{versie}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response readFile(@PathParam("uuid") final UUID uuid, @PathParam("versie") final Integer versie) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        assertPolicy(policyService.readDocumentRechten(enkelvoudigInformatieObject).getLezen());
        try (final ByteArrayInputStream inhoud = (versie != null) ?
                drcClientService.downloadEnkelvoudigInformatieobjectVersie(uuid, versie) :
                drcClientService.downloadEnkelvoudigInformatieobject(uuid)) {
            return Response.ok(inhoud)
                    .header("Content-Disposition",
                            "attachment; filename=\"" + enkelvoudigInformatieObject.getBestandsnaam() + "\"")
                    .build();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/download/zip")
    public Response readFilesAsZip(final List<String> uuids) {
        final List<EnkelvoudigInformatieobject> informatieobjecten = uuids.stream()
                .map(UUID::fromString)
                .map(drcClientService::readEnkelvoudigInformatieobject)
                .toList();
        informatieobjecten.forEach(
                informatieobject -> assertPolicy(policyService.readDocumentRechten(informatieobject).getLezen()));
        final StreamingOutput streamingOutput = enkelvoudigInformatieObjectDownloadService.getZipStreamOutput(
                informatieobjecten);
        return Response.ok(streamingOutput).header("Content-Type", "application/zip").build();
    }

    @GET
    @Path("informatieobject/{uuid}/huidigeversie")
    public RESTEnkelvoudigInformatieObjectVersieGegevens readHuidigeVersieInformatieObject(
            @PathParam("uuid") final UUID uuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieObject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        assertPolicy(policyService.readDocumentRechten(enkelvoudigInformatieObject).getLezen());
        return informatieobjectConverter.convertToRESTEnkelvoudigInformatieObjectVersieGegevens(
                enkelvoudigInformatieObject);
    }

    @POST
    @Path("/informatieobject/update")
    public RESTEnkelvoudigInformatieobject updateEnkelvoudigInformatieobject(
            final RESTEnkelvoudigInformatieObjectVersieGegevens enkelvoudigInformatieObjectVersieGegevens) {
        final EnkelvoudigInformatieobject document = drcClientService.readEnkelvoudigInformatieobject(
                enkelvoudigInformatieObjectVersieGegevens.uuid);
        assertPolicy(policyService.readDocumentRechten(document, zrcClientService.readZaak(
                enkelvoudigInformatieObjectVersieGegevens.zaakUuid)).getWijzigen());
        final boolean tempLock = !document.getLocked();
        try {
            final RESTFileUpload file = (RESTFileUpload) httpSession.get()
                    .getAttribute("FILE_" + enkelvoudigInformatieObjectVersieGegevens.zaakUuid);
            EnkelvoudigInformatieobjectWithInhoudAndLock updatedDocument =
                    informatieobjectConverter.convert(enkelvoudigInformatieObjectVersieGegevens, getLock(document), file);
            updatedDocument = drcClientService.updateEnkelvoudigInformatieobject(
                    document.getUUID(), enkelvoudigInformatieObjectVersieGegevens.toelichting, updatedDocument);
            return informatieobjectConverter.convertToREST(updatedDocument);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(document.getUUID());
            }
        }
    }

    private String getLock(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        if (enkelvoudigInformatieobject.getLocked()) {
            return enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieobject.getUUID())
                    .map(EnkelvoudigInformatieObjectLock::getLock)
                    .orElseThrow(() -> new FoutmeldingException(
                            "Document kan niet worden aangepast omdat het is gelocked met onbekende lock."));
        } else {
            return enkelvoudigInformatieObjectLockService.createLock(enkelvoudigInformatieobject.getUUID(),
                                                                     loggedInUserInstance.get().getId()).getLock();
        }
    }

    @POST
    @Path("/informatieobject/{uuid}/lock")
    public Response lockDocument(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject =
                drcClientService.readEnkelvoudigInformatieobject(uuid);
        assertPolicy(isFalse(enkelvoudigInformatieobject.getLocked()) && policyService.readDocumentRechten(
                enkelvoudigInformatieobject, zrcClientService.readZaak(zaakUUID)).getVergrendelen());
        enkelvoudigInformatieObjectLockService.createLock(uuid, loggedInUserInstance.get().getId());
        // Hiervoor wordt door open zaak geen notificatie verstuurd. Dus zelf het ScreenEvent versturen!
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/unlock")
    public Response unlockDocument(@PathParam("uuid") final UUID uuid, @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        assertPolicy(enkelvoudigInformatieobject.getLocked() && policyService.readDocumentRechten(
                enkelvoudigInformatieobject, zrcClientService.readZaak(zaakUUID)).getOntgrendelen());
        enkelvoudigInformatieObjectLockService.deleteLock(uuid);
        // Hiervoor wordt door open zaak geen notificatie verstuurd. Dus zelf het ScreenEvent versturen!
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(uuid));
        return Response.ok().build();
    }

    @GET
    @Path("informatieobject/{uuid}/historie")
    public List<RESTHistorieRegel> listHistorie(@PathParam("uuid") final UUID uuid) {
        assertPolicy(
                policyService.readDocumentRechten(drcClientService.readEnkelvoudigInformatieobject(uuid)).getLezen());
        List<AuditTrailRegel> auditTrail = drcClientService.listAuditTrail(uuid);
        return historieRegelConverter.convert(auditTrail);
    }

    @POST
    @Path("/documentcreatie")
    public RESTDocumentCreatieResponse createDocument(final RESTDocumentCreatieGegevens restDocumentCreatieGegevens) {
        final Zaak zaak = zrcClientService.readZaak(restDocumentCreatieGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getWijzigen());
        final DocumentCreatieGegevens documentCreatieGegevens = new DocumentCreatieGegevens(zaak,
                                                                                            restDocumentCreatieGegevens.informatieobjecttypeUUID,
                                                                                            restDocumentCreatieGegevens.taskId);
        documentCreatieGegevens.setTitel(restDocumentCreatieGegevens.titel);
        final DocumentCreatieResponse documentCreatieResponse = documentCreatieService.creeerDocumentAttendedSD(
                documentCreatieGegevens);
        return new RESTDocumentCreatieResponse(documentCreatieResponse.getRedirectUrl(),
                                               documentCreatieResponse.getMessage());
    }

    @GET
    @Path("informatieobject/{informatieObjectUuid}/zaakidentificaties")
    public List<String> listZaakIdentificatiesForInformatieobject(
            @PathParam("informatieObjectUuid") UUID informatieobjectUuid) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                informatieobjectUuid);
        assertPolicy(policyService.readDocumentRechten(enkelvoudigInformatieobject).getLezen());
        List<ZaakInformatieobject> zaakInformatieobjects = zrcClientService.listZaakinformatieobjecten(
                enkelvoudigInformatieobject);
        return zaakInformatieobjects.stream()
                .map(zaakInformatieobject -> zrcClientService.readZaak(zaakInformatieobject.getZaak())
                        .getIdentificatie()).toList();
    }

    @POST
    @Path("/informatieobject/{uuid}/onderteken")
    public Response ondertekenInformatieObject(@PathParam("uuid") final UUID uuid,
            @QueryParam("zaak") final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(
                uuid);
        assertPolicy(enkelvoudigInformatieobject.getOndertekening() == null &&
                             policyService.readDocumentRechten(enkelvoudigInformatieobject,
                                                               zrcClientService.readZaak(zaakUUID)).getOndertekenen());
        enkelvoudigInformatieObjectOndertekenService.ondertekenEnkelvoudigInformatieObject(uuid);

        // Hiervoor wordt door open zaak geen notificatie verstuurd. Dus zelf het ScreenEvent versturen!
        eventingService.send(ENKELVOUDIG_INFORMATIEOBJECT.updated(enkelvoudigInformatieobject));

        return Response.ok().build();
    }

    @POST
    @Path("/informatieobject/{uuid}/convert")
    public Response convertInformatieObjectToPDF(@PathParam("uuid") final UUID enkelvoudigInformatieobjectUUID,
            @QueryParam("zaak") final UUID zaakUUID) throws IOException {
        final EnkelvoudigInformatieobject document =
                drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        assertPolicy(policyService.readDocumentRechten(document, zrcClientService.readZaak(zaakUUID)).getWijzigen());
        final boolean tempLock = !document.getLocked();
        try (final ByteArrayInputStream documentInputStream =
                     drcClientService.downloadEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
             final ByteArrayInputStream pdfInputStream =
                     officeConverterClientService.convertToPDF(documentInputStream, document.getBestandsnaam())) {
            final EnkelvoudigInformatieobjectWithInhoudAndLock pdf = new EnkelvoudigInformatieobjectWithInhoudAndLock();
            pdf.setLock(getLock(document));
            pdf.setInhoud(pdfInputStream.readAllBytes());
            pdf.setFormaat(MEDIA_TYPE_PDF);
            pdf.setBestandsnaam(StringUtils.substringBeforeLast(document.getBestandsnaam(), ".") + ".pdf");
            drcClientService.updateEnkelvoudigInformatieobject(document.getUUID(), TOELICHTING_PDF, pdf);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(document.getUUID());
            }
        }
        return Response.ok().build();
    }

    private List<RESTEnkelvoudigInformatieobject> listEnkelvoudigInformatieobjectenVoorZaak(final Zaak zaak) {
        return informatieobjectConverter.convertToREST(zrcClientService.listZaakinformatieobjecten(zaak));
    }

    private List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(
            final URI zaakURI, final RelatieType relatieType) {
        final Zaak zaak = zrcClientService.readZaak(zaakURI);
        return zrcClientService.listZaakinformatieobjecten(zaak).stream()
                .map(zaakInformatieobject -> informatieobjectConverter.convertToREST(zaakInformatieobject, relatieType,
                                                                                     zaak))
                .toList();
    }

    private List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> listGekoppeldeZaakInformatieObjectenVoorZaak(
            final Zaak zaak) {
        final List<RESTGekoppeldeZaakEnkelvoudigInformatieObject> enkelvoudigInformatieobjectList = new ArrayList<>();
        zaak.getDeelzaken().forEach(deelzaak -> {
            enkelvoudigInformatieobjectList.addAll(
                    listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(deelzaak, RelatieType.DEELZAAK));
        });
        if (zaak.getHoofdzaak() != null) {
            enkelvoudigInformatieobjectList.addAll(
                    listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(zaak.getHoofdzaak(),
                                                                            RelatieType.HOOFDZAAK));
        }
        zaak.getRelevanteAndereZaken().forEach(relevanteAndereZaak -> {
            enkelvoudigInformatieobjectList.addAll(
                    listGekoppeldeZaakEnkelvoudigInformatieobjectenVoorZaak(relevanteAndereZaak.getUrl(),
                                                                            gerelateerdeZaakConverter.convertToRelatieType(
                                                                                    relevanteAndereZaak.getAardRelatie())));
        });
        return enkelvoudigInformatieobjectList;
    }
}
