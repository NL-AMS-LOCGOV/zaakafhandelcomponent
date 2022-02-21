/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.personen.converter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.brp.model.Geboorte;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.ListPersonenParameters;
import net.atos.client.brp.model.NaamPersoon;
import net.atos.client.brp.model.Verblijfplaats;
import net.atos.zac.app.personen.model.RESTListPersonenParameters;
import net.atos.zac.app.personen.model.RESTPersoon;
import net.atos.zac.app.personen.model.RESTPersoonOverzicht;

public class RESTPersoonConverter {

    public static final String FIELDS_PERSOON_OVERZICHT = "naam.voornamen,naam.voorvoegsel,naam.geslachtsnaam,geboorte.datum.datum," +
            "verblijfplaats.straat,verblijfplaats.huisnummer,verblijfplaats.huisnummertoevoeging,verblijfplaats.huisletter,verblijfplaats.woonplaats";


    private static final String ONBEKEND = "<onbekend>";

    public RESTPersoonOverzicht convertToPersoonOverzicht(final IngeschrevenPersoonHal persoon) {
        final RESTPersoonOverzicht persoonOverzicht = new RESTPersoonOverzicht();
        if (persoon != null) {
            persoonOverzicht.naam = convertToNaam(persoon.getNaam());
            persoonOverzicht.geboortedatum = convertToGeboortedatum(persoon.getGeboorte());
            persoonOverzicht.inschrijfadres = convertToInschrijfadres(persoon.getVerblijfplaats());
        } else {
            persoonOverzicht.naam = ONBEKEND;
            persoonOverzicht.geboortedatum = ONBEKEND;
            persoonOverzicht.inschrijfadres = ONBEKEND;
        }
        return persoonOverzicht;
    }

    public ListPersonenParameters convert(final RESTListPersonenParameters persoonZoekParameters) {
        final ListPersonenParameters listPersonenParameters = new ListPersonenParameters();
        if (persoonZoekParameters.geboortedatum != null) {
            listPersonenParameters.setGeboorteDatum(persoonZoekParameters.geboortedatum);
        } else {
            listPersonenParameters.setVerblijfplaatsGemeenteVanInschrijving(persoonZoekParameters.gemeenteVanInschrijving);
        }
        listPersonenParameters.setNaamGeslachtsnaam(persoonZoekParameters.geslachtsnaam);
        return listPersonenParameters;
    }

    public List<RESTPersoon> convert(final List<IngeschrevenPersoonHal> ingeschrevenPersonen) {
        return ingeschrevenPersonen.stream().map(this::convertToPersoon).toList();
    }

    public RESTPersoon convertToPersoon(final IngeschrevenPersoonHal ingeschrevenPersoon) {
        final RESTPersoon persoon = new RESTPersoon();
        persoon.bsn = ingeschrevenPersoon.getBurgerservicenummer();
        persoon.geslacht = ingeschrevenPersoon.getGeslachtsaanduiding().toString();
        persoon.naam = convertToNaam(ingeschrevenPersoon.getNaam());
        persoon.geboortedatum = convertToGeboortedatum(ingeschrevenPersoon.getGeboorte());
        persoon.inschrijfadres = convertToInschrijfadres(ingeschrevenPersoon.getVerblijfplaats());
        return persoon;
    }

    private String convertToNaam(final NaamPersoon naam) {
        if (naam != null) {
            return Stream.of(naam.getVoornamen(), naam.getVoorvoegsel(), naam.getGeslachtsnaam())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
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
            return Stream.of(verblijfplaats.getStraat(),
                             Objects.toString(verblijfplaats.getHuisnummer(), null),
                             verblijfplaats.getHuisnummertoevoeging(),
                             verblijfplaats.getHuisletter(),
                             verblijfplaats.getWoonplaats())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
        } else {
            return ONBEKEND;
        }
    }
}
