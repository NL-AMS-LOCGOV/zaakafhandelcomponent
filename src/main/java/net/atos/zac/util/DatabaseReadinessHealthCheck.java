/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseReadinessHealthCheck implements HealthCheck {

    @Inject
    @ConfigProperty(name = "DB_HOST")
    private String dbHost;

    private String dbHostName;

    private int dbHostPort;

    @PostConstruct
    public void init() {
        dbHostName = StringUtils.substringBefore(dbHost, ":");
        dbHostPort = Integer.valueOf(StringUtils.substringAfter(dbHost, ":"));
    }

    @Override
    public HealthCheckResponse call() {
        try {
            pingServer();
            return HealthCheckResponse.up(DatabaseReadinessHealthCheck.class.getName());
        } catch (final Exception exception) {
            return HealthCheckResponse.named(DatabaseReadinessHealthCheck.class.getName())
                    .withData("time", LocalDateTime.now().toString())
                    .withData("error", exception.getMessage())
                    .down()
                    .build();
        }
    }

    private void pingServer() throws IOException {
        try (final Socket socket = new Socket(dbHostName, dbHostPort)) {
            // Nothing to do.
        }
    }
}
