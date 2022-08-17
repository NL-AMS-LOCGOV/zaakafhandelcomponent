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

    public Resultaat find(final KVKZoekenParameters parameters) {
        try {
            return zoekenClient.getResults(parameters);
        } catch (final KvKClientNoResultException e) {
            return new Resultaat().totaal(0);
        } catch (final TimeoutException | ProcessingException e) {
            LOG.severe(() -> String.format("Error while calling KVKClient provider: %s", e.getMessage()));
            return new Resultaat().totaal(0);
        }
    }

    public ResultaatItem findHoofdvestiging(final String kvkNummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setType("hoofdvestiging");
        zoekParameters.setKvkNummer(kvkNummer);
        return findSingleItem(zoekParameters);
    }

    public ResultaatItem findVestiging(final String vestigingsnummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setVestigingsnummer(vestigingsnummer);
        return findSingleItem(zoekParameters);
    }

    public ResultaatItem findRechtspersoon(final String rsin) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setType("rechtspersoon");
        zoekParameters.setRsin(rsin);
        return findSingleItem(zoekParameters);
    }

    private ResultaatItem findSingleItem(final KVKZoekenParameters parameters) {
        final Resultaat resultaat = find(parameters);
        return switch (resultaat.getTotaal()) {
            case 0 -> null;
            case 1 -> resultaat.getResultaten().get(0);
            default -> throw new IllegalStateException("%s: %d".formatted("Too many results", resultaat.getAantal()));
        };
    }
}
