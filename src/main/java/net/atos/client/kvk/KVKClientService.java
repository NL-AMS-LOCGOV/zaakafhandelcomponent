/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.kvk;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.kvk.exception.KvKClientNoResultException;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.vestigingsprofiel.model.Vestiging;
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
    private VestigingsprofielClient vestigingsprofielClient;

    public Resultaat list(final KVKZoekenParameters parameters) {
        try {
            return zoekenClient.getResults(parameters);
        } catch (final KvKClientNoResultException exception) {
            // Nothing to report
        } catch (final RuntimeException exception) {
            LOG.warning(() -> "Error while calling list: %s".formatted(exception.getMessage()));
        }
        return createEmptyResultaat();
    }

    public CompletionStage<Resultaat> listAsync(final KVKZoekenParameters parameters) {
        return zoekenClient.getResultsAsync(parameters)
                .handle(this::handleListAsync);
    }

    public Optional<Vestiging> readVestigingsprofiel(final String vestigingsnummer) {
        return Optional.of(vestigingsprofielClient.getVestigingByVestigingsnummer(vestigingsnummer, false));
    }

    public Optional<ResultaatItem> findHoofdvestiging(final String kvkNummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setType("hoofdvestiging");
        zoekParameters.setKvkNummer(kvkNummer);
        return convertToSingleItem(list(zoekParameters));
    }

    public Optional<ResultaatItem> findVestiging(final String vestigingsnummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setVestigingsnummer(vestigingsnummer);
        return convertToSingleItem(list(zoekParameters));
    }

    public CompletionStage<Optional<ResultaatItem>> findVestigingAsync(final String vestigingsnummer) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setVestigingsnummer(vestigingsnummer);
        return listAsync(zoekParameters).thenApply(this::convertToSingleItem);
    }

    private Resultaat handleListAsync(final Resultaat resultaat, final Throwable exception) {
        if (resultaat != null) {
            return resultaat;
        } else {
            if (!(exception instanceof KvKClientNoResultException)) {
                LOG.warning(() -> "Error while calling listAsync: %s".formatted(exception.getMessage()));
            }
            return createEmptyResultaat();
        }
    }

    public Optional<ResultaatItem> findRechtspersoon(final String rsin) {
        final KVKZoekenParameters zoekParameters = new KVKZoekenParameters();
        zoekParameters.setType("rechtspersoon");
        zoekParameters.setRsin(rsin);
        return convertToSingleItem(list(zoekParameters));
    }

    private Optional<ResultaatItem> convertToSingleItem(final Resultaat resultaat) {
        return switch (resultaat.getTotaal()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(resultaat.getResultaten().get(0));
            default -> throw new IllegalStateException("Too many results: %d".formatted(resultaat.getAantal()));
        };
    }

    private Resultaat createEmptyResultaat() {
        final Resultaat resultaat = new Resultaat();
        resultaat.setAantal(0);
        resultaat.setResultaten(Collections.emptyList());
        return resultaat;
    }
}
