package net.atos.zac.app.audit.converter;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.AuditWijziging;
import net.atos.zac.app.audit.model.RESTWijziging;

public abstract class AbstractRESTAuditWijzigingConverter<W extends AuditWijziging<?>> {

    public RESTWijziging convert(AuditWijziging<?> wijziging) {
        return doConvert((W) wijziging);
    }

    public abstract boolean supports(ObjectType objectType);

    protected abstract RESTWijziging doConvert(final W wijziging);
}
