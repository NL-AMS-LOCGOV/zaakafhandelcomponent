/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieobject;
import net.atos.zac.app.zaken.converter.RESTZaakActiesConverter;
import net.atos.zac.app.zaken.converter.RESTZaakStatusConverter;
import net.atos.zac.policy.PolicyService;

public class RESTZaakInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    @Inject
    private RESTZaakActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;

    public RESTZaakInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final RESTZaakInformatieobject restZaakInformatieobject = new RESTZaakInformatieobject();
        final Zaak zaak = zrcClientService.readZaak(zaakInformatieObject.getZaak());
        Statustype statustype = null;
        if (zaak.getStatus() != null) {
            final Status status = zrcClientService.readStatus(zaak.getStatus());
            statustype = ztcClientService.readStatustype(status.getStatustype());
            restZaakInformatieobject.zaakStatus = restZaakStatusConverter.convertToRESTZaakStatus(status, statustype);
        }
        restZaakInformatieobject.zaakIdentificatie = zaak.getIdentificatie();
        restZaakInformatieobject.zaakStartDatum = zaak.getStartdatum();
        restZaakInformatieobject.zaakEinddatumGepland = zaak.getEinddatumGepland();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        restZaakInformatieobject.zaaktypeOmschrijving = zaaktype.getOmschrijving();
        restZaakInformatieobject.zaakActies = actiesConverter.convert(policyService.readZaakActies(zaak, zaaktype, statustype));
        return restZaakInformatieobject;
    }
}
