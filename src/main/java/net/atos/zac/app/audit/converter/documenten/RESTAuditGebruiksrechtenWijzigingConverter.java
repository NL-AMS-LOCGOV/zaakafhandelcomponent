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
        final Gebruiksrechten nieuw = wijziging.getNieuw();
        final Gebruiksrechten oud = wijziging.getOud();
        if (oud == null) {
            return new RESTWijziging(String.format("Gebruiksrecht '%s' is toegevoegd aan zaak", nieuw.getOmschrijvingVoorwaarden()));
        }
        if (nieuw == null) {
            return new RESTWijziging(String.format("Gebruiksrecht '%s' is verwijderd van zaak", oud.getOmschrijvingVoorwaarden()));
        }
        return new RESTWijziging("Gebruiksrecht is gewijzigd", nieuw.getOmschrijvingVoorwaarden(), oud.getOmschrijvingVoorwaarden());
    }

}
