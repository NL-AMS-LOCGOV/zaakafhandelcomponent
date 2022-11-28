/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static net.atos.zac.zoeken.model.FilterWaarde.LEEG;
import static net.atos.zac.zoeken.model.FilterWaarde.NIET_LEEG;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SimpleParams;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.FilterResultaat;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.SorteerVeld;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekResultaat;
import net.atos.zac.zoeken.model.ZoekVeld;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@ApplicationScoped
public class ZoekenService {

    private static final String SOLR_CORE = "zac";

    private static final String NON_EXISTING_ZAAKTYPE = "-NON-EXISTING-ZAAKTYPE-";

    private static final String ZAAKTYPE_OMSCHRIJVING_VELD = "zaaktypeOmschrijving";

    @Inject
    private PolicyService policyService;

    private SolrClient solrClient;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public ZoekenService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new Http2SolrClient.Builder(format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public ZoekResultaat<? extends ZoekObject> zoek(final ZoekParameters zoekParameters) {
        final SolrQuery query = new SolrQuery("*:*");

        if (loggedInUserInstance.get() != null) { // SignaleringenJob heeft geen ingelogde gebruiker
            applyAllowedZaaktypenPolicy(query);
        }

        if (zoekParameters.getType() != null) {
            query.addFilterQuery(format("type:%s", zoekParameters.getType().toString()));
        }

        zoekParameters.getZoeken().forEach((zoekVeld, tekst) -> {
            if (StringUtils.isNotBlank(tekst)) {
                if (zoekVeld == ZoekVeld.ZAAK_IDENTIFICATIE || zoekVeld == ZoekVeld.TAAK_ZAAK_ID) {
                    query.addFilterQuery(format("%s:(*%s*)", zoekVeld.getVeld(), tekst));
                } else {
                    query.addFilterQuery(format("%s:(%s)", zoekVeld.getVeld(), tekst));
                }

            }
        });

        zoekParameters.getDatums().forEach((datumVeld, datum) -> {
            if (datum != null) {
                query.addFilterQuery(
                        format("%s:[%s TO %s]", datumVeld.getVeld(),
                               datum.van() == null ? "*" : DateTimeFormatter.ISO_INSTANT.format(datum.van().atStartOfDay(ZoneId.systemDefault())),
                               datum.tot() == null ? "*" : DateTimeFormatter.ISO_INSTANT.format(datum.tot().atStartOfDay(ZoneId.systemDefault()))));
            }
        });

        zoekParameters.getBeschikbareFilters()
                .forEach(facetVeld -> query.addFacetField(format("{!ex=%s}%s", facetVeld, facetVeld.getVeld())));

        zoekParameters.getFilters().forEach((filter, waardes) -> {
            if (CollectionUtils.isNotEmpty(waardes)) {
                final String waarde = String.join("\" OR \"", waardes);
                if (LEEG.is(waarde)) {
                    query.addFilterQuery(format("{!tag=%s}!%s:(*)", filter, filter.getVeld()));
                } else if (NIET_LEEG.is(waarde)) {
                    query.addFilterQuery(format("{!tag=%s}%s:(*)", filter, filter.getVeld()));
                } else {
                    query.addFilterQuery(format("{!tag=%s}%s:(\"%s\")", filter, filter.getVeld(), waarde));
                }
            }
        });

        zoekParameters.getFilterQueries().forEach((veld, waarde) -> query.addFilterQuery(format("%s:\"%s\"", veld, waarde)));

        query.setFacetMinCount(1);
        query.setFacetMissing(!zoekParameters.isGlobaalZoeken());
        query.setFacet(true);
        query.setParam("q.op", SimpleParams.AND_OPERATOR);
        query.setRows(zoekParameters.getRows());
        query.setStart(zoekParameters.getStart());
        query.addSort(zoekParameters.getSortering().sorteerVeld().getVeld(),
                      zoekParameters.getSortering().richting() == SorteerRichting.DESCENDING ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);

        if (zoekParameters.getSortering().sorteerVeld() != SorteerVeld.CREATED) {
            query.addSort(SorteerVeld.CREATED.getVeld(), SolrQuery.ORDER.desc);
        }
        if (zoekParameters.getSortering().sorteerVeld() != SorteerVeld.ZAAK_IDENTIFICATIE) {
            query.addSort(SorteerVeld.ZAAK_IDENTIFICATIE.getVeld(), SolrQuery.ORDER.desc);
        }
        query.addSort("id", SolrQuery.ORDER.desc); // uniek veld, zodat resultaten (van dezelfde query) altijd in dezelfde volgorde staan

        try {
            final QueryResponse response = solrClient.query(query);

            List<? extends ZoekObject> zoekObjecten = response.getResults().stream().map(solrDocument -> {
                final ZoekObjectType zoekObjectType = ZoekObjectType.valueOf(String.valueOf(solrDocument.get("type")));
                return solrClient.getBinder().getBean(zoekObjectType.getZoekObjectClass(), solrDocument);
            }).collect(Collectors.toList());

            final ZoekResultaat<? extends ZoekObject> zoekResultaat = new ZoekResultaat<>(zoekObjecten, response.getResults().getNumFound());
            response.getFacetFields().forEach(facetField -> {
                final FilterVeld facetVeld = FilterVeld.fromValue(facetField.getName());
                final List<FilterResultaat> waardes = new ArrayList<>();
                facetField.getValues().stream()
                        .filter(facet -> facet.getCount() > 0)
                        .forEach(facet -> waardes.add(new FilterResultaat(facet.getName() == null ? LEEG.toString() : facet.getName(), facet.getCount())));
                zoekResultaat.addFilter(facetVeld, waardes);
            });
            return zoekResultaat;
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyAllowedZaaktypenPolicy(final SolrQuery query) {
        final Set<String> allowedZaaktypen = policyService.getAllowedZaaktypen();
        if (allowedZaaktypen != null) {
            if (allowedZaaktypen.isEmpty()) {
                query.addFilterQuery(format("%s:\"%s\"", ZAAKTYPE_OMSCHRIJVING_VELD, NON_EXISTING_ZAAKTYPE));
            } else {
                query.addFilterQuery(allowedZaaktypen.stream()
                                             .map(zaaktype -> format("%s:\"%s\"", ZAAKTYPE_OMSCHRIJVING_VELD, zaaktype))
                                             .collect(joining(" OR ")));
            }
        }
    }
}
