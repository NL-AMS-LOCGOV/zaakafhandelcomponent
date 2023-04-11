/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp;

import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.RAADPLEEG_MET_BURGERSERVICENUMMER;
import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.ZOEK_MET_GESLACHTSNAAM_EN_GEBOORTEDATUM;
import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.ZOEK_MET_NAAM_EN_GEMEENTE_VAN_INSCHRIJVING;
import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.ZOEK_MET_NUMMERAANDUIDING_IDENTIFICATIE;
import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.ZOEK_MET_POSTCODE_EN_HUISNUMMER;
import static net.atos.client.brp.util.PersonenQueryResponseJsonbDeserializer.ZOEK_MET_STRAAT_HUISNUMMER_EN_GEMEENTE_VAN_INSCHRIJVING;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.brp.model.PersonenQuery;
import net.atos.client.brp.model.PersonenQueryResponse;
import net.atos.client.brp.model.Persoon;
import net.atos.client.brp.model.RaadpleegMetBurgerservicenummer;
import net.atos.client.brp.model.RaadpleegMetBurgerservicenummerResponse;
import net.atos.client.brp.model.ZoekMetGeslachtsnaamEnGeboortedatum;
import net.atos.client.brp.model.ZoekMetNaamEnGemeenteVanInschrijving;
import net.atos.client.brp.model.ZoekMetNummeraanduidingIdentificatie;
import net.atos.client.brp.model.ZoekMetPostcodeEnHuisnummer;
import net.atos.client.brp.model.ZoekMetStraatHuisnummerEnGemeenteVanInschrijving;

@ApplicationScoped
public class BRPClientService {

    private static final Logger LOG = Logger.getLogger(BRPClientService.class.getName());

    private static final String BURGERSERVICENUMMER = "burgerservicenummer";

    private static final String GESLACHT = "geslacht";

    private static final String NAAM = "naam";

    private static final String GEBOORTE = "geboorte";

    private static final String VERBLIJFPLAATS = "verblijfplaats";

    private static final String ADRESSERING = "adressering";

    private static final List<String> FIELDS_PERSOON =
            List.of(BURGERSERVICENUMMER, GESLACHT, NAAM, GEBOORTE, VERBLIJFPLAATS);

    private static final List<String> FIELDS_PERSOON_BEPERKT =
            List.of(BURGERSERVICENUMMER, GESLACHT, NAAM, GEBOORTE, ADRESSERING);

    @Inject
    @RestClient
    private PersonenApi personenApi;

    public PersonenQueryResponse queryPersonen(final PersonenQuery personenQuery) {
        complementQuery(personenQuery);
        return personenApi.personen(personenQuery);
    }

    /**
     * Vindt een persoon
     * <p>
     * Raadpleeg een (overleden) persoon.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien,
     */
    public Optional<Persoon> findPersoon(final String burgerservicenummer) {
        try {
            final var response = (RaadpleegMetBurgerservicenummerResponse) personenApi.personen(
                    createRaadpleegMetBurgerservicenummerQuery(burgerservicenummer));
            if (!CollectionUtils.isEmpty(response.getPersonen())) {
                return Optional.of(response.getPersonen().get(0));
            } else {
                return Optional.empty();
            }
        } catch (final RuntimeException exception) {
            LOG.warning(() -> "Error while calling findPersoon: %s".formatted(exception.getMessage()));
            return Optional.empty();
        }
    }

    /**
     * Vindt een persoon asynchroon
     * <p>
     * Raadpleeg een (overleden) persoon.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien,
     */
    public CompletionStage<Optional<Persoon>> findPersoonAsync(final String burgerservicenummer) {
        return personenApi.personenAsync(createRaadpleegMetBurgerservicenummerQuery(burgerservicenummer))
                .handle((response, exception) -> handleFindPersoonAsync(
                        (RaadpleegMetBurgerservicenummerResponse) response, exception));
    }

    private Optional<Persoon> handleFindPersoonAsync(final RaadpleegMetBurgerservicenummerResponse response,
            final Throwable exception) {
        if (!CollectionUtils.isEmpty(response.getPersonen())) {
            return Optional.of(response.getPersonen().get(0));
        } else {
            LOG.warning(() -> "Error while calling findPersoonAsync: %s".formatted(exception.getMessage()));
            return Optional.empty();
        }
    }

    private static RaadpleegMetBurgerservicenummer createRaadpleegMetBurgerservicenummerQuery(
            final String burgerservicenummer) {
        final var query = new RaadpleegMetBurgerservicenummer();
        complementQuery(query);
        query.addBurgerservicenummerItem(burgerservicenummer);
        return query;
    }

    private static void complementQuery(final PersonenQuery personenQuery) {
        switch (personenQuery) {
            case RaadpleegMetBurgerservicenummer query -> {
                query.setType(RAADPLEEG_MET_BURGERSERVICENUMMER);
                query.setFields(FIELDS_PERSOON);
            }
            case ZoekMetGeslachtsnaamEnGeboortedatum query -> {
                query.setType(ZOEK_MET_GESLACHTSNAAM_EN_GEBOORTEDATUM);
                query.setFields(FIELDS_PERSOON_BEPERKT);
            }
            case ZoekMetNaamEnGemeenteVanInschrijving query -> {
                query.setType(ZOEK_MET_NAAM_EN_GEMEENTE_VAN_INSCHRIJVING);
                query.setFields(FIELDS_PERSOON_BEPERKT);
            }
            case ZoekMetNummeraanduidingIdentificatie query -> {
                query.setType(ZOEK_MET_NUMMERAANDUIDING_IDENTIFICATIE);
                query.setFields(FIELDS_PERSOON_BEPERKT);
            }
            case ZoekMetPostcodeEnHuisnummer query -> {
                query.setType(ZOEK_MET_POSTCODE_EN_HUISNUMMER);
                query.setFields(FIELDS_PERSOON_BEPERKT);
            }
            case ZoekMetStraatHuisnummerEnGemeenteVanInschrijving query -> {
                query.setType(ZOEK_MET_STRAAT_HUISNUMMER_EN_GEMEENTE_VAN_INSCHRIJVING);
                query.setFields(FIELDS_PERSOON_BEPERKT);
            }
            case PersonenQuery query -> throw new IllegalStateException(
                    "Must use one of the subclasses of '%s'".formatted(PersonenQuery.class.getSimpleName()));
        }
    }
}
