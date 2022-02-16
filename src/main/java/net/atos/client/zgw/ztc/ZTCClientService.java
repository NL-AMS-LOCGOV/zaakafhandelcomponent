/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.util.ClientFactory;
import net.atos.client.zgw.shared.cache.Caching;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.ztc.model.AardVanRol;
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
            ZTC_RESULTAATTYPE,
            ZTC_STATUSTYPE,
            ZTC_ZAAKTYPE,
            ZTC_ZAAKTYPE_MANAGED,
            ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED,
            ZTC_ZAAKTYPE_ROLTYPE,
            ZTC_ZAAKTYPE_STATUSTYPE_MANAGED,
            ZTC_ZAAKTYPE_URL);

    @Inject
    @RestClient
    private ZTCClient ztcClient;

    @Inject
    private ZGWClientHeadersFactory zgwClientHeadersFactory;

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

    /**
     * Read {@link Zaaktype} via URI.
     * Throws a RuntimeException if the {@link Zaaktype} can not be read.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return {@link Zaaktype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_MANAGED)
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
    @CacheResult(cacheName = ZTC_ZAAKTYPE_MANAGED)
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
     * Read zaaktype {@link URI} via Identificatie.
     * Throws a RuntimeException if the {@link Zaaktype} can not be found.
     *
     * @param identificatie Identificatie of {@link Zaaktype}.
     * @return {@link URI}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_URL)
    public URI readZaaktypeUrl(final String identificatie) {
        final ZaaktypeListParameters zaaktypeListParameters = new ZaaktypeListParameters();
        zaaktypeListParameters.setIdentificatie(identificatie);
        return ztcClient.zaaktypeList(zaaktypeListParameters).getResults().stream()
                .filter(zaaktype -> zaaktype.getEindeGeldigheid() == null)
                .map(Zaaktype::getUrl)
                .findAny()
                .orElseThrow(() -> new RuntimeException(format("Zaaktype with identificatie '%s' not found.", identificatie)));
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
     * Read the {@link Statustype} of {@link Zaaktype}.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return list of {@link Statustype}.
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_STATUSTYPE_MANAGED)
    public List<Statustype> readStatustypen(final URI zaaktypeURI) {
        return ztcClient.statustypeList(new StatustypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Read {@link Statustype} of {@link Zaaktype} and Omschrijving of {@link Statustype}.
     * Throws a RuntimeException if the {@link Statustype} can not be read.
     *
     * @param zaaktypeURI  URI of {@link Zaaktype}.
     * @param omschrijving Omschrijving of {@link Statustype}/
     * @return {@link Statustype}. Never 'null'!
     */
    public Statustype readStatustype(final List<Statustype> statustypes, final String omschrijving, final URI zaaktypeURI) {
        return statustypes.stream()
                .filter(statustype -> omschrijving.equals(statustype.getOmschrijving()))
                .findAny()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Zaaktype '%s': Statustype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    /**
     * Read the Eind {@link Statustype} of {@link Zaaktype}.
     * Throws a RuntimeException if the {@link Statustype} can not be read.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @return {@link Statustype}. Never 'null'!
     */
    public Statustype readStatustypeEind(final List<Statustype> statustypes, final URI zaaktypeURI) {
        return statustypes.stream()
                .filter(Statustype::getEindstatus)
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Zaaktype '%s': No eind Status found for Zaaktype.", zaaktypeURI)));
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
    @CacheResult(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED)
    public List<Resultaattype> readResultaattypen(final URI zaaktypeURI) {
        return ztcClient.resultaattypeList(new ResultaattypeListParameters(zaaktypeURI)).getSinglePageResults();
    }

    /**
     * Read {@link Resultaattype} of {@link Zaaktype} and Omschrijving of {@link Resultaattype}.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param zaaktypeURI  URI of {@link Zaaktype}.
     * @param omschrijving Omschrijving of {@link Resultaattype}/
     * @return {@link Resultaattype}. Never 'null'!
     */
    public Resultaattype readResultaattype(List<Resultaattype> resultaattypes, final String omschrijving, final URI zaaktypeURI) {
        return resultaattypes.stream()
                .filter(resultaattype -> StringUtils.equals(resultaattype.getOmschrijving(), omschrijving))
                .findAny()
                .orElseThrow(
                        () -> new RuntimeException(
                                String.format("Zaaktype '%s': Resultaattype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    /**
     * Read {@link Roltype} of {@link Zaaktype} and {@link AardVanRol}.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param zaaktypeURI URI of {@link Zaaktype}.
     * @param aardVanRol  {@link AardVanRol}.
     * @return {@link Roltype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_ROLTYPE)
    public Roltype readRoltype(final URI zaaktypeURI, final AardVanRol aardVanRol) {
        return ztcClient.roltypeList(new RoltypeListParameters(zaaktypeURI, aardVanRol)).getSingleResult()
                .orElseThrow(
                        () -> new RuntimeException(format("Zaaktype '%s': Roltype with aard '%s' not found.", zaaktypeURI.toString(), aardVanRol.toString())));
    }

    /**
     * Read {@link Informatieobjecttype} via its URI.
     * Throws a RuntimeException if the {@link Informatieobjecttype} can not be read.
     *
     * @param informatieobjecttypeURI URI of {@link Informatieobjecttype}.
     * @return {@link Informatieobjecttype}. Never 'null'!
     */
    public Informatieobjecttype readInformatieobjecttype(final URI informatieobjecttypeURI) {
        return createInvocationBuilder(informatieobjecttypeURI).get(Informatieobjecttype.class);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE)
    public String clearZaaktypeCache() {
        return cleared(ZTC_ZAAKTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_MANAGED)
    public String clearZaaktypeManagedCache() {
        return cleared(ZTC_ZAAKTYPE_MANAGED);
    }

    @CacheRemove(cacheName = ZTC_ZAAKTYPE_MANAGED)
    public void updateZaaktypeCache(final URI key) {
        removed(ZTC_ZAAKTYPE_MANAGED, key);
    }

    @CacheRemove(cacheName = ZTC_ZAAKTYPE_MANAGED)
    public void updateZaaktypeCache(final UUID key) {
        removed(ZTC_ZAAKTYPE_MANAGED, key);
    }

    @CacheRemoveAll(cacheName = ZTC_STATUSTYPE)
    public String clearStatustypeCache() {
        return cleared(ZTC_STATUSTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_STATUSTYPE_MANAGED)
    public String clearZaaktypeStatustypeManagedCache() {
        return cleared(ZTC_ZAAKTYPE_STATUSTYPE_MANAGED);
    }

    @CacheRemove(cacheName = ZTC_ZAAKTYPE_STATUSTYPE_MANAGED)
    public void updateZaaktypeStatustypeCache(final URI key) {
        removed(ZTC_ZAAKTYPE_STATUSTYPE_MANAGED, key);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_URL)
    public String clearZaaktypeUrlCache() {
        return cleared(ZTC_ZAAKTYPE_URL);
    }

    @CacheRemoveAll(cacheName = ZTC_RESULTAATTYPE)
    public String clearResultaattypeCache() {
        return cleared(ZTC_RESULTAATTYPE);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED)
    public String clearZaaktypeResultaattypeManagedCache() {
        return cleared(ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED);
    }

    @CacheRemove(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED)
    public void updateZaaktypeResultaattypeCache(final URI key) {
        removed(ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED, key);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_ROLTYPE)
    public String clearZaaktypeRoltypeCache() {
        return cleared(ZTC_ZAAKTYPE_ROLTYPE);
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
