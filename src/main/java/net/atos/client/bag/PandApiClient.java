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

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

import net.atos.client.bag.model.BouwjaarFilter;
import net.atos.client.bag.model.CrsEnum;
import net.atos.client.bag.model.PandHalBasis;
import net.atos.client.bag.model.PandHalCollectie;
import net.atos.client.bag.model.StatusPandEnum;
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
@Path("/panden")
public interface PandApiClient {

    /**
     * levert een pand
     * <p>
     * Raadpleeg een actueel pand met de identificatie. Een pand is een bouwkundige, constructief zelfstandige eenheid die direct en duurzaam met de aarde is verbonden en betreedbaar en afsluitbaar is. Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien, zie [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).
     */
    @GET
    @Path("/{pandidentificatie}")
    PandHalBasis raadpleegPand(@PathParam("pandidentificatie") String pandidentificatie, @QueryParam("fields") String fields,
            @HeaderParam("Accept-Crs") CrsEnum acceptCrs);

    /**
     * vindt panden
     * <p>
     * Zoek actuele panden: 1. met de identificatie van een adresseerbaar object of 2. met de identificatie van een nummeraanduiding of 3. met een locatie (punt) of  4. binnen een geometrische contour (rechthoek) die voldoen aan de opgegeven status, geconstateerd of bouwjaar.  Gebruik de fields-parameter als je alleen specifieke velden in het antwoord wil zien, zie  [functionele specificaties fields-parameter](https://github.com/VNG-Realisatie/Haal-Centraal-common/blob/v1.3.0/features/fields.feature).
     */
    @GET
    PandHalCollectie raadpleegPanden(@QueryParam("adresseerbaarObjectIdentificatie") String adresseerbaarObjectIdentificatie,
            @QueryParam("nummeraanduidingIdentificatie") String nummeraanduidingIdentificatie, @QueryParam("locatie") List<BigDecimal> locatie,
            @QueryParam("fields") String fields, @HeaderParam("Accept-Crs") CrsEnum acceptCrs, @HeaderParam("Content-Crs") CrsEnum contentCrs,
            @QueryParam("bbox") List<BigDecimal> bbox, @QueryParam("statusPand") List<StatusPandEnum> statusPand,
            @QueryParam("geconstateerd") Boolean geconstateerd, @QueryParam("bouwjaar") BouwjaarFilter bouwjaar,
            @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("pageSize") @DefaultValue("20") Integer pageSize);
}
