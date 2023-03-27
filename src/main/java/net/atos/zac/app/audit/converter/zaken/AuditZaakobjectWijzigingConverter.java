/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.net.URI;
import java.util.stream.Stream;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditZaakobjectWijzigingConverter extends AbstractAuditWijzigingConverter<AuditWijziging<Zaakobject>> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAKOBJECT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final AuditWijziging<Zaakobject> wijziging) {
        return Stream.of(new RESTHistorieRegel(toAttribuutLabel(wijziging), toWaarde(wijziging.getOud()), toWaarde(wijziging.getNieuw())));
    }

    private String toAttribuutLabel(final AuditWijziging<Zaakobject> wijziging) {
        final Objecttype objecttype = wijziging.getOud() != null
                ? wijziging.getOud().getObjectType()
                : wijziging.getNieuw().getObjectType();
        return "objecttype." + objecttype.name();
    }

    private String toWaarde(final Zaakobject zaakobject) {
        return zaakobject != null ? extractID(zaakobject.getObject()) : null;
    }

    private static String extractID(final URI uri) {
        if (uri == null) {
            return "-";
        }
        String path = uri.getPath();
        return contains(path, "/") ? substringAfterLast(path, "/") : path;
    }

}
