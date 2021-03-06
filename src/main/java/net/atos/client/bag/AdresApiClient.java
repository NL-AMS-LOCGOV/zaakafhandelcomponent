/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * Huidige bevragingen API
 * Deze API levert actuele gegevens over adressen, adresseerbare objecten en panden. Actueel betekent in deze API `zonder eindstatus`. De bron voor deze API is de basisregistratie adressen en gebouwen (BAG).
 * <p>
 * The version of the OpenAPI document: 1.5.0
 * Contact: bag@kadaster.nl
 * <p>
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.bag;

import java.time.temporal.ChronoUnit;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.bag.model.AdresHal;
import net.atos.client.bag.model.AdresHalCollectie;
import net.atos.client.bag.model.CrsEnum;
import net.atos.client.bag.model.NummeraanduidingHalBasis;
import net.atos.client.bag.model.OpenbareRuimteHalBasis;
import net.atos.client.bag.model.WoonplaatsHal;
import net.atos.client.bag.model.ZoekResultaatHalCollectie;
import net.atos.client.bag.util.BAGClientHeadersFactory;
import net.atos.client.brp.exception.RuntimeExceptionMapper;

/**
 * Huidige bevragingen API
 *
 * <p>Deze API levert actuele gegevens over adressen, adresseerbare objecten en panden. Actueel betekent in deze API `zonder eindstatus`. De bron voor deze API is de basisregistratie adressen en gebouwen (BAG).
 */

@RegisterRestClient(configKey = "BAG-API-Client")
@RegisterClientHeaders(BAGClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(RuntimeExceptionMapper.class)
})
@Produces({"application/hal+json", "application/problem+json"})
@Timeout(unit = ChronoUnit.SECONDS, value = 5)
@Path("")
public interface AdresApiClient {

    /**
     * levert een adres
     * <p>
     * Raadpleeg een actueel adres met de nummeraanduidingidentificatie.  Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien,  zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).  Gebruik de expand-parameter als je het antwoord wil uitbreiden met (delen van) de gerelateerde resources nummeraanduiding, woonplaats, openbare ruimte, adresseerbaarobject,  zie [functionele specificaties expand-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/expand.feature).
     */
    @GET
    @Path("adressen/{nummeraanduidingidentificatie}")
    AdresHal raadpleegAdresMetNumId(@PathParam("nummeraanduidingidentificatie") String nummeraanduidingidentificatie, @QueryParam("expand") String expand,
            @QueryParam("fields") String fields);

    /**
     * vindt adressen
     * <p>
     * Vind een actueel adres met: 1. Een pandidentificatie of 2. Een adresseerbaarobjectidentificatie of 3. Een postcode, huisnummer en optioneel huisletter, huisnummertoevoeging en/of exacteMatch. 4. Een zoekterm.  Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien,  zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).  Gebruik de expand-parameter als je het antwoord wil uitbreiden met (delen van) de gerelateerde resources nummeraanduiding, woonplaats en openbare ruimte,  zie [functionele specificaties expand-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/expand.feature). Gebruik de exacteMatch parameter als je alleen resultaten wilt ontvangen die exact overeenkomen met de opgegeven zoek criteria, zie [functionele specificaties exacteMatch-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-BAG-bevragen/blob/v1.2.0/features/exacte_match.feature). Je kunt adresseerbare objecten niet expanden als je tegelijkertijd de query parameter adresseerbaarObjectIdentificatie gebruikt.
     */
    @GET
    @Path("adressen")
    AdresHalCollectie raadpleegAdressen(@QueryParam("pandIdentificatie") String pandIdentificatie,
            @QueryParam("adresseerbaarObjectIdentificatie") String adresseerbaarObjectIdentificatie,
            @QueryParam("zoekresultaatIdentificatie") String zoekresultaatIdentificatie, @QueryParam("expand") String expand,
            @QueryParam("fields") String fields, @QueryParam("page") @DefaultValue("1") Integer page,
            @QueryParam("pageSize") @DefaultValue("20") Integer pageSize, @QueryParam("postcode") String postcode, @QueryParam("huisnummer") Integer huisnummer,
            @QueryParam("huisletter") String huisletter, @QueryParam("huisnummertoevoeging") String huisnummertoevoeging,
            @QueryParam("exacteMatch") @DefaultValue("false") Boolean exacteMatch, @QueryParam("q") String q);

    /**
     * levert BAG details van een nummeraanduiding
     * <p>
     * Raadpleeg een actuele nummeraanduiding met de identificatie. Een nummeraanduiding is een postcode, huisnummer met evt een huisletter en huisnummertoevoeging die hoort bij een verblijfsobject, een standplaats of een ligplaats. Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien, zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).
     */
    @GET
    @Path("nummeraanduidingen/{nummeraanduidingidentificatie}")
    NummeraanduidingHalBasis raadpleegNummeraanduiding(@PathParam("nummeraanduidingidentificatie") String nummeraanduidingidentificatie,
            @QueryParam("fields") String fields);

    /**
     * levert BAG detals van een openbare ruimte
     * <p>
     * Raadpleeg een actuele openbare ruimte met de identificatie. Een openbare ruimte is een buitenruimte met een naam die binnen ????n woonplaats ligt, bijvoorbeeld een straat of een plein. Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien, zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).
     */
    @GET
    @Path("openbareruimten/{openbareruimteidentificatie}")
    OpenbareRuimteHalBasis raadpleegOpenbareRuimte(@PathParam("openbareruimteidentificatie") String openbareruimteidentificatie,
            @QueryParam("fields") String fields);

    /**
     * levert BAG details van een woonplaats
     * <p>
     * Raadpleeg een actuele woonplaats met de identificatie. Een woonplaats is een gedeelte van het grondgebied van de gemeente met een naam. Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien, zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature). Gebruik de expand-parameter als je het antwoord wil uitbreiden met de gerelateerde resource geometrie, zie [functionele specificaties expand-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/expand.feature).
     */
    @GET
    @Path("woonplaatsen/{woonplaatsidentificatie}")
    WoonplaatsHal raadpleegWoonplaats(@PathParam("woonplaatsidentificatie") String woonplaatsidentificatie, @QueryParam("expand") String expand,
            @QueryParam("fields") String fields, @HeaderParam("Accept-Crs") CrsEnum acceptCrs);

    /**
     * \&quot;fuzzy\&quot; zoeken van adressen
     * <p>
     * Free query zoeken van adressen met postcode, woonplaats, straatnaam, huisnummer, huisletter, huisnummertoevoeging. Delen van de adressen in het antwoord matchen exact met jouw invoer. Je vindt een adres door de zoekresultaatidentificatie uit het antwoord te gebruiken in get/adressen
     *
     * @deprecated
     */
    @Deprecated
    @GET
    @Path("adressen/zoek")
    ZoekResultaatHalCollectie zoek(@QueryParam("zoek") String zoek, @QueryParam("page") @DefaultValue("1") Integer page,
            @QueryParam("pageSize") @DefaultValue("20") Integer pageSize);
}
