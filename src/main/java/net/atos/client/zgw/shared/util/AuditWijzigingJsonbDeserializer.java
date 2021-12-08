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
import net.atos.client.zgw.shared.model.audit.documenten.EnkelvoudigInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.GebuiksrechtenWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ResultaatWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolMedewerkerWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolNatuurlijkPersoonWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolNietNatuurlijkPersoonWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolOrganisatorischeEenheidWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolVestigingWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.StatusWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakobjectWijziging;
import net.atos.client.zgw.zrc.model.BetrokkeneType;

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

        final ObjectType type = URIUtil.getObjectTypeFromResourceURL(waardeObject.get("url").toString());
        switch (type) {
            case ZAAK:
                return JSONB.fromJson(wijzigingObject.toString(), ZaakWijziging.class);
            case RESULTAAT:
                return JSONB.fromJson(wijzigingObject.toString(), ResultaatWijziging.class);
            case STATUS:
                return JSONB.fromJson(wijzigingObject.toString(), StatusWijziging.class);
            case ENKELVOUDIG_INFORMATIEOBJECT:
                return JSONB.fromJson(wijzigingObject.toString(), EnkelvoudigInformatieobjectWijziging.class);
            case ZAAK_INFORMATIEOBJECT:
                return JSONB.fromJson(wijzigingObject.toString(), ZaakInformatieobjectWijziging.class);
            case GEBRUIKSRECHTEN:
                return JSONB.fromJson(wijzigingObject.toString(), GebuiksrechtenWijziging.class);
            case ZAAKOBJECT:
                return JSONB.fromJson(wijzigingObject.toString(), ZaakobjectWijziging.class);
            case ROL:
                final BetrokkeneType betrokkenetype = BetrokkeneType.fromValue(waardeObject.getJsonString("betrokkeneType").getString());
                return getRolWijziging(wijzigingObject, betrokkenetype);
        }
        return null;
    }

    private AuditWijziging<?> getRolWijziging(final JsonObject wijzigingObject, final BetrokkeneType betrokkenetype) {
        switch (betrokkenetype) {
            case VESTIGING:
                return JSONB.fromJson(wijzigingObject.toString(), RolVestigingWijziging.class);
            case MEDEWERKER:
                return JSONB.fromJson(wijzigingObject.toString(), RolMedewerkerWijziging.class);
            case NATUURLIJK_PERSOON:
                return JSONB.fromJson(wijzigingObject.toString(), RolNatuurlijkPersoonWijziging.class);
            case NIET_NATUURLIJK_PERSOON:
                return JSONB.fromJson(wijzigingObject.toString(), RolNietNatuurlijkPersoonWijziging.class);
            case ORGANISATORISCHE_EENHEID:
                return JSONB.fromJson(wijzigingObject.toString(), RolOrganisatorischeEenheidWijziging.class);
            default:
                throw new RuntimeException(String.format("BetrokkeneType '%s' wordt niet ondersteund", betrokkenetype));
        }
    }
}
