/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Geometry;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.zoeken.converter.ZaakZoekItemConverter;
import net.atos.zac.zoeken.model.ZaakZoekParameters;
import net.atos.zac.zoeken.model.ZaakZoekItem;
import net.atos.zac.zoeken.model.ZoekResultaat;

@ApplicationScoped
public class ZoekenService {

    private static final String SOLR_CORE = "zac";

    @Inject
    private ZaakZoekItemConverter zaakZoekItemConverter;

    private SolrClient solrClient;

    public ZoekenService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public ZoekResultaat<ZaakZoekItem> zoekZaak(final ZaakZoekParameters zoekZaakParameters) {
        final SolrQuery query = new SolrQuery(zoekZaakParameters.getTekst());

        try {
            final QueryResponse response = solrClient.query(query);
            return new ZoekResultaat<>(response.getBeans(ZaakZoekItem.class), response.getResults().getNumFound());

        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void addZaak(final UUID zaakUUID) {
        final ZaakZoekItem zaak = zaakZoekItemConverter.convert(zaakUUID);
        try {
            solrClient.addBean(zaak);
            solrClient.commit();
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeZaak(final UUID zaakUUID) {
        try {
            solrClient.deleteById(zaakUUID.toString());
            solrClient.commit();
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }
}
