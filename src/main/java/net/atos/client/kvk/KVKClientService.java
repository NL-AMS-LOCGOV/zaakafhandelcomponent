/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.kvk;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.kvk.exception.KvKClientNoResultException;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;
import net.atos.client.kvk.zoeken.model.ResultaatItem;

@ApplicationScoped
public class KVKClientService {

    private static final Logger LOG = Logger.getLogger(KVKClientService.class.getName());

    @Inject
    @RestClient
    private ZoekenClient zoekenClient;

    @Inject
    @RestClient
    private BasisprofielClient basisprofielClient;

    public Resultaat zoeken(final KVKZoekenParameters parameters) {
        try {
            return zoekenClient.getResults(parameters);
        } catch (final KvKClientNoResultException e) {
            return new Resultaat().aantal(0);
        } catch (final TimeoutException | ProcessingException e) {
            LOG.severe(() -> String.format("Error while calling KVKClient provider: %s", e.getMessage()));
        }
        return null;
    }

    public ResultaatItem findHoofdvestiging(final String kvkNummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setType("hoofdvestiging");
        zoekParameters.setKvkNummer(kvkNummer);
        final Resultaat resultaat = zoeken(zoekParameters);
        if (resultaat != null) {
            return switch (resultaat.getAantal()) {
                case 0 -> null;
                case 1 -> resultaat.getResultaten().get(0);
                default -> throw new IllegalStateException("%s: %d".formatted("Too many results", resultaat.getAantal()));
            };
        }
        return null;
    }
}
