/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.klanten;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.klanten.model.KlantListParameters;
import net.atos.client.klanten.model.generated.Klant;

@ApplicationScoped
public class KlantenClientService {

    @Inject
    @RestClient
    private KlantenClient klantenClient;

    public Optional<Klant> findKlant(final String bsn) {
        final var parameters = new KlantListParameters();
        parameters.setSubjectNatuurlijkPersoonInpBsn(bsn);
        final var response = klantenClient.klantList(parameters);
        if (response.getResults().isEmpty()) {
            return Optional.empty();
        } else if (response.getResults().size() == 1) {
            return Optional.of(response.getResults().get(0));
        } else {
            throw new RuntimeException("Meerdere klanten gevonden met BSN: %s ".formatted(bsn));
        }
    }
}
