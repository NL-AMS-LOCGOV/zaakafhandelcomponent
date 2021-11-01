/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import java.net.URI;

import javax.ejb.Singleton;
import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;

/**
 *
 */
@Singleton
public class ConfigurationService {

    //TODO ESUITEDEV-25102 vervangen van onderstaande placeholders
    public static final String BRON_ORGANISATIE = "123443210";

    public static final String VERANTWOORDELIJKE_ORGANISATIE = "316245124";

    private static final String CATALOGUS_DOMEIN = "ALG";

    @Inject
    private ZTCClientService ztcClientService;

    private URI catalogus;

    public URI getCatalogus() {
        if (catalogus == null) {
            catalogus = retrievCatalogus();
        }
        return catalogus;
    }

    private URI retrievCatalogus() {
        final CatalogusListParameters catalogusListParameters = new CatalogusListParameters();
        catalogusListParameters.setDomein(CATALOGUS_DOMEIN);
        return ztcClientService.getCatalogus(catalogusListParameters).getUrl();
    }
}
