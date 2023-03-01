/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc;

import static java.lang.String.format;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Besluittype;
import net.atos.client.zgw.ztc.model.BesluittypeListParameters;
import net.atos.client.zgw.ztc.model.Catalogus;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.ResultaattypeListParameters;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.RoltypeListParameters;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.StatustypeListParameters;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.client.zgw.ztc.model.ZaaktypeInformatieobjecttype;
import net.atos.client.zgw.ztc.model.ZaaktypeInformatieobjecttypeListParameters;
import net.atos.client.zgw.ztc.model.ZaaktypeListParameters;

/**
 * Careful!
 * <p>
 * Never call methods with caching annotations from within the service (or it will not work).
 * Do not introduce caches with keys other than URI and UUID.
 * Use Optional for caches that need to hold nulls (Infinispan does not cache nulls).
 */
@ApplicationScoped
public class ZTCClientService implements Caching {

    private static final List<String> CACHES = List.of(
            ZTC_BESLUITTYPE,
            ZTC_CACHE_TIME,
            ZTC_RESULTAATTYPE,
            ZTC_STATUSTYPE,
            ZTC_INFORMATIEOBJECTTYPE,
            ZTC_ZAAKTYPE_INFORMATIEOBJECTTYPE,
            ZTC_ZAAKTYPE);

    @Inject
    @RestClient
    private ZTCClient ztcClient;

    @Inject
    private ZGWClientHeadersFactory zgwClientHeadersFactory;

    public Results<Catalogus> listCatalogus(final CatalogusListParameters catalogusListParameters) {
        return ztcClient.catalogusList(catalogusListParameters);
    }

    /**
     * Read {@link Catalogus} filtered by {@link CatalogusListParameters}.
     * Throws a RuntimeException if the {@link Catalogus} can not be read.
     *
     * @param filter {@link CatalogusListParameters}.
     * @return {@link Catalogus}. Never 'null'!
     */
    public Catalogus readCatalogus(final CatalogusListParameters filter) {
        return ztcClient.catalogusList(filter)
                .getSingleResult()
                .orElseThrow(() -> new RuntimeException("Catalogus not found."));
    }

    @CacheResult(cacheName = ZTC_CACHE_TIME)
    public ZonedDateTime readCacheTime() {
        return ZonedDateTime.now();
    }

    /**
     * Read {@link Zaaktype} via URI.
     * Throws a RuntimeException if the {@link Zaaktype} can not be read.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return {@link Zaaktype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype readZaaktype(final URI zaaktypeURI) {
        return createInvocationBuilder(zaaktypeURI).get(Zaaktype.class);
    }

    /**
     * Read {@link Zaaktype} via UUID.
     * Throws a RuntimeException if the {@link Zaaktype} can not be read.
     *
     * @param zaaktypeUuid UUID of {@link Zaaktype}.
     * @return {@link Zaaktype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype readZaaktype(final UUID zaaktypeUuid) {
        return ztcClient.zaaktypeRead(zaaktypeUuid);
    }

    /**
     * List instances of {@link Zaaktype} in {@link Catalogus}.
     *
     * @param catalogusURI URI of {@link Catalogus}.
     * @return List of {@link Zaaktype} instances
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public List<Zaaktype> listZaaktypen(final URI catalogusURI) {
        return ztcClient.zaaktypeList(new ZaaktypeListParameters(catalogusURI)).getResults();
    }

    /**
     * Read {@link Statustype} via URI.
     * Throws a RuntimeException if the {@link Statustype} can not be read.
     *
     * @param statustypeURI URI of {@link Statustype}.
     * @return {@link Statustype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_STATUSTYPE)
    public Statustype readStatustype(final URI statustypeURI) {
        return createInvocationBuilder(statustypeURI).get(Statustype.class);
    }

    /**
     * Read {@link Statustype} via its UUID.
     * Throws a RuntimeException if the {@link Statustype} can not be read.
     *
     * @param statustypeUUID UUID of {@link Statustype}.
     * @return {@link Statustype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_STATUSTYPE)
    public Statustype readStatustype(final UUID statustypeUUID) {
        return ztcClient.statustypeRead(statustypeUUID);
    }

    /**
     * Read the {@link Statustype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Statustype}.
     */
    @CacheResult(cacheName = ZTC_STATUSTYPE)
    public List<Statustype> readStatustypen(final URI zaaktypeURI) {
        return ztcClient.statustypeList(new StatustypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Read the {@link ZaaktypeInformatieobjecttype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link ZaaktypeInformatieobjecttype}.
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_INFORMATIEOBJECTTYPE)
    public List<ZaaktypeInformatieobjecttype> readZaaktypeInformatieobjecttypen(final URI zaaktypeURI) {
        return ztcClient.zaaktypeinformatieobjecttypeList(new ZaaktypeInformatieobjecttypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Read the {@link Informatieobjecttype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Informatieobjecttype}.
     */
    @CacheResult(cacheName = ZTC_INFORMATIEOBJECTTYPE)
    public List<Informatieobjecttype> readInformatieobjecttypen(final URI zaaktypeURI) {
        return readZaaktypeInformatieobjecttypen(zaaktypeURI).stream()
                .map(zaaktypeInformatieobjecttype -> readInformatieobjecttype(zaaktypeInformatieobjecttype.getInformatieobjecttype())).toList();
    }

    /**
     * Read {@link Resultaattype} via its URI.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param resultaattypeURI URI of {@link Resultaattype}.
     * @return {@link Resultaattype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_RESULTAATTYPE)
    public Resultaattype readResultaattype(final URI resultaattypeURI) {
        return createInvocationBuilder(resultaattypeURI).get(Resultaattype.class);
    }

    /**
     * Read {@link Besluittype} via its URI.
     * Throws a RuntimeException if the {@link Besluittype} can not be read.
     *
     * @param besluittypeURI URI of {@link Besluittype}.
     * @return {@link Besluittype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_BESLUITTYPE)
    public Besluittype readBesluittype(final URI besluittypeURI) {
        return createInvocationBuilder(besluittypeURI).get(Besluittype.class);
    }

    /**
     * Read {@link Besluittype} via its UUID.
     * Throws a RuntimeException if the {@link Besluittype} can not be read.
     *
     * @param besluittypeUUID UUID of {@link Besluittype}.
     * @return {@link Besluittype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_BESLUITTYPE)
    public Besluittype readBesluittype(final UUID besluittypeUUID) {
        return ztcClient.besluittypeRead(besluittypeUUID);
    }

    /**
     * Read the {@link Besluittype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Besluittype}.
     */
    @CacheResult(cacheName = ZTC_BESLUITTYPE)
    public List<Besluittype> readBesluittypen(final URI zaaktypeURI) {
        return ztcClient.besluittypeList(new BesluittypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Read {@link Resultaattype} via its UUID.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param resultaattypeUUID UUID of {@link Resultaattype}.
     * @return {@link Resultaattype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_RESULTAATTYPE)
    public Resultaattype readResultaattype(final UUID resultaattypeUUID) {
        return ztcClient.resultaattypeRead(resultaattypeUUID);
    }

    /**
     * Read the {@link Resultaattype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Resultaattype}.
     */
    @CacheResult(cacheName = ZTC_RESULTAATTYPE)
    public List<Resultaattype> readResultaattypen(final URI zaaktypeURI) {
        return ztcClient.resultaattypeList(new ResultaattypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Find {@link Roltype} of {@link Zaaktype} and {@link AardVanRol}.
     * returns null if the {@link Resultaattype} can not be found
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @param aardVanRol  {@link AardVanRol}.
     * @return {@link Roltype} or NULL
     */
    @CacheResult(cacheName = ZTC_ROLTYPE)
    public Optional<Roltype> findRoltype(final URI zaaktypeURI, final AardVanRol aardVanRol) {
        return ztcClient.roltypeList(new RoltypeListParameters(zaaktypeURI, aardVanRol)).getSingleResult();
    }

    /**
     * Read {@link Roltype} of {@link Zaaktype} and {@link AardVanRol}.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @param aardVanRol  {@link AardVanRol}.
     * @return {@link Roltype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ROLTYPE)
    public Roltype readRoltype(final AardVanRol aardVanRol, final URI zaaktypeURI) {
        return ztcClient.roltypeList(new RoltypeListParameters(zaaktypeURI, aardVanRol)).getSingleResult()
                .orElseThrow(
                        () -> new RuntimeException(format("Zaaktype '%s': Roltype with aard '%s' not found.", zaaktypeURI.toString(), aardVanRol.toString())));
    }

    /**
     * Read {@link Roltype}s of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Roltype}s.
     */
    @CacheResult(cacheName = ZTC_ROLTYPE)
    public List<Roltype> listRoltypen(final URI zaaktypeURI) {
        return ztcClient.roltypeList(new RoltypeListParameters(zaaktypeURI)).getResults();
    }

    /**
     * Read {@link Roltype} via its UUID.
     * Throws a RuntimeException if the {@link Roltype} can not be read.
     *
     * @param roltypeUUID UUID of {@link Roltype}.
     * @return {@link Roltype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ROLTYPE)
    public Roltype readRoltype(final UUID roltypeUUID) {
        return ztcClient.roltypeRead(roltypeUUID);
    }

    /**
     * Read {@link Informatieobjecttype} via its URI.
     * Throws a RuntimeException if the {@link Informatieobjecttype} can not be read.
     *
     * @param informatieobjecttypeURI URI of {@link Informatieobjecttype}.
     * @return {@link Informatieobjecttype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_INFORMATIEOBJECTTYPE)
    public Informatieobjecttype readInformatieobjecttype(final URI informatieobjecttypeURI) {
        return createInvocationBuilder(informatieobjecttypeURI).get(Informatieobjecttype.class);
    }

    /**
     * Read {@link Informatieobjecttype} via its UUID.
     * Throws a RuntimeException if the {@link Informatieobjecttype} can not be read.
     *
     * @param informatieobjecttypeUUID UUID of {@link Informatieobjecttype}.
     * @return {@link Informatieobjecttype}.
     */
    @CacheResult(cacheName = ZTC_INFORMATIEOBJECTTYPE)
    public Informatieobjecttype readInformatieobjecttype(final UUID informatieobjecttypeUUID) {
        return ztcClient.informatieObjectTypeRead(informatieobjecttypeUUID);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE)
    public String clearZaaktypeCache() {
        return cleared(ZTC_ZAAKTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_STATUSTYPE)
    public String clearStatustypeCache() {
        return cleared(ZTC_STATUSTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_RESULTAATTYPE)
    public String clearResultaattypeCache() {
        return cleared(ZTC_RESULTAATTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_INFORMATIEOBJECTTYPE)
    public String clearInformatieobjecttypeCache() {
        return cleared(ZTC_INFORMATIEOBJECTTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_INFORMATIEOBJECTTYPE)
    public String clearZaaktypeInformatieobjecttypeCache() {
        return cleared(ZTC_ZAAKTYPE_INFORMATIEOBJECTTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_BESLUITTYPE)
    public String clearBesluittypeCache() {
        return cleared(ZTC_BESLUITTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_ROLTYPE)
    public String clearRoltypeCache() {
        return cleared(ZTC_ROLTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_CACHE_TIME)
    public String clearCacheTime() {
        return cleared(ZTC_CACHE_TIME);
    }

    @Override
    public List<String> cacheNames() {
        return CACHES;
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, zgwClientHeadersFactory.generateJWTToken());
    }
}
