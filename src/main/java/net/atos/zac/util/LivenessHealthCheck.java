/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class LivenessHealthCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(LivenessHealthCheck.class.getName());

    @Override
    public HealthCheckResponse call() {
        LOG.fine(">>> Liveness health check called.");
        return HealthCheckResponse.named("Liveness").up().withData("time", LocalDateTime.now().toString()).build();
    }
}
