/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.util.stream.Stream;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditRolWijzigingConverter extends AbstractAuditWijzigingConverter<AuditWijziging<Rol>> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ROL == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final AuditWijziging<Rol> wijziging) {
        return Stream.of(new RESTHistorieRegel(toAttribuutLabel(wijziging), toWaarde(wijziging.getOud()), toWaarde(wijziging.getNieuw())));
    }

    private String toAttribuutLabel(final AuditWijziging<Rol> wijziging) {
        final BetrokkeneType betrokkeneType = wijziging.getOud() != null
                ? wijziging.getOud().getBetrokkeneType()
                : wijziging.getNieuw().getBetrokkeneType();
        return "betrokkenetype." + betrokkeneType.name();
    }

    private String toWaarde(final Rol<?> rol) {
        return rol != null ? rol.getNaam() : null;
    }
}
