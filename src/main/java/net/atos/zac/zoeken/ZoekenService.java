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
import net.atos.zac.zoeken.model.ZoekZaakParameters;
import net.atos.zac.zoeken.model.ZoekZaakResultaat;

@ApplicationScoped
public class ZoekenService {

    private static final String SOLR_CORE = "zac";

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    private SolrClient solrClient;

    public ZoekenService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/%s", solrUrl, SOLR_CORE)).build();
    }

    public List<ZoekZaakResultaat> zoekZaak(final ZoekZaakParameters zoekZaakParameters) {
        final SolrQuery query = new SolrQuery(zoekZaakParameters.getVrijeTekst());
        query.setShowDebugInfo(true); // ToDo: Remove

        try {
            final QueryResponse response = solrClient.query(query);
            return response.getBeans(ZoekZaakResultaat.class);
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void indexeerZaak(final UUID zaakUUID) {
        final ZoekZaakResultaat zoekZaakResultaat = readZoekZaakResultaat(zaakUUID);
        try {
            solrClient.addBean(zoekZaakResultaat);
            solrClient.commit();
        } catch (final IOException | SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    private ZoekZaakResultaat readZoekZaakResultaat(final UUID zaakUUID) {
        final ZoekZaakResultaat zoekZaakResultaat = new ZoekZaakResultaat();
        zoekZaakResultaat.setUuid(zaakUUID);
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        zoekZaakResultaat.setIdentificatie(zaak.getIdentificatie());
        zoekZaakResultaat.setOmschrijving(zaak.getOmschrijving());
        zoekZaakResultaat.setToelichting(zaak.getToelichting());
        zoekZaakResultaat.setLocatie(convertToLocatie(zaak.getZaakgeometrie()));
        zoekZaakResultaat.setZaaktype(ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving());
        zoekZaakResultaat.setStatus(ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype()).getOmschrijving());
        return zoekZaakResultaat;
    }

    private String convertToLocatie(final Geometry zaakgeometrie) {
        // ToDo
        return null;
    }
}
