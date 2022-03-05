/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;

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

    public RESTZaakStatus convertToRESTZaakStatus(final URI statusURI) {
        if (statusURI != null) {
            final Status status = zrcClientService.readStatus(statusURI);
            final RESTZaakStatus restZaakStatus = new RESTZaakStatus();
            restZaakStatus.toekenningsdatumTijd = status.getDatumStatusGezet();
            restZaakStatus.toelichting = status.getStatustoelichting();
            final Statustype statustype = ztcClientService.readStatustype(status.getStatustype());
            restZaakStatus.eindStatus = statustype.getEindstatus();
            restZaakStatus.statusToelichting = statustype.getToelichting();
            restZaakStatus.informeren = statustype.getInformeren();
            restZaakStatus.informerenStatusTekst = statustype.getStatustekst();
            restZaakStatus.naam = statustype.getOmschrijving();
            restZaakStatus.naamGeneriek = statustype.getOmschrijvingGeneriek();
            return restZaakStatus;
        } else {
            return null;
        }
    }

    public String convertToStatusOmschrijving(final URI statusURI) {
        final Status status = zrcClientService.readStatus(statusURI);
        final Statustype statustype = ztcClientService.readStatustype(status.getStatustype());
        return statustype.getOmschrijving();
    }
}
