/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.configuratie.ConfiguratieService.CATALOGUS_DOMEIN;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;

@Readiness
@ApplicationScoped
public class OpenZaakReadinessHealthCheck implements HealthCheck {

    private static final CatalogusListParameters CATALOGUS_LIST_PARAMETERS = new CatalogusListParameters();

    static {
        CATALOGUS_LIST_PARAMETERS.setDomein(CATALOGUS_DOMEIN);
    }

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public HealthCheckResponse call() {
        try {
            ztcClientService.listCatalogus(CATALOGUS_LIST_PARAMETERS);
            return HealthCheckResponse.up(OpenZaakReadinessHealthCheck.class.getName());
        } catch (final Exception exception) {
            return HealthCheckResponse.named(OpenZaakReadinessHealthCheck.class.getName())
                    .withData("time", LocalDateTime.now().toString())
                    .withData("error", exception.getMessage())
                    .down()
                    .build();
        }
    }
}
