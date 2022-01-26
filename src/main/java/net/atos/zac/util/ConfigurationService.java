/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    public static final String CATALOGUS_DOMEIN = "ALG";

    public static final String MELDING_KLEIN_EVENEMENT_ZAAKTYPE_IDENTIFICATIE = "melding-klein-evenement";

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    @ConfigProperty(name = "AUTH_RESOURCE")
    private String authResource;

    private URI catalogusURI;

    public URI readDefaultCatalogusURI() {
        if (catalogusURI == null) {
            final CatalogusListParameters catalogusListParameters = new CatalogusListParameters();
            catalogusListParameters.setDomein(CATALOGUS_DOMEIN);
            catalogusURI = ztcClientService.readCatalogus(catalogusListParameters).getUrl();
        }
        return catalogusURI;
    }

    public boolean isLocalDevelopment() {
        return authResource.contains("localhost");
    }
}
