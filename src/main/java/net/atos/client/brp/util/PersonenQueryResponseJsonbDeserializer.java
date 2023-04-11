/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import net.atos.client.brp.model.PersonenQueryResponse;
import net.atos.client.brp.model.RaadpleegMetBurgerservicenummerResponse;
import net.atos.client.brp.model.ZoekMetGeslachtsnaamEnGeboortedatumResponse;
import net.atos.client.brp.model.ZoekMetNaamEnGemeenteVanInschrijvingResponse;
import net.atos.client.brp.model.ZoekMetNummeraanduidingIdentificatieResponse;
import net.atos.client.brp.model.ZoekMetPostcodeEnHuisnummerResponse;
import net.atos.client.brp.model.ZoekMetStraatHuisnummerEnGemeenteVanInschrijvingResponse;

public class PersonenQueryResponseJsonbDeserializer implements JsonbDeserializer<PersonenQueryResponse> {

    public static final String RAADPLEEG_MET_BURGERSERVICENUMMER = "RaadpleegMetBurgerservicenummer";

    public static final String ZOEK_MET_GESLACHTSNAAM_EN_GEBOORTEDATUM = "ZoekMetGeslachtsnaamEnGeboortedatum";

    public static final String ZOEK_MET_NAAM_EN_GEMEENTE_VAN_INSCHRIJVING = "ZoekMetNaamEnGemeenteVanInschrijving";

    public static final String ZOEK_MET_NUMMERAANDUIDING_IDENTIFICATIE = "ZoekMetNummeraanduidingIdentificatie";

    public static final String ZOEK_MET_POSTCODE_EN_HUISNUMMER = "ZoekMetPostcodeEnHuisnummer";

    public static final String ZOEK_MET_STRAAT_HUISNUMMER_EN_GEMEENTE_VAN_INSCHRIJVING = "ZoekMetStraatHuisnummerEnGemeenteVanInschrijving";

    private static final Jsonb JSONB =
            JsonbBuilder.create(new JsonbConfig()
                                        .withPropertyVisibilityStrategy(new FieldPropertyVisibilityStrategy())
                                        .withDeserializers(new AbstractDatumJsonbDeserializer(),
                                                           new AbstractVerblijfplaatsJsonbDeserializer()));

    @Override
    public PersonenQueryResponse deserialize(final JsonParser parser, final DeserializationContext ctx,
            final Type rtType) {
        final JsonObject jsonObject = parser.getObject();
        final String type = jsonObject.getString("type");
        return switch (type) {
            case RAADPLEEG_MET_BURGERSERVICENUMMER ->
                    JSONB.fromJson(jsonObject.toString(), RaadpleegMetBurgerservicenummerResponse.class);
            case ZOEK_MET_GESLACHTSNAAM_EN_GEBOORTEDATUM ->
                    JSONB.fromJson(jsonObject.toString(), ZoekMetGeslachtsnaamEnGeboortedatumResponse.class);
            case ZOEK_MET_NAAM_EN_GEMEENTE_VAN_INSCHRIJVING ->
                    JSONB.fromJson(jsonObject.toString(), ZoekMetNaamEnGemeenteVanInschrijvingResponse.class);
            case ZOEK_MET_NUMMERAANDUIDING_IDENTIFICATIE ->
                    JSONB.fromJson(jsonObject.toString(), ZoekMetNummeraanduidingIdentificatieResponse.class);
            case ZOEK_MET_POSTCODE_EN_HUISNUMMER ->
                    JSONB.fromJson(jsonObject.toString(), ZoekMetPostcodeEnHuisnummerResponse.class);
            case ZOEK_MET_STRAAT_HUISNUMMER_EN_GEMEENTE_VAN_INSCHRIJVING -> JSONB.fromJson(jsonObject.toString(),
                                                                                           ZoekMetStraatHuisnummerEnGemeenteVanInschrijvingResponse.class);
            default -> throw new RuntimeException("Type '%s' wordt niet ondersteund".formatted(type));
        };
    }
}
