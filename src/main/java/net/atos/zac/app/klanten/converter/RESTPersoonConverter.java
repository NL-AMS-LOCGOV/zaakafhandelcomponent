/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import static net.atos.zac.app.klanten.KlantenUtil.ONBEKEND;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.brp.model.Geboorte;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.ListPersonenParameters;
import net.atos.client.brp.model.NaamPersoon;
import net.atos.client.brp.model.Verblijfplaats;
import net.atos.zac.app.klanten.KlantenUtil;
import net.atos.zac.app.klanten.model.personen.RESTListPersonenParameters;
import net.atos.zac.app.klanten.model.personen.RESTPersoon;

public class RESTPersoonConverter {

    public static final String FIELDS_PERSOON =
            "burgerservicenummer," +
                    "geslachtsaanduiding," +
                    "naam.voornamen," +
                    "naam.voorvoegsel," +
                    "naam.geslachtsnaam," +
                    "geboorte.datum.datum," +
                    "verblijfplaats.straat," +
                    "verblijfplaats.huisnummer," +
                    "verblijfplaats.huisnummertoevoeging," +
                    "verblijfplaats.huisletter," +
                    "verblijfplaats.woonplaats";

    public RESTPersoon convert(final IngeschrevenPersoonHal persoon) {
        final RESTPersoon restPersoon = new RESTPersoon();
        restPersoon.bsn = persoon.getBurgerservicenummer();
        restPersoon.geslacht = persoon.getGeslachtsaanduiding().toString();
        restPersoon.naam = convertToNaam(persoon.getNaam());
        restPersoon.geboortedatum = convertToGeboortedatum(persoon.getGeboorte());
        restPersoon.inschrijfadres = convertToInschrijfadres(persoon.getVerblijfplaats());
        return restPersoon;
    }

    public ListPersonenParameters convert(final RESTListPersonenParameters restListPersonenParameters) {

        final ListPersonenParameters listPersonenParameters = new ListPersonenParameters();
        listPersonenParameters.setFields(FIELDS_PERSOON);
        if (StringUtils.isNotBlank(restListPersonenParameters.bsn)) {
            listPersonenParameters.setBurgerservicenummers(List.of(restListPersonenParameters.bsn));
        }
        if (restListPersonenParameters.geboortedatum != null) {
            listPersonenParameters.setGeboorteDatum(restListPersonenParameters.geboortedatum);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.geslachtsnaam)) {
            listPersonenParameters.setNaamGeslachtsnaam(restListPersonenParameters.geslachtsnaam);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.voorvoegsel)) {
            listPersonenParameters.setNaamVoorvoegsel(restListPersonenParameters.voorvoegsel);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.voornamen)) {
            listPersonenParameters.setNaamVoornamen(restListPersonenParameters.voornamen);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.straat)) {
            listPersonenParameters.setVerblijfplaatsStraat(restListPersonenParameters.straat);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.gemeenteVanInschrijving)) {
            listPersonenParameters.setVerblijfplaatsGemeenteVanInschrijving(
                    restListPersonenParameters.gemeenteVanInschrijving);
        }
        if (StringUtils.isNotBlank(restListPersonenParameters.postcode)) {
            listPersonenParameters.setVerblijfplaatsPostcode(restListPersonenParameters.postcode);
        }
        if (restListPersonenParameters.huisnummer != null) {
            listPersonenParameters.setVerblijfplaatsHuisnummer(restListPersonenParameters.huisnummer);
        }
        return listPersonenParameters;
    }

    public List<RESTPersoon> convert(final List<IngeschrevenPersoonHal> ingeschrevenPersonen) {
        if (ingeschrevenPersonen == null) {
            return Collections.emptyList();
        }
        return ingeschrevenPersonen.stream().map(this::convert).toList();
    }

    private String convertToNaam(final NaamPersoon naam) {
        if (naam != null) {
            return KlantenUtil.nonBreaking(naam.getVoornamen(),
                                           naam.getVoorvoegsel(),
                                           naam.getGeslachtsnaam());
        } else {
            return ONBEKEND;
        }
    }

    private String convertToGeboortedatum(final Geboorte geboorte) {
        if (geboorte != null && geboorte.getDatum() != null && geboorte.getDatum().getDatum() != null) {
            return geboorte.getDatum().getDatum().toString();
        } else {
            return ONBEKEND;
        }
    }

    private String convertToInschrijfadres(final Verblijfplaats verblijfplaats) {
        if (verblijfplaats != null) {
            final String adres = KlantenUtil.nonBreaking(verblijfplaats.getStraat(),
                                                         Objects.toString(verblijfplaats.getHuisnummer(), null),
                                                         verblijfplaats.getHuisnummertoevoeging(),
                                                         verblijfplaats.getHuisletter());
            final String postcode = KlantenUtil.nonBreaking(verblijfplaats.getPostcode());
            final String woonplaats = KlantenUtil.nonBreaking(verblijfplaats.getWoonplaats());
            return KlantenUtil.breakingAfterCommas(adres,
                                                   postcode,
                                                   woonplaats);
        } else {
            return ONBEKEND;
        }
    }
}
