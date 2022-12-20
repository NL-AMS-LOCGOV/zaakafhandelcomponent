/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.klanten;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.klanten.model.Klant;
import net.atos.client.klanten.model.KlantList200Response;
import net.atos.client.klanten.model.KlantListParameters;
import net.atos.zac.configuratie.ConfiguratieService;

@ApplicationScoped
public class KlantenClientService {

    @Inject
    @RestClient
    private KlantenClient klantenClient;

    public CompletionStage<Optional<Klant>> findPersoonAsync(final String bsn) {
        return klantenClient.klantListAsync(createFindPersoonListParameters(bsn))
                .thenApply(this::convertToSingleItem);
    }

    public CompletionStage<Optional<Klant>> findVestigingAsync(final String vestigingsnummer) {
        return klantenClient.klantListAsync(createFindVestigingListParameters(vestigingsnummer))
                .thenApply(this::convertToSingleItem);
    }

    private KlantListParameters createFindPersoonListParameters(final String bsn) {
        final KlantListParameters klantListParameters = createKlantListParameters();
        klantListParameters.setSubjectNatuurlijkPersoonInpBsn(bsn);
        return klantListParameters;
    }

    private KlantListParameters createFindVestigingListParameters(final String vestigingsnummer) {
        final KlantListParameters klantListParameters = createKlantListParameters();
        klantListParameters.setSubjectVestigingVestigingsNummer(vestigingsnummer);
        return klantListParameters;
    }

    private KlantListParameters createKlantListParameters() {
        final KlantListParameters klantListParameters = new KlantListParameters();
        klantListParameters.setBronorganisatie(ConfiguratieService.BRON_ORGANISATIE);
        return klantListParameters;
    }

    private Optional<Klant> convertToSingleItem(final KlantList200Response response) {
        return switch (response.getResults().size()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(response.getResults().get(0));
            default -> throw new IllegalStateException("Too many results: %d".formatted(response.getResults().size()));
        };
    }
}
