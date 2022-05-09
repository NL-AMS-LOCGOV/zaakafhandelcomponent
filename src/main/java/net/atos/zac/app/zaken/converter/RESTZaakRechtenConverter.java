/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.model.RESTZaakRechten;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;

public class RESTZaakRechtenConverter {

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public RESTZaakRechten convertToRESTZaakRechten(final Zaaktype zaaktype, final Zaak zaak) {
        final RESTZaakRechten restZaakRechten = new RESTZaakRechten();
        restZaakRechten.afbreekbaar = !zaakafhandelParameterService.getZaakafhandelParameters(zaak).getZaakbeeindigParameters().isEmpty();
        restZaakRechten.open = zaak.isOpen();
        restZaakRechten.opgeschort = zaak.isOpgeschort();
        return restZaakRechten;
    }
}
