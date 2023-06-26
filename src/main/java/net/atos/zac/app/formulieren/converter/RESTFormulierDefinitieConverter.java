/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren.converter;

import java.util.Comparator;

import javax.inject.Inject;

import net.atos.zac.app.formulieren.model.RESTFormulierDefinitie;
import net.atos.zac.app.formulieren.model.RESTFormulierMailGegevens;
import net.atos.zac.formulieren.model.FormulierDefinitie;
import net.atos.zac.formulieren.model.FormulierVeldDefinitie;

public class RESTFormulierDefinitieConverter {

    @Inject
    private RESTFormulierVeldDefinitieConverter veldDefinitieConverter;

    public RESTFormulierDefinitie convert(final FormulierDefinitie formulierDefinitie, boolean inclusiefVelden) {
        final RESTFormulierDefinitie restFormulierDefinitie = new RESTFormulierDefinitie();
        restFormulierDefinitie.id = formulierDefinitie.getId();
        restFormulierDefinitie.beschrijving = formulierDefinitie.getBeschrijving();
        restFormulierDefinitie.naam = formulierDefinitie.getNaam();
        restFormulierDefinitie.creatiedatum = formulierDefinitie.getCreatiedatum();
        restFormulierDefinitie.wijzigingsdatum = formulierDefinitie.getWijzigingsdatum();
        restFormulierDefinitie.uitleg = formulierDefinitie.getUitleg();
        restFormulierDefinitie.systeemnaam = formulierDefinitie.getSysteemnaam();
        if (inclusiefVelden) {
            restFormulierDefinitie.veldDefinities = formulierDefinitie.getVeldDefinities().stream()
                    .sorted(Comparator.comparingInt(FormulierVeldDefinitie::getVolgorde))
                    .map(vd -> veldDefinitieConverter.convert(vd))
                    .toList();
        }
        restFormulierDefinitie.mailVersturen = formulierDefinitie.isMailVersturen();
        restFormulierDefinitie.mailGegevens = new RESTFormulierMailGegevens();
        restFormulierDefinitie.mailGegevens.to = formulierDefinitie.getMailTo();
        restFormulierDefinitie.mailGegevens.from = formulierDefinitie.getMailFrom();
        restFormulierDefinitie.mailGegevens.subject = formulierDefinitie.getMailSubject();
        restFormulierDefinitie.mailGegevens.body = formulierDefinitie.getMailBody();
        return restFormulierDefinitie;
    }

    public FormulierDefinitie convert(final RESTFormulierDefinitie restFormulierDefinitie) {
        return convert(restFormulierDefinitie, new FormulierDefinitie());
    }

    public FormulierDefinitie convert(final RESTFormulierDefinitie restFormulierDefinitie, final FormulierDefinitie formulierDefinitie) {
        formulierDefinitie.setId(restFormulierDefinitie.id);
        formulierDefinitie.setNaam(restFormulierDefinitie.naam);
        formulierDefinitie.setSysteemnaam(restFormulierDefinitie.systeemnaam);
        formulierDefinitie.setBeschrijving(restFormulierDefinitie.beschrijving);
        formulierDefinitie.setUitleg(restFormulierDefinitie.uitleg);
        formulierDefinitie.setVeldDefinities(restFormulierDefinitie.veldDefinities.stream()
                                                     .map(veldDefinitieConverter::convert)
                                                     .toList());

        formulierDefinitie.setMailVersturen(restFormulierDefinitie.mailVersturen);
        formulierDefinitie.setMailTo(restFormulierDefinitie.mailGegevens.to);
        formulierDefinitie.setMailFrom(restFormulierDefinitie.mailGegevens.from);
        formulierDefinitie.setMailSubject(restFormulierDefinitie.mailGegevens.subject);
        formulierDefinitie.setMailBody(restFormulierDefinitie.mailGegevens.body);
        return formulierDefinitie;
    }
}
