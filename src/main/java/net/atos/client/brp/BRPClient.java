/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.IngeschrevenPersoonHalCollectie;
import net.atos.client.brp.model.listPersonenParameters;

/**
 * Bevragen Ingeschreven Personen
 * <p>
 * API voor het bevragen van ingeschreven personen uit de basisregistratie personen (BRP), inclusief de registratie niet-ingezeten (RNI).
 * Met deze API kun je personen zoeken en actuele gegevens over personen, kinderen, partners en ouders raadplegen.
 * Gegevens die er niet zijn of niet actueel zijn krijg je niet terug.
 * Heeft een persoon bijvoorbeeld geen geldige nationaliteit, en alleen een beëindigd partnerschap, dan krijg je geen gegevens over nationaliteit en partner.
 * Zie de [Functionele documentatie](https://github.com/VNG-Realisatie/Haal-Centraal-BRP-bevragen/tree/v1.1.0/features) voor nadere toelichting.
 */

@RegisterRestClient(configKey = "BRP-API-Client")
@Produces({"application/hal+json", "application/problem+json"})
@Path("api/brp/ingeschrevenpersonen")
public interface BRPClient {

    /**
     * Vindt personen
     * <p>
     * Zoek personen met één van de onderstaande verplichte combinaties van parameters en vul ze evt. aan met parameters uit de andere combinaties.
     * Default krijg je personen terug die nog in leven zijn, tenzij je de inclusiefoverledenpersonen&#x3D;true opgeeft.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien,
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
    @GET
    IngeschrevenPersoonHalCollectie listPersonen(@BeanParam final listPersonenParameters parameters);

    /**
     * Raadpleeg een persoon
     * <p>
     * Raadpleeg een (overleden) persoon.
     * Gebruik de fields parameter als je alleen specifieke velden in het antwoord wil zien,
     * [zie functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-BRP-bevragen/blob/v1.1.0/features/fields_extensie.feature).
     */
    @GET
    @Path("{burgerservicenummer}")
    IngeschrevenPersoonHal readPersoon(@PathParam("burgerservicenummer") final String burgerservicenummer, @QueryParam("fields") final String fields);
}
