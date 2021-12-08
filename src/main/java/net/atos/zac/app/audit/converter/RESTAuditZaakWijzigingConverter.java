package net.atos.zac.app.audit.converter;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.ZaakWijziging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditZaakWijzigingConverter extends AbstractRESTAuditWijzigingConverter<ZaakWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final ZaakWijziging zaakWijziging) {
        final Zaak nieuw = zaakWijziging.getNieuw();
        final Zaak oud = zaakWijziging.getOud();
        if (oud == null) {
            return new RESTWijziging(String.format("Zaak '%s' is toegevoegd", nieuw.getIdentificatie()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Zaak '%s' is verwijderd", oud.getIdentificatie()));
        }

        if (!StringUtils.equals(nieuw.getToelichting(), oud.getToelichting())) {
            return new RESTWijziging("Toelichting", oud.getToelichting(), nieuw.getToelichting());
        }
        if (!StringUtils.equals(nieuw.getOmschrijving(), oud.getOmschrijving())) {
            return new RESTWijziging("Omschrijving", oud.getOmschrijving(), nieuw.getOmschrijving());
        }
        return new RESTWijziging("Onbekend", "-", "-");
    }
}
