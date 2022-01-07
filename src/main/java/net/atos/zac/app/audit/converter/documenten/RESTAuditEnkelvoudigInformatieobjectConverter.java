package net.atos.zac.app.audit.converter.documenten;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.documenten.EnkelvoudigInformatieobjectWijziging;
import net.atos.zac.app.audit.converter.AbstractRESTAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTWijziging;

public class RESTAuditEnkelvoudigInformatieobjectConverter extends AbstractRESTAuditWijzigingConverter<EnkelvoudigInformatieobjectWijziging> {

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ENKELVOUDIG_INFORMATIEOBJECT == objectType;
    }

    @Override
    protected RESTWijziging doConvert(final EnkelvoudigInformatieobjectWijziging wijziging) {
        final EnkelvoudigInformatieobject nieuw = wijziging.getNieuw();
        final EnkelvoudigInformatieobject oud = wijziging.getOud();

        if (oud == null) {
            return new RESTWijziging("Document toegevoegd");
        }
        if (nieuw == null) {
            return new RESTWijziging("Document verwijderd");
        }

        if (!Objects.equals(nieuw.getVersie(), oud.getVersie())) {
            return new RESTWijziging("Versie", Integer.toString(oud.getVersie()), Integer.toString(nieuw.getVersie()));
        }
        if (!StringUtils.equals(nieuw.getBeschrijving(), oud.getBeschrijving())) {
            return new RESTWijziging("Beschrijving", oud.getBeschrijving(), nieuw.getBeschrijving());
        }

        return new RESTWijziging();
    }
}
