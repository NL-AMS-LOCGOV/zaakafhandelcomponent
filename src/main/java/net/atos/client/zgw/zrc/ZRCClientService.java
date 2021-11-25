/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc;

import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;
import static net.atos.client.zgw.shared.util.ZGWClientHeadersFactory.generateJWTToken;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.util.ClientFactory;
import net.atos.client.zgw.shared.cache.Caching;
import net.atos.client.zgw.shared.model.Archiefnominatie;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
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

/**
 * Careful!
 * <p>
 * Never call methods with caching annotations from within the service (or it will not work).
 * Do not introduce caches with keys other than URI and UUID.
 * Use Optional for caches that need to hold nulls (Infinispan does not cache nulls).
 */
@ApplicationScoped
public class ZRCClientService implements Caching {

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    /**
     * Create {@link Rol}.
     *
     * @param rol {@link Rol}/
     * @return Created {@link Rol}.
     */
    public Rol<?> createRol(final Rol<?> rol) {
        return zrcClient.rolCreate(rol);
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
     * Update all instances of {@link Rol} for {@link Zaak}.
     * Replaces all current instances of {@link Rol} with the suplied instances.
     *
     * @param zaakUrl de bij te werken zaak
     * @param rollen  de gewenste rollen
     */
    public void updateRollen(final URI zaakUrl, final Collection<Rol<?>> rollen) {
        final Collection<Rol<?>> current=listRollen(zaakUrl);
        deleteDeletedRollen(current, rollen);
        deleteUpdatedRollen(current, rollen);
        createUpdatedRollen(current, rollen);
        createCreatedRollen(current, rollen);
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
    @CacheResult(cacheName = ZRC_STATUS_MANAGED)
    public Status readStatus(final URI statusURI) {
        return createInvocationBuilder(statusURI).get(Status.class);
    }

    /**
     * List instances of {@link Zaakobject} filtered by {@link ZaakobjectListParameters}.
     *
     * @param filter {@link ZaakobjectListParameters}.
     * @return List of {@link Zaakobject} instances.
     */
    public Results<Zaakobject> listZaakobjecten(final ZaakobjectListParameters filter) {
        return zrcClient.zaakobjectList(filter);
    }

    /**
     * Partially update {@link Zaak}.
     *
     * @param zaakUUID UUID of {@link Zaak}.
     * @param zaak     {@link Zaak} with parts that need to be updated.
     * @return Updated {@link Zaak}
     */
    public Zaak updateZaakPartially(final UUID zaakUUID, final Zaak zaak) {
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
     * List instances of {@link Zaak} filtered which are open.
     *
     * @param filters {@link ZaakListParameters}.
     * @return List of {@link Zaak} instances.
     */
    // TODO ESUITEDEV-25904 Oplossing vinden voor paginering van open zaken
    public Results<Zaak> listOpenZaken(final ZaakListParameters filters) {
        final Results<Zaak> zakenResult = zrcClient.zaakList(filters);
        final List<Zaak> zaken = zakenResult.getResults().stream().filter(zaak -> zaak.getArchiefnominatie() == null)
                .collect(Collectors.toList());
        zakenResult.setResults(zaken);

        return zakenResult;
    }

    /**
     * List instances of {@link Zaak} filtered which are closed.
     *
     * @param filters {@link ZaakListParameters}.
     * @return List of {@link Zaak} instances.
     */
    public Results<Zaak> listClosedZaken(final ZaakListParameters filters) {
        final Set<Archiefnominatie> archiefnominaties = new HashSet<>();
        archiefnominaties.add(Archiefnominatie.BLIJVEND_BEWAREN);
        archiefnominaties.add(Archiefnominatie.VERNIETIGEN);

        filters.setArchiefnominatieIn(archiefnominaties);

        return zrcClient.zaakList(filters);
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
     * @param zaakURI URI of {@link Zaak}
     * @return List of {@link Rol}
     */
    public List<Rol<?>> listRollen(final URI zaakURI) {
        final RolListParameters rolListParameters = new RolListParameters();
        rolListParameters.setZaak(zaakURI);
        return zrcClient.rolList(rolListParameters).getResults();
    }

    private void deleteDeletedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen) {
        current.stream()
                .filter(oud -> rollen.stream()
                        .noneMatch(oud::equalBetrokkeneRol))
                .forEach(oud -> zrcClient.rolDelete(oud.getUuid()));
    }

    private void deleteUpdatedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen) {
        current.stream()
                .filter(oud -> rollen.stream()
                        .filter(oud::equalBetrokkeneRol)
                        .anyMatch(nieuw -> !nieuw.equals(oud)))
                .forEach(oud -> zrcClient.rolDelete(oud.getUuid()));
    }

    private void createUpdatedRollen(final Collection<Rol<?>> current, final Collection<Rol<?>> rollen) {
        rollen.stream()
                .filter(nieuw -> current.stream()
                        .filter(nieuw::equalBetrokkeneRol)
                        .anyMatch(oud -> !oud.equals(nieuw)))
                .forEach(nieuw -> zrcClient.rolCreate(nieuw));
    }

    private void createCreatedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen) {
        rollen.stream()
                .filter(nieuw -> currentRollen.stream()
                        .noneMatch(nieuw::equalBetrokkeneRol))
                .forEach(nieuw -> zrcClient.rolCreate(nieuw));
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateJWTToken())
                .header(ZRCClient.ACCEPT_CRS, ZRCClient.ACCEPT_CRS_VALUE)
                .header(ZRCClient.CONTENT_CRS, ZRCClient.ACCEPT_CRS_VALUE);
    }

    @CacheRemoveAll(cacheName = ZRC_STATUS_MANAGED)
    public void clearZaakstatusManagedCache() {
        cleared(ZRC_STATUS_MANAGED);
    }

    @CacheRemove(cacheName = ZRC_STATUS_MANAGED)
    public void updateZaakstatusCache(final URI key) {
        removed(ZRC_STATUS_MANAGED, key);
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

}
