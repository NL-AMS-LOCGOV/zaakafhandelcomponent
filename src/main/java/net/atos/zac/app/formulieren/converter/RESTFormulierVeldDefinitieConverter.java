/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren.converter;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.zac.app.formulieren.model.RESTFormulierVeldDefinitie;
import net.atos.zac.formulieren.model.FormulierVeldDefinitie;

public class RESTFormulierVeldDefinitieConverter {

    private static final String SEPARATOR = ";";

    public RESTFormulierVeldDefinitie convert(final FormulierVeldDefinitie veldDefinitie) {
        final RESTFormulierVeldDefinitie restVeldDefinitie = new RESTFormulierVeldDefinitie();
        restVeldDefinitie.id = veldDefinitie.getId();
        restVeldDefinitie.systeemnaam = veldDefinitie.getSysteemnaam();
        restVeldDefinitie.volgorde = veldDefinitie.getVolgorde();
        restVeldDefinitie.label = veldDefinitie.getLabel();
        restVeldDefinitie.veldtype = veldDefinitie.getVeldtype();
        restVeldDefinitie.verplicht = veldDefinitie.isVerplicht();
        restVeldDefinitie.beschrijving = veldDefinitie.getBeschrijving();
        restVeldDefinitie.helptekst = veldDefinitie.getHelptekst();
        restVeldDefinitie.defaultWaarde = veldDefinitie.getDefaultWaarde();
        restVeldDefinitie.meerkeuzeOpties = veldDefinitie.getMeerkeuzeOpties();
        if (StringUtils.isNotBlank(veldDefinitie.getValidaties())) {
            restVeldDefinitie.validaties = List.of(StringUtils.split(veldDefinitie.getValidaties(), SEPARATOR));
        }
        return restVeldDefinitie;
    }

    public FormulierVeldDefinitie convert(final RESTFormulierVeldDefinitie restVeldDefinitie) {
        final FormulierVeldDefinitie veldDefinitie = new FormulierVeldDefinitie();
        veldDefinitie.setId(restVeldDefinitie.id);
        veldDefinitie.setSysteemnaam(restVeldDefinitie.systeemnaam);
        veldDefinitie.setVolgorde(restVeldDefinitie.volgorde);
        veldDefinitie.setLabel(restVeldDefinitie.label);
        veldDefinitie.setVeldtype(restVeldDefinitie.veldtype);
        veldDefinitie.setVerplicht(restVeldDefinitie.verplicht);
        veldDefinitie.setBeschrijving(restVeldDefinitie.beschrijving);
        veldDefinitie.setHelptekst(restVeldDefinitie.helptekst);
        veldDefinitie.setDefaultWaarde(restVeldDefinitie.defaultWaarde);
        veldDefinitie.setMeerkeuzeOpties(restVeldDefinitie.meerkeuzeOpties);
        if (CollectionUtils.isNotEmpty(restVeldDefinitie.validaties)) {
            veldDefinitie.setValidaties(String.join(SEPARATOR, restVeldDefinitie.validaties));
        }
        return veldDefinitie;
    }
}
