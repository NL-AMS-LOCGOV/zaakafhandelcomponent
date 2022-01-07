package net.atos.zac.app.audit.converter.documenten;

import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.documenten.GebuiksrechtenWijziging;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditGebruiksrechtenWijzigingConverter extends AbstractRESTAuditWijzigingConverter<GebuiksrechtenWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.GEBRUIKSRECHTEN == objectType;
    }

    protected RESTWijziging doConvert(final GebuiksrechtenWijziging wijziging) {
        return new RESTWijziging("Gebruiksrecht", gebruiksRechten(wijziging.getNieuw()), gebruiksRechten(wijziging.getOud()));
    }

    private String gebruiksRechten(final Gebruiksrechten gebruiksrechten) {
        return gebruiksrechten == null ? null : gebruiksrechten.getOmschrijvingVoorwaarden();
    }
}
