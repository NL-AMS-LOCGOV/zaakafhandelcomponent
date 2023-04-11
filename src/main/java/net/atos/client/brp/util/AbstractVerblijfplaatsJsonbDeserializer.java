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

import net.atos.client.brp.model.AbstractVerblijfplaats;
import net.atos.client.brp.model.Adres;
import net.atos.client.brp.model.Locatie;
import net.atos.client.brp.model.VerblijfplaatsBuitenland;
import net.atos.client.brp.model.VerblijfplaatsOnbekend;

public class AbstractVerblijfplaatsJsonbDeserializer implements JsonbDeserializer<AbstractVerblijfplaats> {

    private static final Jsonb JSONB =
            JsonbBuilder.create(
                    new JsonbConfig().withPropertyVisibilityStrategy(new FieldPropertyVisibilityStrategy()));

    @Override
    public AbstractVerblijfplaats deserialize(final JsonParser parser, final DeserializationContext ctx,
            final Type rtType) {
        final JsonObject jsonObject = parser.getObject();
        final String type = jsonObject.getString("type");
        return switch (type) {
            case "VerblijfplaatsBuitenland" -> JSONB.fromJson(jsonObject.toString(), VerblijfplaatsBuitenland.class);
            case "Adres" -> JSONB.fromJson(jsonObject.toString(), Adres.class);
            case "VerblijfplaatsOnbekend" -> JSONB.fromJson(jsonObject.toString(), VerblijfplaatsOnbekend.class);
            case "Locatie" -> JSONB.fromJson(jsonObject.toString(), Locatie.class);
            default -> throw new RuntimeException("Type '%s' wordt niet ondersteund".formatted(type));
        };
    }
}
