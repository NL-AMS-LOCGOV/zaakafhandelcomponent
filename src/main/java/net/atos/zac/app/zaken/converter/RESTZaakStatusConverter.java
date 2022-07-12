/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.zac.app.zaken.model.RESTZaakStatus;

public class RESTZaakStatusConverter {

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakStatus convertToRESTZaakStatus(final Status status, final Statustype statustype) {
        final RESTZaakStatus restZaakStatus = new RESTZaakStatus();
        restZaakStatus.toelichting = status.getStatustoelichting();
        restZaakStatus.naam = statustype.getOmschrijving();
        return restZaakStatus;
    }
}
