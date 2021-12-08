package net.atos.zac.app.audit.converter;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.ZaakInformatieobjectWijziging;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditZaakInformatieobjectConverter extends AbstractRESTAuditWijzigingConverter<ZaakInformatieobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK_INFORMATIEOBJECT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final ZaakInformatieobjectWijziging wijziging) {
        final ZaakInformatieobject nieuw = wijziging.getNieuw();
        final ZaakInformatieobject oud = wijziging.getOud();

        if (oud == null) {
            return new RESTWijziging(String.format("Informatieobject '%s' is toegevoegd aan zaak", nieuw.getTitel()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Informatieobject '%s' is verwijderd van zaak", oud.getTitel()));
        }
        return new RESTWijziging(String.format("Informatieobject '%s' is gewijzigd", nieuw.getTitel()));
    }
}
