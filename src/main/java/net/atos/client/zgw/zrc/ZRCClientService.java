/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc;

import static net.atos.zac.websocket.event.ScreenEventType.ZAAK_INFORMATIEOBJECTEN;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.util.ClientFactory;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.JsonbConfiguration;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakEigenschap;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakInformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.zac.event.EventingService;
import net.atos.zac.util.UriUtil;

/**
 * Careful!
 * <p>
 * Never call methods with caching annotations from within the service (or it will not work).
 * Do not introduce caches with keys other than URI and UUID.
 * Use Optional for caches that need to hold nulls (Infinispan does not cache nulls).
 */
@ApplicationScoped
public class ZRCClientService {

    @Inject
    @ConfigProperty(name = "ZGW_API_URL_EXTERN")
    private String zgwApiUrlExtern;

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    @Inject
    private ZGWClientHeadersFactory zgwClientHeadersFactory;

    @Inject
    private EventingService eventingService;

    /**
     * Create {@link Rol}.
     *
     * @param rol {@link Rol}/
     * @return Created {@link Rol}.
     */
    public Rol<?> createRol(final Rol<?> rol) {
        return createRol(rol, null);
    }

    /**
     * Create {@link Rol}.
     *
     * @param rol         {@link Rol}/
     * @param toelichting de toelichting
     * @return Created {@link Rol}.
     */
    public Rol<?> createRol(final Rol<?> rol, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        return zrcClient.rolCreate(rol);
    }

    /**
     * Delete {@link Rol}.
     *
     * @param rolUUID     uuid van de {@link Rol}/
     * @param toelichting de toelichting
     */
    public void deleteRol(final UUID rolUUID, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        zrcClient.rolDelete(rolUUID);
    }

    /**
     * Create {@link Zaakobject}.
     *
     * @param zaakobject {@link Zaakobject}.
     * @return Created {@link Zaakobject}.
     */
    public Zaakobject createZaakobject(final Zaakobject zaakobject) {
        return zrcClient.zaakobjectCreate(zaakobject);
    }

    /**
     * Create {@link ZaakInformatieobject}
     *
     * @param zaakInformatieobject describes relation between ZAAK en INFORMATIEOBJECT
     * @return ZaakInformatieobject
     */
    public ZaakInformatieobject createZaakInformatieobject(final ZaakInformatieobject zaakInformatieobject, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        return zrcClient.zaakinformatieobjectCreate(zaakInformatieobject);
    }

    /**
     * delete {@link ZaakInformatieobject}
     *
     * @param zaakInformatieobjectUuid uuid
     */
    public void deleteZaakInformatieobject(final UUID zaakInformatieobjectUuid, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        zrcClient.zaakinformatieobjectDelete(zaakInformatieobjectUuid);
    }

    /**
     * Read {@link Zaak} via its UUID.
     * Throws a RuntimeException if the {@link Zaak} can not be read.
     *
     * @param zaakUUID UUID of {@link Zaak}.
     * @return {@link Zaak}. Never NULL!
     */
    public Zaak readZaak(final UUID zaakUUID) {
        return zrcClient.zaakRead(zaakUUID);
    }

    /**
     * Read {@link Zaak} via its URI.
     * Throws a RuntimeException if the {@link Zaak} can not be read.
     *
     * @param zaakURI URI of {@link Zaak}.
     * @return {@link Zaak}. Never NULL!
     */
    public Zaak readZaak(final URI zaakURI) {
        return createInvocationBuilder(zaakURI).get(Zaak.class);
    }


    /**
     * Read {@link ZaakInformatieobject} via its UUID.
     * Throws a RuntimeException if the {@link ZaakInformatieobject} can not be read.
     *
     * @param zaakinformatieobjectUUID UUID of {@link ZaakInformatieobject}.
     * @return {@link ZaakInformatieobject}. Never NULL!
     */
    public ZaakInformatieobject readZaakinformatieobject(final UUID zaakinformatieobjectUUID) {
        return zrcClient.zaakinformatieobjectRead(zaakinformatieobjectUUID);
    }

    /**
     * Update all instances of {@link Rol} for {@link Zaak}.
     * Replaces all current instances of {@link Rol} with the suplied instances.
     *
     * @param zaakUUID de bij te werken zaak
     * @param rollen   de gewenste rollen
     */
    private void updateRollen(final UUID zaakUUID, final Collection<Rol<?>> rollen, final String toelichting) {
        final Collection<Rol<?>> current = listRollen(zaakUUID);
        deleteDeletedRollen(current, rollen, toelichting);
        deleteUpdatedRollen(current, rollen, toelichting);
        createUpdatedRollen(current, rollen, toelichting);
        createCreatedRollen(current, rollen, toelichting);
    }

    public void updateRol(final UUID zaakUUID, final Rol<?> rol, final String toelichting) {
        final List<Rol<?>> rollen = listRollen(zaakUUID);
        rollen.add(rol);

        updateRollen(zaakUUID, rollen, toelichting);
    }

    public void deleteRol(final UUID zaakUUID, final BetrokkeneType betrokkeneType, final String toelichting) {
        final List<Rol<?>> rollen = listRollen(zaakUUID);

        final Optional<Rol<?>> rolMedewerker =
                rollen.stream().filter(rol -> rol.getBetrokkeneType() == betrokkeneType).findFirst();

        rolMedewerker.ifPresent(betrokkene -> rollen.removeIf(rol -> rol.equalBetrokkeneRol(betrokkene)));

        updateRollen(zaakUUID, rollen, toelichting);
    }

    /**
     * Read {@link Rol} via its URI.
     * Throws a RuntimeException if the {@link Rol} can not be read.
     *
     * @param rolURI URI of {@link Rol}.
     * @return {@link Rol}. Never NULL!
     */
    public Rol<?> readRol(final URI rolURI) {
        return createInvocationBuilder(rolURI).get(Rol.class);
    }

    /**
     * Read {@link Rol} via its UUID.
     * Throws a RuntimeException if the {@link Rol} can not be read.
     *
     * @param rolUUID UUID of {@link Rol}.
     * @return {@link Rol}. Never NULL!
     */
    public Rol<?> readRol(final UUID rolUUID) {
        return zrcClient.rolRead(rolUUID);
    }

    /**
     * Read {@link Resultaat} via its URI.
     * Throws a RuntimeException if the {@link Resultaat} can not be read.
     *
     * @param resultaatURI URI of {@link Resultaat}.
     * @return {@link Resultaat}. Never 'null'!
     */
    public Resultaat readResultaat(final URI resultaatURI) {
        return createInvocationBuilder(resultaatURI).get(Resultaat.class);
    }

    /**
     * Read {@link ZaakEigenschap} via its URI.
     * Throws a RuntimeException if the {@link ZaakEigenschap} can not be read.
     *
     * @param zaakeigenschapURI URI of {@link ZaakEigenschap}.
     * @return {@link ZaakEigenschap}. Never 'null'!
     */
    public ZaakEigenschap readZaakeigenschap(final URI zaakeigenschapURI) {
        return createInvocationBuilder(zaakeigenschapURI).get(ZaakEigenschap.class);
    }

    /**
     * Read {@link Status} via its URI.
     * Throws a RuntimeException if the {@link Status} can not be read.
     *
     * @param statusURI URI of {@link Status}.
     * @return {@link Status}. Never 'null'!
     */
    public Status readStatus(final URI statusURI) {
        return createInvocationBuilder(statusURI).get(Status.class);
    }

    /**
     * List instances of {@link Zaakobject} filtered by {@link ZaakobjectListParameters}.
     *
     * @param zaakobjectListParameters {@link ZaakobjectListParameters}.
     * @return List of {@link Zaakobject} instances.
     */
    public Results<Zaakobject> listZaakobjecten(final ZaakobjectListParameters zaakobjectListParameters) {
        return zrcClient.zaakobjectList(zaakobjectListParameters);
    }

    /**
     * Partially update {@link Zaak}.
     *
     * @param zaakUUID UUID of {@link Zaak}.
     * @param zaak     {@link Zaak} with parts that need to be updated.
     * @return Updated {@link Zaak}
     */
    public Zaak updateZaak(final UUID zaakUUID, final Zaak zaak, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        return updateZaak(zaakUUID, zaak);
    }

    /**
     * Partially update {@link Zaak}.
     *
     * @param zaakUUID UUID of {@link Zaak}.
     * @param zaak     {@link Zaak} with parts that need to be updated.
     * @return Updated {@link Zaak}
     */
    public Zaak updateZaak(final UUID zaakUUID, final Zaak zaak) {
        return zrcClient.zaakPartialUpdate(zaakUUID, zaak);
    }

    /**
     * List instances of {@link Zaak} filtered by {@link ZaakListParameters}.
     *
     * @param filter {@link ZaakListParameters}.
     * @return List of {@link Zaak} instances.
     */
    public Results<Zaak> listZaken(final ZaakListParameters filter) {
        return zrcClient.zaakList(filter);
    }

    /**
     * List instances of {@link ZaakInformatieobject} filtered by {@link ZaakInformatieobjectListParameters}.
     *
     * @param filter {@link ZaakInformatieobjectListParameters}.
     * @return List of {@link ZaakInformatieobject} instances.
     */
    public List<ZaakInformatieobject> listZaakinformatieobjecten(final ZaakInformatieobjectListParameters filter) {
        return zrcClient.zaakinformatieobjectList(filter);
    }

    public List<ZaakInformatieobject> listZaakinformatieobjecten(final EnkelvoudigInformatieobject informatieobject) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(informatieobject.getUrl());
        return listZaakinformatieobjecten(parameters);
    }

    /**
     * List instances of {@link Rol} filtered by {@link RolListParameters}.
     *
     * @param filter {@link RolListParameters}.
     * @return List of {@link Rol} instances.
     */
    public Results<Rol<?>> listRollen(final RolListParameters filter) {
        return zrcClient.rolList(filter);
    }

    /**
     * List all instances of {@link Rol} for a specific {@link Zaak}.
     *
     * @param zaakUUID UUID of {@link Zaak}
     * @return List of {@link Rol}
     */
    public List<Rol<?>> listRollen(final UUID zaakUUID) {
        final Zaak zaak = zrcClient.zaakRead(zaakUUID);
        return zrcClient.rolList(new RolListParameters(zaak.getUrl())).getResults();
    }

    public Zaak readZaakByID(final String identificatie) {
        final ZaakListParameters zaakListParameters = new ZaakListParameters();
        zaakListParameters.setIdentificatie(identificatie);
        final Results<Zaak> zaakResults = listZaken(zaakListParameters);
        if (zaakResults.getCount() == 0) {
            throw new NotFoundException(String.format("Zaak met identificatie '%s' niet gevonden", identificatie));
        } else if (zaakResults.getCount() > 1) {
            throw new IllegalStateException(String.format("Meerdere zaken met identificatie '%s' gevonden", identificatie));
        }
        return zaakResults.getResults().get(0);
    }

    public void verplaatsInformatieobject(final EnkelvoudigInformatieobject informatieobject, final Zaak oudeZaak, final Zaak nieuweZaak) {
        final ZaakInformatieobjectListParameters parameters = new ZaakInformatieobjectListParameters();
        parameters.setInformatieobject(informatieobject.getUrl());
        parameters.setZaak(oudeZaak.getUrl());
        List<ZaakInformatieobject> zaakInformatieobjecten = listZaakinformatieobjecten(parameters);
        if (zaakInformatieobjecten.isEmpty()) {
            throw new NotFoundException(String.format("Geen ZaakInformatieobject gevonden voor Zaak: '%s' en InformatieObject: '%s'",
                                                      oudeZaak.getIdentificatie(),
                                                      UriUtil.uuidFromURI(informatieobject.getInhoud())));
        }

        final ZaakInformatieobject oudeZaakInformatieobject = zaakInformatieobjecten.get(0);
        final ZaakInformatieobject nieuweZaakInformatieObject = new ZaakInformatieobject();
        nieuweZaakInformatieObject.setZaak(nieuweZaak.getUrl());
        nieuweZaakInformatieObject.setInformatieobject(informatieobject.getUrl());
        nieuweZaakInformatieObject.setTitel(oudeZaakInformatieobject.getTitel());
        nieuweZaakInformatieObject.setBeschrijving(oudeZaakInformatieobject.getBeschrijving());


        final String toelichting = "Verplaatst: %s -> %s".formatted(oudeZaak.getIdentificatie(), nieuweZaak.getIdentificatie());
        createZaakInformatieobject(nieuweZaakInformatieObject, toelichting);
        deleteZaakInformatieobject(oudeZaakInformatieobject.getUuid(), toelichting);
        eventingService.send(ZAAK_INFORMATIEOBJECTEN.updated(oudeZaak));
        eventingService.send(ZAAK_INFORMATIEOBJECTEN.updated(nieuweZaak));
    }

    public void koppelInformatieobject(final EnkelvoudigInformatieobject informatieobject, final Zaak nieuweZaak, final String toelichting) {
        List<ZaakInformatieobject> zaakInformatieobjecten = listZaakinformatieobjecten(informatieobject);
        if (!zaakInformatieobjecten.isEmpty()) {
            final UUID zaakUuid = UriUtil.uuidFromURI(zaakInformatieobjecten.get(0).getZaak());
            throw new IllegalStateException(String.format("Informatieobject is reeds gekoppeld aan zaak '%s'", zaakUuid));
        }
        final ZaakInformatieobject nieuweZaakInformatieObject = new ZaakInformatieobject();
        nieuweZaakInformatieObject.setZaak(nieuweZaak.getUrl());
        nieuweZaakInformatieObject.setInformatieobject(informatieobject.getUrl());
        nieuweZaakInformatieObject.setTitel(informatieobject.getTitel());
        nieuweZaakInformatieObject.setBeschrijving(informatieobject.getBeschrijving());
        createZaakInformatieobject(nieuweZaakInformatieObject, toelichting);
        eventingService.send(ZAAK_INFORMATIEOBJECTEN.updated(nieuweZaak));
    }

    /**
     * List all instances of {@link AuditTrailRegel} for a specific {@link Zaak}.
     *
     * @param zaakUUID UUID of {@link Zaak}.
     * @return List of {@link AuditTrailRegel} instances.
     */
    public List<AuditTrailRegel> listAuditTrail(final UUID zaakUUID) {
        return zrcClient.listAuditTrail(zaakUUID);
    }

    public Resultaat createResultaat(final Resultaat resultaat) {
        zgwClientHeadersFactory.setAuditToelichting(resultaat.getToelichting());
        return zrcClient.resultaatCreate(resultaat);
    }

    public void deleteResultaat(final UUID resultaatUUID) {
        zrcClient.resultaatDelete(resultaatUUID);
    }

    public Zaak createZaak(final Zaak zaak) {
        zgwClientHeadersFactory.setAuditToelichting(zaak.getToelichting());
        return zrcClient.zaakCreate(zaak);
    }

    public Status createStatus(final Status status) {
        zgwClientHeadersFactory.setAuditToelichting(status.getStatustoelichting());
        return zrcClient.statusCreate(status);
    }

    public URI createUrlExternToZaak(final UUID zaakUUID) {
        return UriBuilder.fromUri(zgwApiUrlExtern).path(ZRCClient.class).path(ZRCClient.class, "zaakRead").build(zaakUUID);
    }

    private void deleteDeletedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen, final String toelichting) {
        current.stream()
                .filter(oud -> rollen.stream()
                        .noneMatch(oud::equalBetrokkeneRol))
                .forEach(rol -> deleteRol(rol.getUuid(), toelichting));
    }

    private void deleteUpdatedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen, final String toelichting) {
        current.stream()
                .filter(oud -> rollen.stream()
                        .filter(oud::equalBetrokkeneRol)
                        .anyMatch(nieuw -> !nieuw.equals(oud)))
                .forEach(rol -> deleteRol(rol.getUuid(), toelichting));
    }

    private void createUpdatedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen, final String toelichting) {
        rollen.stream()
                .filter(nieuw -> current.stream()
                        .filter(nieuw::equalBetrokkeneRol)
                        .anyMatch(oud -> !oud.equals(nieuw)))
                .forEach(rol -> createRol(rol, toelichting));
    }

    private void createCreatedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen, final String toelichting) {
        rollen.stream()
                .filter(nieuw -> currentRollen.stream()
                        .noneMatch(nieuw::equalBetrokkeneRol))
                .forEach(rol -> createRol(rol, toelichting));
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .register(FoutExceptionMapper.class)
                .register(ValidatieFoutExceptionMapper.class)
                .register(RuntimeExceptionMapper.class)
                .register(JsonbConfiguration.class)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, zgwClientHeadersFactory.generateJWTToken())
                .header(ZRCClient.ACCEPT_CRS, ZRCClient.ACCEPT_CRS_VALUE)
                .header(ZRCClient.CONTENT_CRS, ZRCClient.ACCEPT_CRS_VALUE);
    }
}
