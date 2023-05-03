/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.client.zgw.shared.util.JsonbUtil.JSONB;

import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import net.atos.zac.app.bag.model.BAGObjectType;
import net.atos.zac.app.bag.model.RESTAdres;
import net.atos.zac.app.bag.model.RESTAdresseerbaarObject;
import net.atos.zac.app.bag.model.RESTBAGObject;
import net.atos.zac.app.bag.model.RESTNummeraanduiding;
import net.atos.zac.app.bag.model.RESTOpenbareRuimte;
import net.atos.zac.app.bag.model.RESTPand;
import net.atos.zac.app.bag.model.RESTWoonplaats;

public class RESTBAGObjectJsonbDeserializer implements JsonbDeserializer<RESTBAGObject> {

    @Override
    public RESTBAGObject deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        final JsonObject jsonObject = parser.getObject();
        final BAGObjectType type = BAGObjectType.valueOf(jsonObject.getJsonString("bagObjectType").getString());

        return switch (type) {
            case ADRES -> JSONB.fromJson(jsonObject.toString(), RESTAdres.class);
            case NUMMERAANDUIDING -> JSONB.fromJson(jsonObject.toString(), RESTNummeraanduiding.class);
            case WOONPLAATS -> JSONB.fromJson(jsonObject.toString(), RESTWoonplaats.class);
            case PAND -> JSONB.fromJson(jsonObject.toString(), RESTPand.class);
            case OPENBARE_RUIMTE -> JSONB.fromJson(jsonObject.toString(), RESTOpenbareRuimte.class);
            case ADRESSEERBAAR_OBJECT -> JSONB.fromJson(jsonObject.toString(), RESTAdresseerbaarObject.class);
        };
    }
}
