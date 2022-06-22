/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static net.atos.zac.zoeken.model.FilterWaarde.LEEG;
import static net.atos.zac.zoeken.model.FilterWaarde.NIET_LEEG;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SimpleParams;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.SorteerVeld;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekResultaat;
import net.atos.zac.zoeken.model.ZoekVeld;

@ApplicationScoped
public class ZoekenService {

    private static final String SOLR_CORE = "zac";

    private SolrClient solrClient;

    public ZoekenService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public ZoekResultaat<ZaakZoekObject> zoek(final ZoekParameters zoekZaakParameters) {
        final SolrQuery query = new SolrQuery("*:*");
        query.addFilterQuery(String.format("type:%s", zoekZaakParameters.getType().toString()));
        zoekZaakParameters.getZoeken().forEach((zoekVeld, tekst) -> {
            if (StringUtils.isNotBlank(tekst)) {
                if (zoekVeld == ZoekVeld.IDENTIFICATIE) {
                    query.addFilterQuery(String.format("%s:(*%s*)", zoekVeld.getVeld(), tekst));
                } else {
                    query.addFilterQuery(String.format("%s:(%s)", zoekVeld.getVeld(), tekst));
                }

            }
        });

        zoekZaakParameters.getDatums().forEach((datumVeld, datum) -> {
            if (datum != null) {
                query.addFilterQuery(String.format("%s:[%s TO %s]", datumVeld.getVeld(),
                                                   datum.van() == null ? "*" : DateTimeFormatter.ISO_INSTANT.format(datum.van().atStartOfDay(ZoneOffset.UTC)),
                                                   datum.tot() == null ? "*" : DateTimeFormatter.ISO_INSTANT.format(datum.tot().atStartOfDay(ZoneOffset.UTC))));
            }
        });

        zoekZaakParameters.getBeschikbareFilters()
                .forEach(facetVeld -> query.addFacetField(String.format("{!ex=%s}%s", facetVeld, facetVeld.getVeld())));

        zoekZaakParameters.getFilters().forEach((filter, waarde) -> {
            if (LEEG.is(waarde)) {
                query.addFilterQuery(String.format("{!tag=%s}!%s:(*)", filter, filter.getVeld()));
            } else if (NIET_LEEG.is(waarde)) {
                query.addFilterQuery(String.format("{!tag=%s}%s:(*)", filter, filter.getVeld()));
            } else {
                query.addFilterQuery(String.format("{!tag=%s}%s:(\"%s\")", filter, filter.getVeld(), waarde));
            }
        });

        zoekZaakParameters.getFilterQueries().forEach((veld, waarde) -> query.addFilterQuery(String.format("%s:\"%s\"", veld, waarde)));

        query.setFacetMinCount(1);
        query.setFacetMissing(true);
        query.setParam("q.op", SimpleParams.AND_OPERATOR);
        query.setRows(zoekZaakParameters.getRows());
        query.setStart(zoekZaakParameters.getStart());
        query.addSort(zoekZaakParameters.getSortering().sorteerVeld().getVeld(),
                      zoekZaakParameters.getSortering().richting() == SorteerRichting.DESCENDING ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
        if (zoekZaakParameters.getSortering().sorteerVeld() != SorteerVeld.IDENTIFICATIE) {
            query.addSort(SorteerVeld.IDENTIFICATIE.getVeld(), SolrQuery.ORDER.desc);
        }
        try {
            final QueryResponse response = solrClient.query(query);
            final ZoekResultaat<ZaakZoekObject> zoekResultaat = new ZoekResultaat<>(response.getBeans(ZaakZoekObject.class),
                                                                                    response.getResults().getNumFound());
            response.getFacetFields().forEach(facetField -> {
                final FilterVeld facetVeld = FilterVeld.fromValue(facetField.getName());
                final List<String> waardes = new ArrayList<>();
                facetField.getValues().stream()
                        .filter(facet -> facet.getCount() > 0)
                        .forEach(facet -> waardes.add(facet.getName() == null ? LEEG.toString() : facet.getName()));
                zoekResultaat.addFilter(facetVeld, waardes);
            });
            return zoekResultaat;
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }
}
