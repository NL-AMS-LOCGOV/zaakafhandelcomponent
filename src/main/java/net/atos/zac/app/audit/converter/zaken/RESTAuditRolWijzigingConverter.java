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
        final Rol oud = wijziging.getOud();
        final Rol nieuw = wijziging.getNieuw();
        if (oud == null) {
            return new RESTWijziging(String.format("%s '%s' is toegevoegd", nieuw.getBetrokkeneType().toValue(), nieuw.getNaam()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("%s '%s' is verwijderd", oud.getBetrokkeneType().toValue(), oud.getNaam()));
        }
        return new RESTWijziging(nieuw.getBetrokkeneType().toValue(), oud.getNaam(), nieuw.getNaam());
    }
}
