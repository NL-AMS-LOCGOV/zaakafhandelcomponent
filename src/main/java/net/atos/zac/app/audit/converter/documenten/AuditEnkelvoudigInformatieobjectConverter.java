package net.atos.zac.app.audit.converter.documenten;

import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.Ondertekening;
import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.documenten.EnkelvoudigInformatieobjectWijziging;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditEnkelvoudigInformatieobjectConverter extends AbstractAuditWijzigingConverter<EnkelvoudigInformatieobjectWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ENKELVOUDIG_INFORMATIEOBJECT == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final EnkelvoudigInformatieobjectWijziging wijziging) {
        final EnkelvoudigInformatieobject oud = wijziging.getOud();
        final EnkelvoudigInformatieobject nieuw = wijziging.getNieuw();

        if (oud == null || nieuw == null) {
            return Stream.of(new RESTHistorieRegel("informatieobject", toWaarde(oud), toWaarde(nieuw)));
        }

        final List<RESTHistorieRegel> historieRegels = new LinkedList<>();
        checkAttribuut("titel", oud.getTitel(), nieuw.getTitel(), historieRegels);
        checkAttribuut("identificatie", oud.getIdentificatie(), nieuw.getIdentificatie(), historieRegels);
        checkAttribuut("vertrouwelijkheidaanduiding", oud.getVertrouwelijkheidaanduiding(), nieuw.getVertrouwelijkheidaanduiding(), historieRegels);
        checkAttribuut("bestandsnaam", oud.getBestandsnaam(), nieuw.getBestandsnaam(), historieRegels);
        checkAttribuut("taal", oud.getTaal(), nieuw.getTaal(), historieRegels);
        checkInformatieobjecttype(oud.getInformatieobjecttype(), nieuw.getInformatieobjecttype(), historieRegels);
        checkAttribuut("auteur", oud.getAuteur(), nieuw.getAuteur(), historieRegels);
        checkAttribuut("ontvangstdatum", oud.getOntvangstdatum(), nieuw.getOntvangstdatum(), historieRegels);
        checkAttribuut("registratiedatum", oud.getBeginRegistratie(), nieuw.getBeginRegistratie(), historieRegels);
        checkAttribuut("locked", oud.getLocked(), nieuw.getLocked(), historieRegels);
        checkAttribuut("versie", Integer.toString(oud.getVersie()), Integer.toString(nieuw.getVersie()), historieRegels);
        checkAttribuut("informatieobject.status", oud.getStatus(), nieuw.getStatus(), historieRegels);
        checkAttribuut("bronorganisatie", oud.getBronorganisatie(), nieuw.getBronorganisatie(), historieRegels);
        checkAttribuut("verzenddatum", oud.getVerzenddatum(), nieuw.getVerzenddatum(), historieRegels);
        checkAttribuut("formaat", oud.getFormaat(), nieuw.getFormaat(), historieRegels);
        checkAttribuut("ondertekening", toWaarde(oud.getOndertekening()), toWaarde(nieuw.getOndertekening()), historieRegels);
        checkAttribuut("creatiedatum", oud.getCreatiedatum(), nieuw.getCreatiedatum(), historieRegels);
        return historieRegels.stream();
    }

    private void checkInformatieobjecttype(final URI oud, final URI nieuw, final List<RESTHistorieRegel> historieRegels) {
        if (ObjectUtils.notEqual(oud, nieuw)) {
            historieRegels.add(new RESTHistorieRegel("documentType", informatieobjecttypeToWaarde(oud), informatieobjecttypeToWaarde(nieuw)));
        }
    }

    private String informatieobjecttypeToWaarde(final URI informatieobjecttype) {
        return informatieobjecttype != null ? ztcClientService.readInformatieobjecttype(informatieobjecttype).getOmschrijving() : null;
    }

    private String toWaarde(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return enkelvoudigInformatieobject != null ? enkelvoudigInformatieobject.getIdentificatie() : null;
    }

    private LocalDate toWaarde(final Ondertekening ondertekening) {
        return ondertekening != null ? ondertekening.getDatum() : null;
    }
}
