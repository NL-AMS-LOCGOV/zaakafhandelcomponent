/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import static net.atos.client.zgw.shared.util.JsonbUtil.JSONB;

import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Objecttype;

public class AuditWijzigingJsonbDeserializer implements JsonbDeserializer<AuditWijziging<?>> {

    @Override
    public AuditWijziging<?> deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        final JsonObject wijzigingObject = parser.getObject();
        final JsonObject waardeObject;
        if (!wijzigingObject.isNull("oud")) {
            waardeObject = wijzigingObject.get("oud").asJsonObject();
        } else if (!wijzigingObject.isNull("nieuw")) {
            waardeObject = wijzigingObject.get("nieuw").asJsonObject();
        } else {
            return null;
        }

        final ObjectType type = ObjectType.getObjectType(waardeObject.getJsonString("url").getString());
        if (type == ObjectType.ROL) {
            final BetrokkeneType betrokkenetype = BetrokkeneType.fromValue(waardeObject.getJsonString("betrokkeneType").getString());
            return JSONB.fromJson(wijzigingObject.toString(), type.getAuditClass(betrokkenetype));
        } else if (type == ObjectType.ZAAKOBJECT) {
            final Objecttype objectType = Objecttype.fromValue(waardeObject.getJsonString("objectType").getString());
            final String objectTypeOverige = waardeObject.getJsonString("objectTypeOverige").getString();
            return JSONB.fromJson(wijzigingObject.toString(), type.getAuditClass(objectType, objectTypeOverige));
        }
        return JSONB.fromJson(wijzigingObject.toString(), type.getAuditClass());
    }
}
