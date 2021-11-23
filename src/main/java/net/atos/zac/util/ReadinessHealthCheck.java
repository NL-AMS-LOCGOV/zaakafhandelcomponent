/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.util.ConfigurationService.CATALOGUS_DOMEIN;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.ztc.ZTCClient;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;

@Readiness
@ApplicationScoped
public class ReadinessHealthCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(ReadinessHealthCheck.class.getName());

    private static final CatalogusListParameters CATALOGUS_LIST_PARAMETERS = new CatalogusListParameters();

    static {
        CATALOGUS_LIST_PARAMETERS.setDomein(CATALOGUS_DOMEIN);
    }

    @RestClient
    @Inject
    private ZTCClient ztcClient;

    @Override
    public HealthCheckResponse call() {
        LOG.fine(">>> Readiness health check called.");
        final HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Readiness");
        try {
            checkOpenZaak();
            checkDatabase();
            responseBuilder.up();
        } catch (final Exception exception) {
            responseBuilder.down().withData("error", exception.getMessage());
        }
        return responseBuilder.build();
    }

    private void checkOpenZaak() {
        ztcClient.catalogusList(CATALOGUS_LIST_PARAMETERS);
    }

    private void checkDatabase() {
        // ToDo: ESUITEDEV-25956
    }
}
