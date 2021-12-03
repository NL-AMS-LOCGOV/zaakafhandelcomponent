/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieobject;
import net.atos.zac.app.zaken.converter.RESTZaakStatusConverter;

public class RESTZaakInformatieobjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    public RESTZaakInformatieobject convert(final ZaakInformatieobject zaakInformatieObject) {
        final RESTZaakInformatieobject restZaakInformatieobject = new RESTZaakInformatieobject();

        final Zaak zaak = zrcClientService.readZaak(zaakInformatieObject.getZaak());

        restZaakInformatieobject.status = restZaakStatusConverter.convert(zaak.getStatus());
        restZaakInformatieobject.zaakUuid = zaak.getUuid().toString();
        restZaakInformatieobject.zaakIdentificatie = zaak.getIdentificatie();
        restZaakInformatieobject.zaakStartDatum = zaak.getStartdatum();
        restZaakInformatieobject.zaakEinddatumGepland = zaak.getEinddatumGepland();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        restZaakInformatieobject.zaaktype = zaaktype.getOmschrijving();
        return restZaakInformatieobject;
    }
}
