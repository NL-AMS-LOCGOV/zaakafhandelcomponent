/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SimpleParams;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.FilterVeld;
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
        final StringBuilder queryBuilder = new StringBuilder();
        zoekZaakParameters.getZoeken().forEach((zoekVeld, tekst) -> {
            if (StringUtils.isNotBlank(tekst)) {
                queryBuilder.append(String.format("%s:(%s) ", zoekVeld.getVeld(), tekst));
            }
        });
        if (StringUtils.isNotBlank(queryBuilder.toString())) {
            query.setQuery(queryBuilder.toString());
        }
        zoekZaakParameters.getBeschikbareFilters()
                .forEach(facetVeld -> query.addFacetField(String.format("{!ex=%s}%s", facetVeld, facetVeld.getVeld())));

        zoekZaakParameters.getFilters()
                .forEach((filter, waarde) -> query.addFilterQuery(String.format("{!tag=%s}%s:(\"%s\")", filter, filter.getVeld(), waarde)));

        query.setParam("q.op", SimpleParams.AND_OPERATOR);
        query.setRows(zoekZaakParameters.getRows());
        query.setStart(zoekZaakParameters.getStart());
        query.addSort(zoekZaakParameters.getSorteren().getSorteerVeld().getVeld(),
                      zoekZaakParameters.getSorteren().getRichting() == SorteerRichting.DESCENDING ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
        try {
            final QueryResponse response = solrClient.query(query);
            final ZoekResultaat<ZaakZoekObject> zoekResultaat = new ZoekResultaat<>(response.getBeans(ZaakZoekObject.class),
                                                                                    response.getResults().getNumFound());
            response.getFacetFields().forEach(facetField -> {
                FilterVeld facetVeld = FilterVeld.fromValue(facetField.getName());
                final List<String> waardes = facetField.getValues().stream().map(FacetField.Count::getName).toList();
                zoekResultaat.addFilter(facetVeld, waardes);
            });
            return zoekResultaat;
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }
}
