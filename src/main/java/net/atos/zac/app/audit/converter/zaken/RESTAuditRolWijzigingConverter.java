/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditRolWijzigingConverter extends AbstractRESTAuditWijzigingConverter<AuditWijziging<Rol>> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ROL == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final AuditWijziging<Rol> wijziging) {
        final Rol<?> oud = wijziging.getOud();
        final Rol<?> nieuw = wijziging.getNieuw();
        if (oud == null) {
            return new RESTWijziging(betrokkeneType(nieuw), null, nieuw.getNaam());
        }
        if (nieuw == null) {
            return new RESTWijziging(betrokkeneType(oud), oud.getNaam(), null);
        }
        return new RESTWijziging(betrokkeneType(nieuw), oud.getNaam(), nieuw.getNaam());
    }

    private String betrokkeneType(final Rol<?> rol) {
        switch (rol.getBetrokkeneType()) {
            case NATUURLIJK_PERSOON:
            case NIET_NATUURLIJK_PERSOON:
            case VESTIGING:
                return "Initiator";
            case ORGANISATORISCHE_EENHEID:
            case MEDEWERKER:
                return "Behandelaar";
            default:
                return rol.getBetrokkeneType().toValue();
        }
    }
}
