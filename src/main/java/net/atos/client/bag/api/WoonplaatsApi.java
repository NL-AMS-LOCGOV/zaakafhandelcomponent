/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * IMBAG API - van de LVBAG
 * Dit is de [BAG API](https://zakelijk.kadaster.nl/-/bag-api) Individuele Bevragingen van de Landelijke Voorziening Basisregistratie Adressen en Gebouwen (LVBAG).  Meer informatie over de Basisregistratie Adressen en Gebouwen is te vinden op de website van het [Ministerie van Binnenlandse Zaken en Koninkrijksrelaties](https://www.geobasisregistraties.nl/basisregistraties/adressen-en-gebouwen) en [Kadaster](https://zakelijk.kadaster.nl/bag).  De BAG API levert informatie conform de [BAG Catalogus 2018](https://www.geobasisregistraties.nl/documenten/publicatie/2018/03/12/catalogus-2018) en het informatiemodel IMBAG 2.0. De API specificatie volgt de [Nederlandse API-Strategie](https://docs.geostandaarden.nl/api/API-Strategie) specificatie versie van 20200204 en is opgesteld in [OpenAPI Specificatie](https://www.forumstandaardisatie.nl/standaard/openapi-specification) (OAS) v3.  Het standaard mediatype HAL (`application/hal+json`) wordt gebruikt. Dit is een mediatype voor het weergeven van resources en hun relaties via hyperlinks.  Deze API is vooral gericht op individuele bevragingen (op basis van de identificerende gegevens van een object). Om gebruik te kunnen maken van de BAG API is een API key nodig, deze kan verkregen worden door het [aanvraagformulier](https://formulieren.kadaster.nl/aanvraag_bag_api_individuele_bevragingen_productie) in te vullen.  Voor vragen, neem contact op met de LVBAG beheerder o.v.v. BAG API 2.0. We zijn aan het kijken naar een geschikt medium hiervoor, mede ook om de API iteratief te kunnen opstellen of doorontwikkelen samen met de community. Als de API iets (nog) niet kan, wat u wel graag wilt, neem dan contact op.
 * <p>
 * The version of the OpenAPI document: 2.6.0
 * <p>
 * <p>
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.bag.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.bag.model.PointGeoJSON;
import net.atos.client.bag.model.WoonplaatsIOHal;
import net.atos.client.bag.model.WoonplaatsIOHalCollection;
import net.atos.client.bag.model.WoonplaatsIOLvcHalCollection;
import net.atos.client.bag.util.BAGClientHeadersFactory;
import net.atos.client.brp.exception.RuntimeExceptionMapper;

/**
 * IMBAG API - van de LVBAG
 *
 * <p>Dit is de [BAG API](https://zakelijk.kadaster.nl/-/bag-api) Individuele Bevragingen van de Landelijke Voorziening Basisregistratie Adressen en Gebouwen (LVBAG).  Meer informatie over de Basisregistratie Adressen en Gebouwen is te vinden op de website van het [Ministerie van Binnenlandse Zaken en Koninkrijksrelaties](https://www.geobasisregistraties.nl/basisregistraties/adressen-en-gebouwen) en [Kadaster](https://zakelijk.kadaster.nl/bag).  De BAG API levert informatie conform de [BAG Catalogus 2018](https://www.geobasisregistraties.nl/documenten/publicatie/2018/03/12/catalogus-2018) en het informatiemodel IMBAG 2.0. De API specificatie volgt de [Nederlandse API-Strategie](https://docs.geostandaarden.nl/api/API-Strategie) specificatie versie van 20200204 en is opgesteld in [OpenAPI Specificatie](https://www.forumstandaardisatie.nl/standaard/openapi-specification) (OAS) v3.  Het standaard mediatype HAL (`application/hal+json`) wordt gebruikt. Dit is een mediatype voor het weergeven van resources en hun relaties via hyperlinks.  Deze API is vooral gericht op individuele bevragingen (op basis van de identificerende gegevens van een object). Om gebruik te kunnen maken van de BAG API is een API key nodig, deze kan verkregen worden door het [aanvraagformulier](https://formulieren.kadaster.nl/aanvraag_bag_api_individuele_bevragingen_productie) in te vullen.  Voor vragen, neem contact op met de LVBAG beheerder o.v.v. BAG API 2.0. We zijn aan het kijken naar een geschikt medium hiervoor, mede ook om de API iteratief te kunnen opstellen of doorontwikkelen samen met de community. Als de API iets (nog) niet kan, wat u wel graag wilt, neem dan contact op.
 */

@RegisterRestClient(configKey = "BAG-API-Client")
@RegisterClientHeaders(BAGClientHeadersFactory.class)
@RegisterProviders({@RegisterProvider(RuntimeExceptionMapper.class)})
@Timeout(unit = ChronoUnit.SECONDS, value = 5)
@Path("/woonplaatsen")
public interface WoonplaatsApi {

    /**
     * bevragen van een woonplaats met een geometrische locatie.
     * <p>
     * Bevragen/raadplegen van een voorkomen van een Woonplaats met een geometrische locatie. Parameter huidig kan worden toegepast, zie [functionele specificatie huidig](https://github.com/lvbag/BAG-API/blob/master/Features/huidig.feature). De geldigOp en beschikbaarOp parameters kunnen gebruikt worden voor  tijdreis vragen, zie  [functionele specificatie tijdreizen](https://github.com/lvbag/BAG-API/blob/master/Features/tijdreizen.feature).  Als expand&#x3D;bronhouders, geometrie of true dan worden de gevraagde of alle gerelateerde objecten als geneste resource geleverd, zie [functionele specificatie expand](https://github.com/lvbag/BAG-API/blob/master/Features/expand.feature).
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/hal+json", "application/problem+json"})
    public WoonplaatsIOHalCollection woonplaatsGeometrie(PointGeoJSON pointGeoJSON,
            @QueryParam("geldigOp") LocalDate geldigOp, @QueryParam("beschikbaarOp") OffsetDateTime beschikbaarOp,
            @QueryParam("huidig") @DefaultValue("false") Boolean huidig, @QueryParam("expand") String expand,
            @HeaderParam("Content-Crs") String contentCrs,
            @HeaderParam("Accept-Crs") String acceptCrs) throws ProcessingException;

    /**
     * bevragen van een woonplaats met de identificatie van een woonplaats.
     * <p>
     * Bevragen/raadplegen van een voorkomen van een Woonplaats met de identificatie van de woonplaats.  Parameter huidig kan worden toegepast, zie [functionele specificatie huidig](https://github.com/lvbag/BAG-API/blob/master/Features/huidig.feature). De geldigOp en beschikbaarOp parameters kunnen gebruikt worden voor  tijdreis vragen, zie  [functionele specificatie tijdreizen](https://github.com/lvbag/BAG-API/blob/master/Features/tijdreizen.feature).  Als expand&#x3D;bronhouders, geometrie of true dan worden de gevraagde of alle gerelateerde objecten als geneste resource geleverd, zie [functionele specificatie expand](https://github.com/lvbag/BAG-API/blob/master/Features/expand.feature).
     */
    @GET
    @Path("/{identificatie}")
    @Produces({"application/hal+json", "application/problem+json"})
    public WoonplaatsIOHal woonplaatsIdentificatie(@PathParam("identificatie") String identificatie,
            @QueryParam("geldigOp") LocalDate geldigOp, @QueryParam("beschikbaarOp") OffsetDateTime beschikbaarOp,
            @QueryParam("expand") String expand, @HeaderParam("Accept-Crs") String acceptCrs,
            @QueryParam("huidig") @DefaultValue("false") Boolean huidig) throws ProcessingException;

    /**
     * bevragen van een voorkomen van een woonplaats met de identificatie van een woonplaats en de identificatie van een voorkomen, bestaande uit een versie en een timestamp van het tijdstip van registratie in de LV BAG.
     * <p>
     * Bevragen/raadplegen van een voorkomen van een Woonplaats met de identificatie van een woonplaats en de identificatie van een voorkomen, bestaande uit een versie en een timestamp van het tijdstip van registratie in de LV BAG. Als expand&#x3D;bronhouders, geometrie of true dan worden de gevraagde of alle gerelateerde objecten als geneste resource geleverd, zie [functionele specificatie expand](https://github.com/lvbag/BAG-API/blob/master/Features/expand.feature).
     */
    @GET
    @Path("/{identificatie}/{versie}/{timestampRegistratieLv}")
    @Produces({"application/hal+json", "application/problem+json"})
    public WoonplaatsIOHal woonplaatsIdentificatieVoorkomen(@PathParam("identificatie") String identificatie,
            @PathParam("versie") Integer versie, @PathParam("timestampRegistratieLv") String timestampRegistratieLv,
            @QueryParam("expand") String expand,
            @HeaderParam("Accept-Crs") String acceptCrs) throws ProcessingException;

    /**
     * bevragen van de levenscyclus van een woonplaats met de identificatie van een woonplaats.
     * <p>
     * Bevragen/raadplegen van de levenscyclus van een Woonplaats met de identificatie van de woonplaats. Als expand&#x3D;bronhouders, geometrie of true dan worden de gevraagde of alle gerelateerde objecten als geneste resource geleverd, zie [functionele specificatie expand](https://github.com/lvbag/BAG-API/blob/master/Features/expand.feature).
     */
    @GET
    @Path("/{identificatie}/lvc")
    @Produces({"application/hal+json", "application/problem+json"})
    public WoonplaatsIOLvcHalCollection woonplaatsLvcIdentificatie(@PathParam("identificatie") String identificatie,
            @QueryParam("geheleLvc") @DefaultValue("false") Boolean geheleLvc, @QueryParam("expand") String expand,
            @HeaderParam("Accept-Crs") String acceptCrs) throws ProcessingException;

    /**
     * Zoeken van één of meer woonplaatsen met een woonplaatsnaam, geometrische locatie of binnen een bounding box.
     * <p>
     * Zoeken van actuele woonplaatsen:  1. met een woonplaatsnaam.  2. met een geometrische locatie.  3. binnen een geometrische contour (rechthoek).   Parameter huidig kan worden toegepast, zie [functionele specificatie huidig](https://github.com/lvbag/BAG-API/blob/master/Features/huidig.feature).  De geldigOp en beschikbaarOp parameters kunnen gebruikt worden voor  tijdreis vragen, zie  [functionele specificatie tijdreizen](https://github.com/lvbag/BAG-API/blob/master/Features/tijdreizen.feature).   Als expand&#x3D;bronhouders, geometrie of true dan worden de gevraagde of alle gerelateerde objecten als geneste resource geleverd, zie [functionele specificatie expand](https://github.com/lvbag/BAG-API/blob/master/Features/expand.feature).  Voor paginering, zie: [functionele specificatie paginering](https://github.com/lvbag/BAG-API/blob/master/Features/paginering.feature).
     */
    @GET
    @Produces({"application/hal+json", "application/problem+json"})
    public WoonplaatsIOHalCollection zoekWoonplaatsen(@QueryParam("naam") String naam,
            @QueryParam("geldigOp") LocalDate geldigOp, @QueryParam("beschikbaarOp") OffsetDateTime beschikbaarOp,
            @QueryParam("huidig") @DefaultValue("false") Boolean huidig, @QueryParam("expand") String expand,
            @HeaderParam("Accept-Crs") String acceptCrs, @HeaderParam("Content-Crs") String contentCrs,
            @QueryParam("page") @DefaultValue("1") Integer page,
            @QueryParam("pageSize") @DefaultValue("20") Integer pageSize, @QueryParam("point") PointGeoJSON point,
            @QueryParam("bbox") List<BigDecimal> bbox) throws ProcessingException;
}