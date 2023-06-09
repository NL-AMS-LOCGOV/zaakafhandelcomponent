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

    private final String SEPORATOR = "|";

    public RESTFormulierVeldDefinitie convert(final FormulierVeldDefinitie veldDefinitie) {
        final RESTFormulierVeldDefinitie restVeldDefinitie = new RESTFormulierVeldDefinitie();
        restVeldDefinitie.id = veldDefinitie.getId();
        restVeldDefinitie.systeemnaam = veldDefinitie.getSysteemnaam();
        restVeldDefinitie.volgorde = veldDefinitie.getVolgorde();
        restVeldDefinitie.label = veldDefinitie.getLabel();
        restVeldDefinitie.veldType = veldDefinitie.getVeldType();
        restVeldDefinitie.beschrijving = veldDefinitie.getBeschrijving();
        restVeldDefinitie.helptekst = veldDefinitie.getHelptekst();
        restVeldDefinitie.defaultWaarde = veldDefinitie.getDefaultWaarde();
        if (StringUtils.isNotBlank(veldDefinitie.getMeerkeuzeOpties())) {
            restVeldDefinitie.meerkeuzeOpties = List.of(StringUtils.split(veldDefinitie.getMeerkeuzeOpties(), SEPORATOR));
        }
        if (StringUtils.isNotBlank(veldDefinitie.getValidaties())) {
            restVeldDefinitie.validaties = List.of(StringUtils.split(veldDefinitie.getValidaties(), SEPORATOR));
        }
        return restVeldDefinitie;
    }

    public FormulierVeldDefinitie convert(final RESTFormulierVeldDefinitie restVeldDefinitie) {
        final FormulierVeldDefinitie veldDefinitie = new FormulierVeldDefinitie();
        veldDefinitie.setId(restVeldDefinitie.id);
        veldDefinitie.setSysteemnaam(restVeldDefinitie.systeemnaam);
        veldDefinitie.setVolgorde(restVeldDefinitie.volgorde);
        veldDefinitie.setLabel(restVeldDefinitie.label);
        veldDefinitie.setVeldType(restVeldDefinitie.veldType);
        veldDefinitie.setBeschrijving(restVeldDefinitie.beschrijving);
        veldDefinitie.setHelptekst(restVeldDefinitie.helptekst);
        veldDefinitie.setDefaultWaarde(restVeldDefinitie.defaultWaarde);
        if (CollectionUtils.isNotEmpty(restVeldDefinitie.meerkeuzeOpties)) {
            veldDefinitie.setMeerkeuzeOpties(String.join(SEPORATOR, restVeldDefinitie.meerkeuzeOpties));
        }
        if (CollectionUtils.isNotEmpty(restVeldDefinitie.validaties)) {
            veldDefinitie.setValidaties(String.join(SEPORATOR, restVeldDefinitie.validaties));
        }
        return veldDefinitie;
    }
}
