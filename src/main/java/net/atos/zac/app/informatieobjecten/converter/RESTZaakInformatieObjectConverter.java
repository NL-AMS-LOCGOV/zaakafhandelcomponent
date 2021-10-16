/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.informatieobjecten.model.RESTZaakInformatieObject;
import net.atos.zac.app.zaken.converter.RESTZaakStatusConverter;

public class RESTZaakInformatieObjectConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    public RESTZaakInformatieObject convert(final ZaakInformatieObject zaakInformatieObject) {
        final RESTZaakInformatieObject restZaakInformatieObject = new RESTZaakInformatieObject();

        final Zaak zaak = ZRCClient.getZaak(zaakInformatieObject.getZaak());

        restZaakInformatieObject.status = restZaakStatusConverter.convert(zaak.getStatus());
        restZaakInformatieObject.zaakUuid = zaak.getUuid().toString();
        restZaakInformatieObject.zaakIdentificatie = zaak.getIdentificatie();
        restZaakInformatieObject.zaakStartDatum = zaak.getStartdatum();
        restZaakInformatieObject.zaakEinddatumGepland = zaak.getEinddatumGepland();
        final Zaaktype zaaktype = ztcClientService.getZaaktype(zaak.getZaaktype());
        restZaakInformatieObject.zaaktype = zaaktype.getOmschrijving();
        return restZaakInformatieObject;
    }


}
