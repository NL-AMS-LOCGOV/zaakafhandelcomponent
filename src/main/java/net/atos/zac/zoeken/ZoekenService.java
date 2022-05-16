/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SimpleParams;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekResultaat;

@ApplicationScoped
public class ZoekenService {

    private static final String SOLR_CORE = "zac";

    private SolrClient solrClient;

    public ZoekenService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public ZoekResultaat<ZaakZoekObject> zoekZaak(final ZoekParameters zoekZaakParameters) {
        final SolrQuery query = new SolrQuery("*:*");
        if (StringUtils.isNotBlank(zoekZaakParameters.getTekst())) {
            query.setQuery("text:(%s)".formatted(zoekZaakParameters.getTekst()));
        }
        query.setParam("q.op", SimpleParams.AND_OPERATOR);
        query.setRows(zoekZaakParameters.getRows());
        query.setStart(zoekZaakParameters.getStart());
        query.addSort("identificatie", SolrQuery.ORDER.desc);
        try {
            final QueryResponse response = solrClient.query(query);
            return new ZoekResultaat<>(response.getBeans(ZaakZoekObject.class), response.getResults().getNumFound());
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

}
