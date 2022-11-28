/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.NAME;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.STRING;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.addField;
import static net.atos.zac.solr.schema.SolrSchemaUpdateHelper.deleteField;
import static net.atos.zac.zoeken.IndexeerService.SOLR_CORE;

import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.SolrException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Singleton
public class SolrDeployer {

    private static final Logger LOG = Logger.getLogger(SolrDeployer.class.getName());

    private static final String VERSION_FIELD_PREFIX = "schema_version_";

    private static final int SOLR_STATUS_OK = 0;

    private static final int WAIT_FOR_SOLR_SECONDS = 10;

    @Inject
    @ConfigProperty(name = "SOLR_URL")
    private String solrUrl;

    @Resource
    private ManagedExecutorService managedExecutor;

    @Inject
    private IndexeerService indexeerService;

    private SolrClient solrClient;

    private List<SolrSchemaUpdate> schemaUpdates;

    @Inject
    public void setSchemaUpdates(final Instance<SolrSchemaUpdate> schemaUpdates) {
        this.schemaUpdates = schemaUpdates.stream()
                .sorted(Comparator.comparingInt(SolrSchemaUpdate::getVersie))
                .toList();
    }

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        solrClient = new Http2SolrClient.Builder("%s/solr/%s".formatted(solrUrl, SOLR_CORE)).build();
        waitForSolrAvailability();
        try {
            final int currentVersion = getCurrentVersion();
            LOG.info("Current version of Solr core '%s': %d".formatted(SOLR_CORE, currentVersion));
            if (currentVersion == schemaUpdates.get(schemaUpdates.size() - 1).getVersie()) {
                LOG.info("Solr core '%s' is up to date. No Solr schema migration needed.".formatted(SOLR_CORE));
            } else {
                schemaUpdates.stream()
                        .skip(currentVersion)
                        .forEach(this::apply);

                schemaUpdates.stream()
                        .skip(currentVersion)
                        .flatMap(schemaUpdate -> schemaUpdate.getTeHerindexerenZoekObjectTypes().stream())
                        .collect(Collectors.toSet())
                        .forEach(this::startHerindexeren);

            }
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForSolrAvailability() {
        while (true) {
            try {
                if (new SolrPing().setActionPing().process(solrClient).getStatus() == SOLR_STATUS_OK) {
                    return;
                }
            } catch (final SolrServerException | IOException | SolrException e) {
                // nothing to report
            }
            LOG.warning("Waiting for %d seconds for Solr core '%s' to become available...".formatted(
                    WAIT_FOR_SOLR_SECONDS, SOLR_CORE));
            try {
                Thread.sleep(Duration.ofSeconds(WAIT_FOR_SOLR_SECONDS).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getCurrentVersion() throws SolrServerException, IOException {
        return new SchemaRequest.Fields().process(solrClient).getFields().stream()
                .map(field -> field.get(NAME).toString())
                .filter(fieldName -> fieldName.startsWith(VERSION_FIELD_PREFIX))
                .findAny()
                .map(versionFieldName -> Integer.valueOf(
                        StringUtils.substringAfter(versionFieldName, VERSION_FIELD_PREFIX)))
                .orElse(0);
    }

    private void apply(final SolrSchemaUpdate schemaUpdate) {
        LOG.info("Updating Solr core '%s' to version: %d".formatted(SOLR_CORE, schemaUpdate.getVersie()));
        try {
            final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
            schemaUpdates.addAll(schemaUpdate.getSchemaUpdates());
            schemaUpdates.addAll(updateVersionField(schemaUpdate.getVersie()));
            new SchemaRequest.MultiUpdate(schemaUpdates).process(solrClient);
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SchemaRequest.Update> updateVersionField(final int version) {
        final List<SchemaRequest.Update> schemaUpdates = new LinkedList<>();
        if (version > 1) {
            schemaUpdates.add(deleteField(VERSION_FIELD_PREFIX + (version - 1)));
        }
        schemaUpdates.add(addField(VERSION_FIELD_PREFIX + version, STRING, false, false));
        return schemaUpdates;
    }

    private void startHerindexeren(final ZoekObjectType type) {
        managedExecutor.submit(() -> indexeerService.herindexeren(type));
    }
}
