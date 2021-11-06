/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc;

import static java.lang.String.format;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_RESULTAATTYPE;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_STATUSTYPE;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_ZAAKTYPE;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_ZAAKTYPE_RESULTAATTYPE;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_ZAAKTYPE_ROLTYPE;
import static net.atos.client.zgw.shared.cache.CacheEnum.ZTC_ZAAKTYPE_STATUSTYPE;
import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;
import static net.atos.client.zgw.shared.util.ZGWClientHeadersFactory.generateJWTToken;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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

@ApplicationScoped
public class ZTCClientService {
    private static final Logger LOG = Logger.getLogger(ZTCClientService.class.getName());

    @Inject
    @RestClient
    private ZTCClient ztcClient;

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
     * Read {@link Zaaktype} via Identificatie.
     * Throws a RuntimeException if the {@link Zaaktype} can not be read.
     *
     * @param identificatie Identificatie of {@link Zaaktype}.
     * @return {@link Zaaktype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype readZaaktype(final String identificatie) {
        final ZaaktypeListParameters zaaktypeListParameters = new ZaaktypeListParameters();
        zaaktypeListParameters.setIdentificatie(identificatie);
        return ztcClient.zaaktypeList(zaaktypeListParameters).getResults().stream()
                .filter(zaaktype -> zaaktype.getEindeGeldigheid() == null)
                .findAny()
                .orElseThrow(() -> new RuntimeException(format("Zaaktype with identificatie '%s' not found.", identificatie)));
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
     * Read {@link Statustype} of {@link Zaaktype} and Omschrijving of {@link Statustype}.
     * Throws a RuntimeException if the {@link Statustype} can not be read.
     *
     * @param zaaktypeURI  URI of {@link Zaaktype}.
     * @param omschrijving Omschrijving of {@link Statustype}/
     * @return {@link Statustype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public Statustype readStatustype(final URI zaaktypeURI, final String omschrijving) {
        return readZaaktype(zaaktypeURI).getStatustypen().stream()
                .map(this::readStatustype)
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
    @CacheResult(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public Statustype readStatustypeEind(final URI zaaktypeURI) {
        return ztcClient.statustypeList(new StatustypeListParameters(zaaktypeURI)).getSinglePageResults().stream()
                .filter(Statustype::getEindstatus)
                .findFirst()
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
     * Read {@link Resultaattype} of {@link Zaaktype} and Omschrijving of {@link Resultaattype}.
     * Throws a RuntimeException if the {@link Resultaattype} can not be read.
     *
     * @param zaaktypeURI  URI of {@link Zaaktype}.
     * @param omschrijving Omschrijving of {@link Resultaattype}/
     * @return {@link Resultaattype}. Never 'null'!
     */
    @CacheResult(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE)
    public Resultaattype readResultaattype(final URI zaaktypeURI, final String omschrijving) {
        return ztcClient.resultaattypeList(new ResultaattypeListParameters(zaaktypeURI)).getSinglePageResults().stream()
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

    /**
     * Clear all caches.
     */
    public void clearCaches() {
        clearZaaktypeCache(true);
        clearStatustypeCache(false);
        clearResultaattypeCache(false);
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_ZAAKTYPE}
     *
     * @param cascade Wheter all related caches should also be cleared.
     */
    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE)
    public void clearZaaktypeCache(final boolean cascade) {
        cleared(ZTC_ZAAKTYPE);
        if (cascade) {
            clearZaaktypeResultaattypeCache();
            clearZaaktypeRoltypeCache();
            clearZaaktypeStatustypeCache();
        }
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_STATUSTYPE}
     *
     * @param cascade Wheter all related caches should also be cleared.
     */
    @CacheRemoveAll(cacheName = ZTC_STATUSTYPE)
    public void clearStatustypeCache(final boolean cascade) {
        cleared(ZTC_STATUSTYPE);
        if (cascade) {
            clearZaaktypeStatustypeCache();
        }
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_ZAAKTYPE_STATUSTYPE}
     */
    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public void clearZaaktypeStatustypeCache() {
        cleared(ZTC_ZAAKTYPE_STATUSTYPE);
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_RESULTAATTYPE}
     *
     * @param cascade Wheter all related caches should also be cleared.
     */
    @CacheRemoveAll(cacheName = ZTC_RESULTAATTYPE)
    public void clearResultaattypeCache(final boolean cascade) {
        cleared(ZTC_RESULTAATTYPE);
        if (cascade) {
            clearZaaktypeResultaattypeCache();
        }
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_ZAAKTYPE_RESULTAATTYPE}
     */
    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE)
    public void clearZaaktypeResultaattypeCache() {
        cleared(ZTC_ZAAKTYPE_RESULTAATTYPE);
    }

    /**
     * Clear caches for type {@link net.atos.client.zgw.shared.cache.CacheEnum#ZTC_ZAAKTYPE_ROLTYPE}
     */
    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_ROLTYPE)
    public void clearZaaktypeRoltypeCache() {
        cleared(ZTC_ZAAKTYPE_ROLTYPE);
    }

    private void cleared(final String cache) {
        LOG.info(String.format("Emptied cache: %s", cache));
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateJWTToken());
    }
}
