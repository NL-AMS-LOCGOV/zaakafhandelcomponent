/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.util;

import static net.atos.client.zgw.shared.util.JsonbUtil.JSONB;

import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectOpenbareRuimte;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectProductaanvraag;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;

public class ZaakObjectJsonbDeserializer implements JsonbDeserializer<Zaakobject> {

    @Override
    public Zaakobject deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        final JsonObject jsonObject = parser.getObject();
        final Objecttype objecttype = Objecttype.fromValue(jsonObject.getString("objectType"));
        final String objecttypeOverige = jsonObject.getString("objectTypeOverige");
        switch (objecttype) {
            case ADRES:
                return JSONB.fromJson(jsonObject.toString(), ZaakobjectAdres.class);
            case PAND:
                return JSONB.fromJson(jsonObject.toString(), ZaakobjectPand.class);
            case OPENBARE_RUIMTE:
                return JSONB.fromJson(jsonObject.toString(), ZaakobjectOpenbareRuimte.class);
            case WOONPLAATS:
                return JSONB.fromJson(jsonObject.toString(), ZaakobjectWoonplaats.class);
            case OVERIGE:
                if (StringUtils.equals(objecttypeOverige, ZaakobjectProductaanvraag.OBJECT_TYPE_OVERIGE)) {
                    return JSONB.fromJson(jsonObject.toString(), ZaakobjectProductaanvraag.class);
                } else if (StringUtils.equals(objecttypeOverige, ZaakobjectNummeraanduiding.OBJECT_TYPE_OVERIGE)) {
                    return JSONB.fromJson(jsonObject.toString(), ZaakobjectNummeraanduiding.class);
                }
            default:
                throw new RuntimeException(String.format("objectType '%s' wordt niet ondersteund", objecttype));
        }
    }
}
