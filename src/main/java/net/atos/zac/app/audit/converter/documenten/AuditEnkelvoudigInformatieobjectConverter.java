package net.atos.zac.app.audit.converter.documenten;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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
        checkWaarden("titel", oud.getTitel(), nieuw.getTitel(), historieRegels);
        checkWaarden("identificatie", oud.getIdentificatie(), nieuw.getIdentificatie(), historieRegels);
        checkWaarden("vertrouwelijkheidaanduiding", enumToWaarde(oud.getVertrouwelijkheidaanduiding()), enumToWaarde(nieuw.getVertrouwelijkheidaanduiding()),
                     historieRegels);
        checkWaarden("bestandsnaam", oud.getBestandsnaam(), nieuw.getBestandsnaam(), historieRegels);
        checkWaarden("taal", oud.getTaal(), nieuw.getTaal(), historieRegels);
        checkWaarden("documentType", informatieobjecttypeToWaarde(oud.getInformatieobjecttype()),
                     informatieobjecttypeToWaarde(nieuw.getInformatieobjecttype()), historieRegels);
        checkWaarden("auteur", oud.getAuteur(), nieuw.getAuteur(), historieRegels);
        checkWaarden("ontvangstdatum", oud.getOntvangstdatum(), nieuw.getOntvangstdatum(), historieRegels);
        checkWaarden("registratiedatum", oud.getBeginRegistratie(), nieuw.getBeginRegistratie(), historieRegels);
        checkWaarden("locked", booleanToWaarde(oud.getLocked()), booleanToWaarde(nieuw.getLocked()), historieRegels);
        checkWaarden("versie", Integer.toString(oud.getVersie()), Integer.toString(nieuw.getVersie()), historieRegels);
        checkWaarden("informatieobject.status", enumToWaarde(oud.getStatus()), enumToWaarde(nieuw.getStatus()), historieRegels);
        checkWaarden("bronorganisatie", oud.getBronorganisatie(), nieuw.getBronorganisatie(), historieRegels);
        checkWaarden("verzenddatum", oud.getVerzenddatum(), nieuw.getVerzenddatum(), historieRegels);
        checkWaarden("formaat", oud.getFormaat(), nieuw.getFormaat(), historieRegels);
        checkWaarden("ondertekening", toWaarde(oud.getOndertekening()), toWaarde(nieuw.getOndertekening()), historieRegels);
        checkWaarden("creatiedatum", oud.getCreatiedatum(), nieuw.getCreatiedatum(), historieRegels);

        if (!StringUtils.equals(nieuw.getBeschrijving(), oud.getBeschrijving())) {
            historieRegels.add(new RESTHistorieRegel("Beschrijving", oud.getBeschrijving(), nieuw.getBeschrijving()));
        }

        return historieRegels.stream();
    }

    private String toWaarde(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return enkelvoudigInformatieobject != null ? enkelvoudigInformatieobject.getIdentificatie() : null;
    }

    private String informatieobjecttypeToWaarde(final URI informatieobjecttype) {
        return informatieobjecttype != null ? ztcClientService.readInformatieobjecttype(informatieobjecttype).getOmschrijving() : null;
    }

    private String toWaarde(final Ondertekening ondertekening) {
        return ondertekening != null ? ondertekening.getDatum().toString() : null;
    }

}
