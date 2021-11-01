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

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.shared.util.ZGWApisInvocationBuilderFactory;
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

    private void cleared(final String cache) {
        LOG.info(String.format("Emptied cache: %s", cache));
    }

    public void clearCaches() {
        clearZaaktypeCache(true);
        clearStatustypeCache(false);
        clearResultaattypeCache(false);
    }

    public Catalogus getCatalogus(final CatalogusListParameters parameters) {
        return ztcClient.catalogusList(parameters)
                .getSingleResult()
                .orElseThrow(() -> new RuntimeException("Catalogus not found."));
    }

    // ZAAKTYPE

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE)
    public void clearZaaktypeCache(final boolean cascade) {
        cleared(ZTC_ZAAKTYPE);
        if (cascade) {
            clearZaaktypeResultaattypeCache();
            clearZaaktypeRoltypeCache();
            clearZaaktypeStatustypeCache();
        }
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype getZaaktype(final URI zaaktypeURI) {
        return ZGWApisInvocationBuilderFactory.create(zaaktypeURI).get(Zaaktype.class);
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype getZaaktype(final UUID zaaktypeUuid) {
        return ztcClient.zaaktypeRead(zaaktypeUuid);
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public Zaaktype getZaaktype(final String identificatie) {
        final ZaaktypeListParameters zaaktypeListParameters = new ZaaktypeListParameters();
        zaaktypeListParameters.setIdentificatie(identificatie);
        return ztcClient.zaaktypeList(zaaktypeListParameters).getResults().stream()
                .filter(zaaktype -> zaaktype.getEindeGeldigheid() == null)
                .findAny()
                .orElseThrow(() -> new RuntimeException(format("Zaaktype with identificatie '%s' not found.", identificatie)));
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE)
    public List<Zaaktype> findZaaktypes(final URI catalogusURI) {
        return ztcClient.zaaktypeList(new ZaaktypeListParameters(catalogusURI)).getResults();
    }

    // STATUSTYPE

    @CacheRemoveAll(cacheName = ZTC_STATUSTYPE)
    public void clearStatustypeCache(final boolean cascade) {
        cleared(ZTC_STATUSTYPE);
        if (cascade) {
            clearZaaktypeStatustypeCache();
        }
    }

    @CacheResult(cacheName = ZTC_STATUSTYPE)
    public Statustype getStatustype(final URI statustypeURI) {
        return ZGWApisInvocationBuilderFactory.create(statustypeURI).get(Statustype.class);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public void clearZaaktypeStatustypeCache() {
        cleared(ZTC_ZAAKTYPE_STATUSTYPE);
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public Statustype getStatustype(final URI zaaktypeURI, final String omschrijving) {
        return getZaaktype(zaaktypeURI).getStatustypen().stream()
                .map(this::getStatustype)
                .filter(statustype -> omschrijving.equals(statustype.getOmschrijving()))
                .findAny()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Zaaktype '%s': Statustype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE_STATUSTYPE)
    public Statustype getEindstatustype(final URI zaaktypeURI) {
        return ztcClient.statustypeList(new StatustypeListParameters(zaaktypeURI)).getSinglePageResults().stream()
                .filter(Statustype::getEindstatus)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Zaaktype '%s': No eind Status found for Zaaktype.", zaaktypeURI)));
    }

    // RESULTAATTYPE

    @CacheRemoveAll(cacheName = ZTC_RESULTAATTYPE)
    public void clearResultaattypeCache(final boolean cascade) {
        cleared(ZTC_RESULTAATTYPE);
        if (cascade) {
            clearZaaktypeResultaattypeCache();
        }
    }

    @CacheResult(cacheName = ZTC_RESULTAATTYPE)
    public Resultaattype getResultaattype(final URI resultaattypeURI) {
        return ZGWApisInvocationBuilderFactory.create(resultaattypeURI).get(Resultaattype.class);
    }

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE)
    public void clearZaaktypeResultaattypeCache() {
        cleared(ZTC_ZAAKTYPE_RESULTAATTYPE);
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE_RESULTAATTYPE)
    public Resultaattype getResultaattype(final URI zaaktypeURI, final String omschrijving) {
        return ztcClient.resultaattypeList(new ResultaattypeListParameters(zaaktypeURI)).getSinglePageResults().stream()
                .filter(rt -> StringUtils.equals(rt.getOmschrijving(), omschrijving))
                .findAny()
                .orElseThrow(
                        () -> new RuntimeException(
                                String.format("Zaaktype '%s': Resultaattype with omschrijving '%s' not found", zaaktypeURI, omschrijving)));
    }

    // ROLTYPE

    @CacheRemoveAll(cacheName = ZTC_ZAAKTYPE_ROLTYPE)
    public void clearZaaktypeRoltypeCache() {
        cleared(ZTC_ZAAKTYPE_ROLTYPE);
    }

    @CacheResult(cacheName = ZTC_ZAAKTYPE_ROLTYPE)
    public Roltype getRoltype(final URI zaaktypeURI, final AardVanRol aardVanRol) {
        return ztcClient.roltypeList(new RoltypeListParameters(zaaktypeURI, aardVanRol)).getSingleResult()
                .orElseThrow(
                        () -> new RuntimeException(format("Zaaktype '%s': Roltype with aard '%s' not found.", zaaktypeURI.toString(), aardVanRol.toString())));
    }

    public Informatieobjecttype getInformatieobjecttype(final URI uri) {
        return ZGWApisInvocationBuilderFactory.create(uri).get(Informatieobjecttype.class);
    }
}
