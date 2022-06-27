/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.util.NamedList;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class SolrDeployer {

    @Inject
    @ConfigProperty(name = "SOLR_URL")
    private String solrUrl;

    private SolrClient solrClient;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr", solrUrl)).build();

        final CoreAdminRequest coreAdminRequest = new CoreAdminRequest();
        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        coreAdminRequest.setIndexInfoNeeded(false);

        try {
            NamedList<Object> response = solrClient.request(coreAdminRequest);
            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));

            SchemaRequest.Fields fields = new SchemaRequest.Fields();
            solrClient = new HttpSolrClient.Builder(String.format("%s/solr/zac", solrUrl)).build();
            response = solrClient.request(fields);
            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));

            final SchemaRequest schemaRequest = new SchemaRequest();
            response = solrClient.request(fields);
            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));

            fields = new SchemaRequest.Fields();
            solrClient = new HttpSolrClient.Builder(String.format("%s/solr/zac", solrUrl)).build();
            response = solrClient.request(fields);
            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
