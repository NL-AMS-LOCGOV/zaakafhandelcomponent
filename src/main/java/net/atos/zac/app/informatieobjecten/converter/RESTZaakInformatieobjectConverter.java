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
import net.atos.zac.app.policy.converter.RESTActiesConverter;
import net.atos.zac.app.zaken.converter.RESTZaakStatusConverter;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.ZaakActies;

public class RESTZaakInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    @Inject
    private RESTActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;

    public RESTZaakInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final Zaak zaak = zrcClientService.readZaak(zaakInformatieObject.getZaak());
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
        final ZaakActies zaakActies = policyService.readZaakActies(zaak, zaaktype, statustype);
        final RESTZaakInformatieobject restZaakInformatieobject = new RESTZaakInformatieobject();
        restZaakInformatieobject.zaakIdentificatie = zaak.getIdentificatie();
        restZaakInformatieobject.zaakActies = actiesConverter.convert(zaakActies);
        if (zaakActies.getLezen()) {
            restZaakInformatieobject.zaakStartDatum = zaak.getStartdatum();
            restZaakInformatieobject.zaakEinddatumGepland = zaak.getEinddatumGepland();
            restZaakInformatieobject.zaaktypeOmschrijving = zaaktype.getOmschrijving();
            if (status != null) {
                restZaakInformatieobject.zaakStatus = restZaakStatusConverter.convertToRESTZaakStatus(status, statustype);
            }
        }
        return restZaakInformatieobject;
    }
}
