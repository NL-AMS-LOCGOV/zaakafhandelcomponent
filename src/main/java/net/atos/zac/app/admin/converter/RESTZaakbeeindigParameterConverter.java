/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.admin.model.RESTZaakbeeindigParameter;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;

public class RESTZaakbeeindigParameterConverter {

    @Inject
    RESTZaakbeeindigRedenConverter restZaakbeeindigRedenConverter;

    @Inject
    RESTZaakResultaattypeConverter restZaakResultaattypeConverter;

    @Inject
    ZTCClientService ztcClientService;

    public RESTZaakbeeindigParameter convertToRest(final ZaakbeeindigParameter zaakbeeindigParameter) {
        final RESTZaakbeeindigParameter restZaakbeeindigParameter = new RESTZaakbeeindigParameter();
        restZaakbeeindigParameter.id = zaakbeeindigParameter.getId();
        restZaakbeeindigParameter.zaakbeeindigReden = restZaakbeeindigRedenConverter.convertToRest(zaakbeeindigParameter.getZaakbeeindigReden());
        restZaakbeeindigParameter.zaakResultaat = restZaakResultaattypeConverter.convertToRest(
                ztcClientService.readResultaattype(zaakbeeindigParameter.getResultaattype()));
        return restZaakbeeindigParameter;
    }

    public List<RESTZaakbeeindigParameter> convertToRest(Collection<ZaakbeeindigParameter> zaakbeeindigRedenen) {
        return zaakbeeindigRedenen.stream()
                .map(this::convertToRest)
                .toList();
    }

    public ZaakbeeindigParameter convertToDomain(final RESTZaakbeeindigParameter restZaakbeeindigParameter) {
        final ZaakbeeindigParameter zaakbeeindigParameter = new ZaakbeeindigParameter();
        zaakbeeindigParameter.setId(restZaakbeeindigParameter.id);
        zaakbeeindigParameter.setZaakbeeindigReden(restZaakbeeindigRedenConverter.convertToDomain(restZaakbeeindigParameter.zaakbeeindigReden));
        zaakbeeindigParameter.setResultaattype(restZaakbeeindigParameter.zaakResultaat.id);
        return zaakbeeindigParameter;
    }

    public List<ZaakbeeindigParameter> convertToDomain(Collection<RESTZaakbeeindigParameter> restZaakbeeindigParameters) {
        return restZaakbeeindigParameters.stream()
                .map(this::convertToDomain)
                .toList();
    }
}
