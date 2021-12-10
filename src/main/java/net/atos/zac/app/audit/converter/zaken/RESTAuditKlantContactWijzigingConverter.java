package net.atos.zac.app.audit.converter.zaken;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.KlantContactWijziging;
import net.atos.client.zgw.zrc.model.KlantContact;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditKlantContactWijzigingConverter extends AbstractRESTAuditWijzigingConverter<KlantContactWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.KLANTCONTACT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final KlantContactWijziging wijziging) {
        final KlantContact oud = wijziging.getOud();
        final KlantContact nieuw = wijziging.getNieuw();
        if (oud == null) {
            return new RESTWijziging(String.format("Klant contact '%s' is toegevoegd", nieuw.getIdentificatie()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Klant contact '%s' is verwijderd", oud.getIdentificatie()));
        }
        return new RESTWijziging("Klant contact", oud.getToelichting(), nieuw.getToelichting());
    }
}
