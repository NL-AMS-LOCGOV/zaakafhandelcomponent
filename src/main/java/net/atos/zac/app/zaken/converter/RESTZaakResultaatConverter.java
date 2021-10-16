/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.zac.app.zaken.model.RESTZaakResultaat;
import net.atos.zac.util.PeriodUtil;

public class RESTZaakResultaatConverter {

    @Inject
    private ZTCClientService ztcClientService;

    public RESTZaakResultaat convert(final URI resultaatURI) {
        if (resultaatURI != null) {
            final Resultaat resultaat = ZRCClient.getResultaat(resultaatURI);
            if (resultaat != null) {
                final RESTZaakResultaat restZaakResultaat = new RESTZaakResultaat();
                final Resultaattype resultaattype = ztcClientService.getResultaattype(resultaat.getResultaattype());
                restZaakResultaat.toelichting = resultaat.getToelichting();
                restZaakResultaat.archiefNominatie = resultaattype.getArchiefnominatie().name();
                restZaakResultaat.toelichtingResultaattype = resultaattype.getToelichting();
                restZaakResultaat.naam = resultaattype.getOmschrijving();
                restZaakResultaat.naamGeneriek = resultaattype.getOmschrijvingGeneriek();
                restZaakResultaat.archiefTermijn = PeriodUtil.format(resultaattype.getArchiefactietermijn());
                return restZaakResultaat;
            }
        }
        return null;
    }

}
