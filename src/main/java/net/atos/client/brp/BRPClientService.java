/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp;

import net.atos.client.brp.exception.PersoonNotFoundException;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.IngeschrevenPersoonHalCollectie;
import net.atos.client.brp.model.ListPersonenParameters;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class BRPClientService {

    private static final Logger LOG = Logger.getLogger(BRPClientService.class.getName());

    @Inject
    @RestClient
    private IngeschrevenpersonenClient ingeschrevenpersonenClient;

    /**
     * Vindt personen
     * <p>
     * Zoek personen met één van de onderstaande verplichte combinaties van parameters en vul ze evt. aan met parameters uit de andere combinaties.
     * Default krijg je personen terug die nog in leven zijn, tenzij je de inclusiefoverledenpersonen&#x3D;true opgeeft.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien.
     * [zie functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-BRP-bevragen/blob/v1.1.0/features/fields_extensie.feature)
     * <p>
     * 1. Persoon
     * - geboorte__datum
     * -  naam__geslachtsnaam (minimaal 2 karakters, [wildcard toegestaan](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.2.0/features/wildcard.feature)
     * <p>
     * 2. Persoon
     * - verblijfplaats__gemeenteVanInschrijving
     * - naam__geslachtsnaam (minimaal 2 karakters, [wildcard toegestaan](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.2.0/features/wildcard.feature)
     * <p>
     * 3. Persoon
     * - burgerservicenummer
     * <p>
     * 4. Postcode
     * - verblijfplaats__postcode
     * - verblijfplaats__huisnummer
     * <p>
     * 5. Straat
     * - verblijfplaats__straat (minimaal 2 karakters, [wildcard toegestaan](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.2.0/features/wildcard.feature) )
     * - verblijfplaats__gemeenteVanInschrijving
     * - verblijfplaats__huisnummer
     * <p>
     * 6. Adres
     * - verblijfplaats__nummeraanduidingIdentificatie
     */
    public IngeschrevenPersoonHalCollectie listPersonen(final ListPersonenParameters parameters) {
        return ingeschrevenpersonenClient.listPersonen(parameters);
    }

    /**
     * Vindt een persoon
     * <p>
     * Raadpleeg een (overleden) persoon.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien,
     * [zie functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-BRP-bevragen/blob/v1.1.0/features/fields_extensie.feature).
     */
    public Optional<IngeschrevenPersoonHal> findPersoon(final String burgerservicenummer, final String fields) {
        try {
            return Optional.of(ingeschrevenpersonenClient.readPersoon(burgerservicenummer, fields));
        } catch (final PersoonNotFoundException e) {
            // Nothing to report
        } catch (final TimeoutException | ProcessingException exception) {
            LOG.severe(() -> String.format("Error while calling BRP bevragen provider: %s", exception.getMessage()));
        }
        return Optional.empty();
    }

    public CompletionStage<Optional<IngeschrevenPersoonHal>> findPersoonAsync(final String burgerservicenummer,
            final String fields) {
        return ingeschrevenpersonenClient.readPersoonAsync(burgerservicenummer, fields)
                .handle((persoon, exception) -> convert(persoon, exception));
    }

    private Optional<IngeschrevenPersoonHal> convert(final IngeschrevenPersoonHal persoon, final Throwable exception) {
        if (persoon != null) {
            return Optional.of(persoon);
        } else {
            if (!(exception instanceof PersoonNotFoundException)) {
                LOG.severe(
                        () -> String.format("Error while calling BRP bevragen provider: %s", exception.getMessage()));
            }
            return Optional.empty();
        }
    }
}
