/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static javax.ejb.TransactionManagementType.BEAN;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

/**
 * See http://are-you-ready.de/blog/2017/06/08/integrating-flyway-with-java-ee-and-using-its-datasource/
 */

@TransactionManagement(value = BEAN)
public class FlywayIntegrator {

    private static final Logger LOG = Logger.getLogger(FlywayIntegrator.class.getName());

    public static final String SCHEMA = "zaakafhandelcomponent";

    private static final String SCHEMA_FILES_LOCATION = "schemas";

    private static final String SCHEMA_PLACEHOLDER = "schema";

    @Resource(lookup = "java:comp/env/jdbc/Datasource")
    private DataSource dataSource;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        if (dataSource == null) {
            throw new RuntimeException("No datasource found to execute the db migrations!");
        }

        final Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(SCHEMA_FILES_LOCATION)
                .schemas(SCHEMA)
                .placeholders(Map.of(SCHEMA_PLACEHOLDER, SCHEMA))
                .load();

        final MigrationInfo migrationInfo = flyway.info().current();

        if (migrationInfo == null) {
            LOG.info("No existing database at the actual datasource");
        } else {
            LOG.info(String.format("Found a database with the version: %s : %s", migrationInfo.getVersion(), migrationInfo.getDescription()));
        }

        flyway.migrate();
        LOG.info(String.format("Successfully migrated to database version: %s", flyway.info().current().getVersion()));
    }
}
