package net.atos.zac.app.audit.converter.besluiten;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.besluiten.BesluitWijziging;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditBesluitConverter extends AbstractAuditWijzigingConverter<BesluitWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.BESLUIT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final BesluitWijziging wijziging) {
        final Besluit oud = wijziging.getOud();
        final Besluit nieuw = wijziging.getNieuw();

        if (oud == null || nieuw == null) {
            return Stream.of(new RESTHistorieRegel("Besluit", toWaarde(oud), toWaarde(nieuw)));
        }

        final List<RESTHistorieRegel> historieRegels = new LinkedList<>();
        checkAttribuut("identificatie", oud.getIdentificatie(), nieuw.getIdentificatie(), historieRegels);
        checkAttribuut("verzenddatum", oud.getVerzenddatum(), nieuw.getVerzenddatum(), historieRegels);
        checkAttribuut("ingangsdatum", oud.getIngangsdatum(), nieuw.getIngangsdatum(), historieRegels);
        checkAttribuut("vervaldatum", oud.getVervaldatum(), nieuw.getVervaldatum(), historieRegels);
        checkAttribuut("toelichting", oud.getToelichting(), nieuw.getToelichting(), historieRegels);
        return historieRegels.stream();
    }

    private String toWaarde(final Besluit besluit) {
        return besluit != null ? besluit.getIdentificatie() : null;
    }
}
