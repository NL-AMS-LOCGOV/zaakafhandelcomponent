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
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class SolrDeployer {

    @Inject
    @ConfigProperty(name = "SOLR_URL")
    private String solrUrl;

    private SolrClient solrClient;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr", solrUrl)).build();

        try {

            final CoreAdminResponse response = CoreAdminRequest.getStatus("zac", solrClient);
            if (response.getCoreStatus().get("zac") != null) {
                System.out.println(">>> Core ZAC exists.");
            }
            if (response.getCoreStatus().get("zacx") == null) {
                System.out.println(">>> Core ZACX does noet exist.");
            }


//            CoreAdminRequest coreAdminRequest = new CoreAdminRequest();
//            coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
//            coreAdminRequest.setIndexInfoNeeded(true);
//            CoreAdminResponse response = solrClient.request(coreAdminRequest);
//            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));
//
//            coreAdminRequest.setCoreName("zacx");
//            response = solrClient.request(coreAdminRequest);
//            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));


            // coreStatus = CoreAdminRequest.createCore("test", solrClient);


//            solrClient = new HttpSolrClient.Builder(String.format("%s/solr/zac", solrUrl)).build();
//            SchemaRequest.Fields fields = new SchemaRequest.Fields();
//            response = solrClient.request(fields);
//            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));
//
//            final SchemaRequest schemaRequest = new SchemaRequest();
//            response = solrClient.request(fields);
//            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));
//
//            fields = new SchemaRequest.Fields();
//            solrClient = new HttpSolrClient.Builder(String.format("%s/solr/zac", solrUrl)).build();
//            response = solrClient.request(fields);
//            response.forEach(((s, o) -> System.out.println(String.format("%s - %s", s, o))));
        } catch (final SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
