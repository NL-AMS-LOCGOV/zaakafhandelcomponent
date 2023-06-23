/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.formulieren;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.formulieren.model.FormulierDefinitie;

public class FormulierRuntimeHelper {

    public static final DateTimeFormatter DATUM_FORMAAT = DateTimeFormatter.ofPattern("dd-MM-yyy");

    public static void resolveDefaultwaarden(FormulierDefinitie formulierDefinitie, Map<String, String> dataElementen) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            dataElementen.forEach((k, v) -> {
                if (k.equals(veldDefinitie.getDefaultWaarde())) {
                    veldDefinitie.setDefaultWaarde(v);
                }
            });
        });
    }

    public static void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, RESTTaak taak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "{TAAK_STARTDATUM}" ->
                            veldDefinitie.setDefaultWaarde(taak.creatiedatumTijd.format(DATUM_FORMAAT));
                    case "{TAAK_FATALE_DATUM}" ->
                            veldDefinitie.setDefaultWaarde(taak.fataledatum.format(DATUM_FORMAAT));
                    case "{TAAK_TOEGEKENDE_GROEP}" ->
                            veldDefinitie.setDefaultWaarde(taak.groep != null ? taak.groep.naam : null);
                    case "{TAAK_TOEGEKENDE_MEDEWERKER}" ->
                            veldDefinitie.setDefaultWaarde(taak.behandelaar != null ? taak.behandelaar.naam : null);
                    default -> {
                    }
                }
            }
        });

    }

    public static void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, RESTZaak zaak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "{ZAAK_STARTDATUM}" -> veldDefinitie.setDefaultWaarde(zaak.startdatum.format(DATUM_FORMAAT));
                    case "{ZAAK_FATALE_DATUM}" ->
                            veldDefinitie.setDefaultWaarde(zaak.uiterlijkeEinddatumAfdoening.format(DATUM_FORMAAT));
                    case "{ZAAK_STREEFDATUM}" ->
                            veldDefinitie.setDefaultWaarde(zaak.einddatumGepland.format(DATUM_FORMAAT));
                    case "{ZAAK_TOEGEKENDE_GROEP}" ->
                            veldDefinitie.setDefaultWaarde(zaak.groep != null ? zaak.groep.naam : null);
                    case "{ZAAK_TOEGEKENDE_MEDEWERKER}" ->
                            veldDefinitie.setDefaultWaarde(zaak.behandelaar != null ? zaak.behandelaar.naam : null);
                    default -> {
                    }
                }
            }
        });
    }


    public static void resolveDefaultwaarden(final FormulierDefinitie formulierDefinitie, Zaak zaak) {
        formulierDefinitie.getVeldDefinities().forEach(veldDefinitie -> {
            if (StringUtils.isNotBlank(veldDefinitie.getDefaultWaarde())) {
                switch (veldDefinitie.getDefaultWaarde()) {
                    case "{ZAAK_STARTDATUM}" ->
                            veldDefinitie.setDefaultWaarde(zaak.getStartdatum().format(DATUM_FORMAAT));
                    case "{ZAAK_FATALE_DATUM}" -> veldDefinitie.setDefaultWaarde(
                            zaak.getUiterlijkeEinddatumAfdoening().format(DATUM_FORMAAT));
                    case "{ZAAK_STREEFDATUM}" ->
                            veldDefinitie.setDefaultWaarde(zaak.getEinddatumGepland().format(DATUM_FORMAAT));
                    case "{ZAAK_TOEGEKENDE_GROEP}" -> veldDefinitie.setDefaultWaarde(null); // moet nog;
                    case "{ZAAK_TOEGEKENDE_MEDEWERKER}" -> veldDefinitie.setDefaultWaarde(null); // moet nog;
                    default -> {
                    }
                }
            }
        });
    }

}
