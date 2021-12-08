package net.atos.zac.app.audit.converter;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.StatusWijziging;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditStatusWijzigingConverter extends AbstractRESTAuditWijzigingConverter<StatusWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.STATUS == objectType;
    }

    protected RESTWijziging doConvert(final StatusWijziging statusWijziging) {
        final Status nieuw = statusWijziging.getNieuw();
        final Status oud = statusWijziging.getOud();
        if (oud == null) {
            final Statustype statustype = ztcClientService.readStatustype(nieuw.getStatustype());
            return new RESTWijziging("Status", "", statustype.getOmschrijving());
        }
        if (nieuw == null) {
            final Statustype statustype = ztcClientService.readStatustype(oud.getStatustype());
            return new RESTWijziging("Status", statustype.getOmschrijving(), "");
        }

        final Statustype statustypeOud = ztcClientService.readStatustype(oud.getStatustype());
        final Statustype statustypeNieuw = ztcClientService.readStatustype(nieuw.getStatustype());
        return new RESTWijziging("Status", statustypeOud.getOmschrijving(), statustypeNieuw.getOmschrijving());
    }
}
