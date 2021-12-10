/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.model.audit.documenten.EnkelvoudigInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.GebuiksrechtenWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.ObjectInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.KlantContactWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ResultaatWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolMedewerkerWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolNatuurlijkPersoonWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolNietNatuurlijkPersoonWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolOrganisatorischeEenheidWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.RolVestigingWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.StatusWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakEigenschapWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakobjectWijziging;
import net.atos.client.zgw.zrc.model.BetrokkeneType;

public enum ObjectType {
    ZAAKEIGENSCHAP("/zaakeigenschappen/", ZaakEigenschapWijziging.class),
    ZAAK("/zaken/api/v1/zaken/", ZaakWijziging.class),
    RESULTAAT("/zaken/api/v1/resultaten/", ResultaatWijziging.class),
    STATUS("/zaken/api/v1/statussen/", StatusWijziging.class),
    ROL("/zaken/api/v1/rollen/", null),
    ZAAKOBJECT("/zaken/api/v1/zaakobjecten", ZaakobjectWijziging.class),
    ZAAK_INFORMATIEOBJECT("/zaken/api/v1/zaakinformatieobjecten", ZaakInformatieobjectWijziging.class),
    ENKELVOUDIG_INFORMATIEOBJECT("/documenten/api/v1/enkelvoudiginformatieobjecten/", EnkelvoudigInformatieobjectWijziging.class),
    GEBRUIKSRECHTEN("/documenten/api/v1/gebruiksrechten", GebuiksrechtenWijziging.class),
    OBJECT_INFORMATIEOBJECT("documenten/api/v1/objectinformatieobjecten", ObjectInformatieobjectWijziging.class),
    KLANTCONTACT("/zaken/api/v1/klantcontacten", KlantContactWijziging.class);

    private final String url;

    public final Type auditClass;

    ObjectType(final String url, Type classType) {
        this.url = url;
        this.auditClass = classType;
    }

    public static ObjectType getObjectType(String url) {
        for (ObjectType value : values()) {
            if (StringUtils.contains(url, value.url)) {
                return value;
            }
        }
        throw new RuntimeException(String.format("URL '%s' wordt niet ondersteund", url));
    }

    public Type getAuditClass(final BetrokkeneType betrokkenetype) {
        switch (betrokkenetype) {
            case VESTIGING:
                return RolVestigingWijziging.class;
            case MEDEWERKER:
                return RolMedewerkerWijziging.class;
            case NATUURLIJK_PERSOON:
                return RolNatuurlijkPersoonWijziging.class;
            case NIET_NATUURLIJK_PERSOON:
                return RolNietNatuurlijkPersoonWijziging.class;
            case ORGANISATORISCHE_EENHEID:
                return RolOrganisatorischeEenheidWijziging.class;
            default:
                throw new RuntimeException(String.format("BetrokkeneType '%s' wordt niet ondersteund", betrokkenetype));
        }
    }

    public Type getAuditClass() {
        return auditClass;
    }

}
