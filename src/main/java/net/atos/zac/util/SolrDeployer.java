/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.zoeken.IndexeerService.SOLR_CORE;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class SolrDeployer {

    private static final Logger LOG = Logger.getLogger(SolrDeployer.class.getName());

    private static final String VERSION_FIELD_PREFIX = "schema_version_";

    @Inject
    @ConfigProperty(name = "SOLR_URL")
    private String solrUrl;

    private List<SolrSchemaUpdate> schemaUpdates;

    private SolrClient solrClient;

    @Inject
    public void setSchemaUpdates(final Instance<SolrSchemaUpdate> schemaUpdates) {
        this.schemaUpdates = schemaUpdates.stream()
                .sorted(Comparator.comparingInt(SolrSchemaUpdate::getVersion))
                .toList();
    }

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        solrClient = new Http2SolrClient.Builder("%s/solr/%s".formatted(solrUrl, SOLR_CORE)).build();
        try {
            final int currentVersion = getCurrentVersion();
            if (currentVersion == schemaUpdates.get(schemaUpdates.size() - 1).getVersion()) {
                LOG.info("Solr core '%s' is up to date. No Solr schema migration needed.".formatted(SOLR_CORE));
            } else {
                schemaUpdates.stream()
                        .skip(currentVersion)
                        .forEach(this::apply);

                schemaUpdates.stream()
                        .skip(currentVersion)
                        .flatMap(schemaUpdate -> schemaUpdate.herindexeren().stream())
                        .collect(Collectors.toSet())
                        .forEach(this::startHerindexeren);
            }
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getCurrentVersion() throws SolrServerException, IOException {
        final String versionFieldName = new SchemaRequest.Fields().process(solrClient).getFields().stream()
                .map(field -> field.get("name").toString())
                .filter(fieldName -> fieldName.startsWith(VERSION_FIELD_PREFIX))
                .findAny()
                .orElse(null);
        final int currentVersion = versionFieldName != null ?
                Integer.valueOf(StringUtils.substringAfter(versionFieldName, VERSION_FIELD_PREFIX)) : 0;
        LOG.info("Current version of Solr core '%s': %d".formatted(SOLR_CORE, currentVersion));
        return currentVersion;
    }

    private void apply(final SolrSchemaUpdate schemaUpdate) {
        LOG.info("Updating Solr core '%s' to version: %d".formatted(SOLR_CORE, schemaUpdate.getVersion()));
        try {
            schemaUpdate.updateSchema();
            updateVersionField(schemaUpdate.getVersion());
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateVersionField(final int version) throws SolrServerException, IOException {
        if (version > 1) {
            new SchemaRequest.DeleteField(VERSION_FIELD_PREFIX + (version - 1))
                    .process(solrClient);
        }
        new SchemaRequest.AddField(
                Map.of("name", VERSION_FIELD_PREFIX + version,
                       "type", "string",
                       "indexed", false,
                       "stored", false
                )).process(solrClient);
    }

    private void startHerindexeren(final ZoekObjectType zoekObjectType) {
        LOG.info("Start herindexern Solr core '%s': %s".formatted(SOLR_CORE, zoekObjectType.toString()));
    }
}
