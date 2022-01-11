/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseReadinessHealthCheck implements HealthCheck {

    private static final int DEFAULT_POSTGRES_PORT = 5432;

    @Inject
    @ConfigProperty(name = "DB_HOST")
    private String dbHost;

    private String dbHostName;

    private int dbHostPort;

    @PostConstruct
    public void init() {
        dbHostName = substringBefore(dbHost, ":");
        final String port = substringAfter(dbHost, ":");
        dbHostPort = isNotEmpty(port) ? Integer.valueOf(port) : DEFAULT_POSTGRES_PORT;
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
