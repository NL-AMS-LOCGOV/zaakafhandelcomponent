/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.shared.model.audit.besluiten.BesluitInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.besluiten.BesluitWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.EnkelvoudigInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.GebuiksrechtenWijziging;
import net.atos.client.zgw.shared.model.audit.documenten.ObjectInformatieobjectWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.KlantcontactWijziging;
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
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectAdresWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectNummeraanduidingWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectOpenbareRuimteWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectPandWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectProductaanvraagWijziging;
import net.atos.client.zgw.shared.model.audit.zaken.objecten.ZaakobjectWoonplaatsWijziging;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectProductaanvraag;

public enum ObjectType {

    /**
     * LETOP: Volgorde is belangrijk. ZAAKEIGENSCHAP moet boven ZAAK; ZAAK url matcht ook op ZAAKEIGENSCHAP url
     * http://open-zaak/zaken/api/v1/zaken/{zaak_uuid}/zaakeigenschappen
     */
    ZAAKEIGENSCHAP("/zaakeigenschappen/", ZaakEigenschapWijziging.class),

    /** http://open-zaak/zaken/api/v1/zaken/{uuid} */
    ZAAK("/zaken/api/v1/zaken/", ZaakWijziging.class),

    /** http://open-zaak/zaken/api/v1/resultaten/{uuid} */
    RESULTAAT("/zaken/api/v1/resultaten/", ResultaatWijziging.class),

    /** http://open-zaak/zaken/api/v1/statussen/{uuid} */
    STATUS("/zaken/api/v1/statussen/", StatusWijziging.class),

    /** http://open-zaak/zaken/api/v1/rollen/{uuid} */
    ROL("/zaken/api/v1/rollen/", null),

    /** http://open-zaak/zaken/api/v1/zaakobjecten/{uuid} */
    ZAAKOBJECT("/zaken/api/v1/zaakobjecten", ZaakobjectAdresWijziging.class),

    /** http://open-zaak/zaken/api/v1/zaakinformatieobjecten/{uuid} */
    ZAAK_INFORMATIEOBJECT("/zaken/api/v1/zaakinformatieobjecten", ZaakInformatieobjectWijziging.class),

    /** http://open-zaak/documenten/api/v1/enkelvoudiginformatieobjecten/{uuid} */
    ENKELVOUDIG_INFORMATIEOBJECT("/documenten/api/v1/enkelvoudiginformatieobjecten/", EnkelvoudigInformatieobjectWijziging.class),

    /** http://open-zaak/documenten/api/v1/gebruiksrechten/{uuid} */
    GEBRUIKSRECHTEN("/documenten/api/v1/gebruiksrechten", GebuiksrechtenWijziging.class),

    /** http://open-zaak/documenten/api/v1/objectinformatieobjecten/{uuid} */
    OBJECT_INFORMATIEOBJECT("documenten/api/v1/objectinformatieobjecten", ObjectInformatieobjectWijziging.class),

    /** http://open-zaak/zaken/api/v1/klantcontacten/{uuid} */
    KLANTCONTACT("/zaken/api/v1/klantcontacten", KlantcontactWijziging.class),

    /** http://open-zaak.default/besluiten/api/v1/besluit/{uuid} */
    BESLUIT("/besluiten/api/v1/besluiten", BesluitWijziging.class),

    /** http://open-zaak.default/besluiten/api/v1/besluitinformatieobjecten/{uuid} */
    BESLUIT_INFORMATIEOBJECT("/besluiten/api/v1/besluitinformatieobjecten", BesluitInformatieobjectWijziging.class);

    private final String url;

    public final Type auditClass;

    ObjectType(final String url, final Class<? extends AuditWijziging<?>> classType) {
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

    public Class<? extends AuditWijziging<? extends Rol<?>>> getAuditClass(final BetrokkeneType betrokkenetype) {
        return switch (betrokkenetype) {
            case VESTIGING -> RolVestigingWijziging.class;
            case MEDEWERKER -> RolMedewerkerWijziging.class;
            case NATUURLIJK_PERSOON -> RolNatuurlijkPersoonWijziging.class;
            case NIET_NATUURLIJK_PERSOON -> RolNietNatuurlijkPersoonWijziging.class;
            case ORGANISATORISCHE_EENHEID -> RolOrganisatorischeEenheidWijziging.class;
        };
    }

    public Type getAuditClass() {
        return auditClass;
    }

    public Class<? extends AuditWijziging<? extends Zaakobject>> getAuditClass(final Objecttype objectType, final String objectTypeOverige) {
        return switch (objectType) {
            case ADRES -> ZaakobjectAdresWijziging.class;
            case OPENBARE_RUIMTE -> ZaakobjectOpenbareRuimteWijziging.class;
            case PAND -> ZaakobjectPandWijziging.class;
            case WOONPLAATS -> ZaakobjectWoonplaatsWijziging.class;
            case OVERIGE -> getAuditClassObjectOverige(objectTypeOverige);
            default -> throw new RuntimeException(String.format("objecttype '%s'  wordt niet ondersteund", objectType));
        };
    }

    private Class<? extends AuditWijziging<? extends Zaakobject>> getAuditClassObjectOverige(final String objectTypeOverige) {
        if (StringUtils.equals(objectTypeOverige, ZaakobjectNummeraanduiding.OBJECT_TYPE_OVERIGE)) {
            return ZaakobjectNummeraanduidingWijziging.class;
        } else if (StringUtils.equals(objectTypeOverige, ZaakobjectProductaanvraag.OBJECT_TYPE_OVERIGE)) {
            return ZaakobjectProductaanvraagWijziging.class;
        }
        throw new RuntimeException(String.format("objecttype 'OVERIGE' met objectTypeOverige '%s' wordt niet ondersteund", objectTypeOverige));
    }
}
