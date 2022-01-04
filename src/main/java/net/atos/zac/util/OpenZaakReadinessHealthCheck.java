/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.util.ConfigurationService.CATALOGUS_DOMEIN;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.ztc.ZTCClient;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;

@Readiness
@ApplicationScoped
public class OpenZaakReadinessHealthCheck implements HealthCheck {

    private static final CatalogusListParameters CATALOGUS_LIST_PARAMETERS = new CatalogusListParameters();

    static {
        CATALOGUS_LIST_PARAMETERS.setDomein(CATALOGUS_DOMEIN);
    }

    @RestClient
    @Inject
    private ZTCClient ztcClient;

    @Override
    public HealthCheckResponse call() {
        try {
            ztcClient.catalogusList(CATALOGUS_LIST_PARAMETERS);
            return HealthCheckResponse.up(OpenZaakReadinessHealthCheck.class.getSimpleName());
        } catch (final Exception exception) {
            return HealthCheckResponse.named(OpenZaakReadinessHealthCheck.class.getSimpleName())
                    .withData("time", LocalDateTime.now().toString())
                    .withData("error", exception.getMessage())
                    .down()
                    .build();
        }
    }
}
