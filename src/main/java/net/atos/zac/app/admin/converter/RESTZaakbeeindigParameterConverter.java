/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.admin.model.RESTZaakbeeindigParameter;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;

public class RESTZaakbeeindigParameterConverter {

    @Inject
    private RESTZaakbeeindigRedenConverter restZaakbeeindigRedenConverter;

    @Inject
    private RESTZaakResultaattypeConverter restZaakResultaattypeConverter;

    @Inject
    private ZTCClientService ztcClientService;

    public List<RESTZaakbeeindigParameter> convertZaakbeeindigParameters(final Set<ZaakbeeindigParameter> zaakbeeindigRedenen) {
        return zaakbeeindigRedenen.stream()
                .map(this::convertZaakbeeindigParameter)
                .toList();
    }

    public List<ZaakbeeindigParameter> convertRESTZaakbeeindigParameters(final List<RESTZaakbeeindigParameter> restZaakbeeindigParameters) {
        return restZaakbeeindigParameters.stream()
                .map(this::convertRESTZaakbeeindigParameter)
                .toList();
    }

    private RESTZaakbeeindigParameter convertZaakbeeindigParameter(final ZaakbeeindigParameter zaakbeeindigParameter) {
        final RESTZaakbeeindigParameter restZaakbeeindigParameter = new RESTZaakbeeindigParameter();
        restZaakbeeindigParameter.id = zaakbeeindigParameter.getId();
        restZaakbeeindigParameter.zaakbeeindigReden = restZaakbeeindigRedenConverter.convertZaakbeeindigReden(zaakbeeindigParameter.getZaakbeeindigReden());
        restZaakbeeindigParameter.zaakResultaat = restZaakResultaattypeConverter.convertResultaattype(
                ztcClientService.readResultaattype(zaakbeeindigParameter.getResultaattype()));
        return restZaakbeeindigParameter;
    }

    private ZaakbeeindigParameter convertRESTZaakbeeindigParameter(final RESTZaakbeeindigParameter restZaakbeeindigParameter) {
        final ZaakbeeindigParameter zaakbeeindigParameter = new ZaakbeeindigParameter();
        zaakbeeindigParameter.setId(restZaakbeeindigParameter.id);
        zaakbeeindigParameter.setZaakbeeindigReden(restZaakbeeindigRedenConverter.convertRESTZaakbeeindigReden(restZaakbeeindigParameter.zaakbeeindigReden));
        zaakbeeindigParameter.setResultaattype(restZaakbeeindigParameter.zaakResultaat.id);
        return zaakbeeindigParameter;
    }
}
