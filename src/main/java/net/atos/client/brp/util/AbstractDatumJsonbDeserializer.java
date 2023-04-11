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

import net.atos.client.brp.model.AbstractDatum;
import net.atos.client.brp.model.DatumOnbekend;
import net.atos.client.brp.model.JaarDatum;
import net.atos.client.brp.model.JaarMaandDatum;
import net.atos.client.brp.model.VolledigeDatum;

public class AbstractDatumJsonbDeserializer implements JsonbDeserializer<AbstractDatum> {

    private static final Jsonb JSONB =
            JsonbBuilder.create(
                    new JsonbConfig().withPropertyVisibilityStrategy(new FieldPropertyVisibilityStrategy()));

    @Override
    public AbstractDatum deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        final JsonObject jsonObject = parser.getObject();
        final String type = jsonObject.getString("type");
        return switch (type) {
            case "Datum" -> JSONB.fromJson(jsonObject.toString(), VolledigeDatum.class);
            case "DatumOnbekend" -> JSONB.fromJson(jsonObject.toString(), DatumOnbekend.class);
            case "JaarDatum" -> JSONB.fromJson(jsonObject.toString(), JaarDatum.class);
            case "JaarMaandDatum" -> JSONB.fromJson(jsonObject.toString(), JaarMaandDatum.class);
            default -> throw new RuntimeException("Type '%s' wordt niet ondersteund".formatted(type));
        };
    }
}
