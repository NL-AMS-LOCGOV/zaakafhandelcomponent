/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.zaken.model.RESTZaakRechten;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;

import org.apache.commons.collections4.CollectionUtils;

public class RESTZaakRechtenConverter {

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private ZRCClientService zrcClientService;

    public RESTZaakRechten convertToRESTZaakRechten(final Zaak zaak, final boolean heropend) {
        final RESTZaakRechten restZaakRechten = new RESTZaakRechten();

        restZaakRechten.beeindigbaar = !(CollectionUtils.isNotEmpty(zaak.getDeelzaken()) &&
                zaak.getDeelzaken().stream().anyMatch(uri -> zrcClientService.readZaak(uri).isOpen()));
        restZaakRechten.afbreekbaar = !zaakafhandelParameterService.readZaakafhandelParameters(zaak).getZaakbeeindigParameters().isEmpty();
        restZaakRechten.open = zaak.isOpen() || heropend;
        restZaakRechten.opgeschort = zaak.isOpgeschort();
        return restZaakRechten;
    }
}
