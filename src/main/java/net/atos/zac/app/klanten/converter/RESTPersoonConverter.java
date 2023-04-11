/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import static net.atos.zac.util.StringUtil.NON_BREAKING_SPACE;
import static net.atos.zac.util.StringUtil.ONBEKEND;
import static net.atos.zac.util.StringUtil.joinNonBlankWith;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.replace;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.atos.client.brp.model.AbstractDatum;
import net.atos.client.brp.model.AbstractVerblijfplaats;
import net.atos.client.brp.model.Adres;
import net.atos.client.brp.model.AdresseringBeperkt;
import net.atos.client.brp.model.DatumOnbekend;
import net.atos.client.brp.model.JaarDatum;
import net.atos.client.brp.model.JaarMaandDatum;
import net.atos.client.brp.model.PersonenQuery;
import net.atos.client.brp.model.PersonenQueryResponse;
import net.atos.client.brp.model.Persoon;
import net.atos.client.brp.model.PersoonBeperkt;
import net.atos.client.brp.model.RaadpleegMetBurgerservicenummer;
import net.atos.client.brp.model.RaadpleegMetBurgerservicenummerResponse;
import net.atos.client.brp.model.VerblijfadresBinnenland;
import net.atos.client.brp.model.VerblijfadresBuitenland;
import net.atos.client.brp.model.VerblijfplaatsBuitenland;
import net.atos.client.brp.model.VerblijfplaatsOnbekend;
import net.atos.client.brp.model.VolledigeDatum;
import net.atos.client.brp.model.Waardetabel;
import net.atos.client.brp.model.ZoekMetGeslachtsnaamEnGeboortedatum;
import net.atos.client.brp.model.ZoekMetGeslachtsnaamEnGeboortedatumResponse;
import net.atos.client.brp.model.ZoekMetNaamEnGemeenteVanInschrijving;
import net.atos.client.brp.model.ZoekMetNaamEnGemeenteVanInschrijvingResponse;
import net.atos.client.brp.model.ZoekMetNummeraanduidingIdentificatieResponse;
import net.atos.client.brp.model.ZoekMetPostcodeEnHuisnummer;
import net.atos.client.brp.model.ZoekMetPostcodeEnHuisnummerResponse;
import net.atos.client.brp.model.ZoekMetStraatHuisnummerEnGemeenteVanInschrijving;
import net.atos.client.brp.model.ZoekMetStraatHuisnummerEnGemeenteVanInschrijvingResponse;
import net.atos.zac.app.klanten.model.personen.RESTListPersonenParameters;
import net.atos.zac.app.klanten.model.personen.RESTPersoon;

public class RESTPersoonConverter {

    public List<RESTPersoon> convertPersonen(final List<Persoon> personen) {
        return personen.stream().map(this::convertPersoon).toList();
    }

    public List<RESTPersoon> convertPersonenBeperkt(final List<PersoonBeperkt> personen) {
        return personen.stream().map(this::convertPersoonBeperkt).toList();
    }

    public RESTPersoon convertPersoon(final Persoon persoon) {
        final RESTPersoon restPersoon = new RESTPersoon();
        restPersoon.bsn = persoon.getBurgerservicenummer();
        if (persoon.getGeslacht() != null) {
            restPersoon.geslacht = convertGeslacht(persoon.getGeslacht());
        }
        if (persoon.getNaam() != null) {
            restPersoon.naam = persoon.getNaam().getVolledigeNaam();
        }
        if (persoon.getGeboorte() != null) {
            restPersoon.geboortedatum = convertGeboortedatum(persoon.getGeboorte().getDatum());
        }
        if (persoon.getVerblijfplaats() != null) {
            restPersoon.verblijfplaats = convertVerblijfplaats(persoon.getVerblijfplaats());
        }
        return restPersoon;
    }

    public RESTPersoon convertPersoonBeperkt(final PersoonBeperkt persoon) {
        final RESTPersoon restPersoon = new RESTPersoon();
        restPersoon.bsn = persoon.getBurgerservicenummer();
        if (persoon.getGeslacht() != null) {
            restPersoon.geslacht = convertGeslacht(persoon.getGeslacht());
        }
        if (persoon.getNaam() != null) {
            restPersoon.naam = persoon.getNaam().getVolledigeNaam();
        }
        if (persoon.getGeboorte() != null) {
            restPersoon.geboortedatum = convertGeboortedatum(persoon.getGeboorte().getDatum());
        }
        if (persoon.getAdressering() != null) {
            final AdresseringBeperkt adressering = persoon.getAdressering();
            restPersoon.verblijfplaats = joinNonBlankWith(", ",
                                                          adressering.getAdresregel1(),
                                                          adressering.getAdresregel2(),
                                                          adressering.getAdresregel3());
        }
        return restPersoon;
    }

    public PersonenQuery convertToPersonenQuery(final RESTListPersonenParameters parameters) {
        if (isNotBlank(parameters.bsn)) {
            final var query = new RaadpleegMetBurgerservicenummer();
            query.addBurgerservicenummerItem(parameters.bsn);
            return query;
        }
        if (isNotBlank(parameters.geslachtsnaam) && parameters.geboortedatum != null) {
            final var query = new ZoekMetGeslachtsnaamEnGeboortedatum();
            query.setGeslachtsnaam(parameters.geslachtsnaam);
            query.setGeboortedatum(parameters.geboortedatum);
            query.setVoornamen(parameters.voornamen);
            query.setVoorvoegsel(parameters.voorvoegsel);
            return query;
        }
        if (isNotBlank(parameters.geslachtsnaam) && isNotBlank(parameters.voornamen) &&
                isNotBlank(parameters.gemeenteVanInschrijving)) {
            final var query = new ZoekMetNaamEnGemeenteVanInschrijving();
            query.setGeslachtsnaam(parameters.geslachtsnaam);
            query.setVoornamen(parameters.voornamen);
            query.setGemeenteVanInschrijving(parameters.gemeenteVanInschrijving);
            query.setVoorvoegsel(parameters.voorvoegsel);
            return query;
        }
        if (isNotBlank(parameters.postcode) && parameters.huisnummer != null) {
            final var query = new ZoekMetPostcodeEnHuisnummer();
            query.setPostcode(parameters.postcode);
            query.setHuisnummer(parameters.huisnummer);
            return query;
        }
        if (isNotBlank(parameters.straat) && parameters.huisnummer != null
                && isNotBlank(parameters.gemeenteVanInschrijving)) {
            final var query = new ZoekMetStraatHuisnummerEnGemeenteVanInschrijving();
            query.setStraat(parameters.straat);
            query.setHuisnummer(parameters.huisnummer);
            query.setGemeenteVanInschrijving(parameters.gemeenteVanInschrijving);
            return query;
        }
        throw new IllegalArgumentException("Ongeldige combinatie van zoek parameters");
    }

    public List<RESTPersoon> convertFromPersonenQueryResponse(final PersonenQueryResponse personenQueryResponse) {
        return switch (personenQueryResponse) {
            case RaadpleegMetBurgerservicenummerResponse response -> convertPersonen(response.getPersonen());
            case ZoekMetGeslachtsnaamEnGeboortedatumResponse response -> convertPersonenBeperkt(response.getPersonen());
            case ZoekMetNaamEnGemeenteVanInschrijvingResponse response ->
                    convertPersonenBeperkt(response.getPersonen());
            case ZoekMetNummeraanduidingIdentificatieResponse response ->
                    convertPersonenBeperkt(response.getPersonen());
            case ZoekMetPostcodeEnHuisnummerResponse response -> convertPersonenBeperkt(response.getPersonen());
            case ZoekMetStraatHuisnummerEnGemeenteVanInschrijvingResponse response ->
                    convertPersonenBeperkt(response.getPersonen());
            default -> Collections.emptyList();
        };
    }

    private String convertGeslacht(final Waardetabel geslacht) {
        return isNotBlank(geslacht.getOmschrijving()) ? geslacht.getOmschrijving() : geslacht.getCode();
    }

    private String convertGeboortedatum(final AbstractDatum abstractDatum) {
        return switch (abstractDatum) {
            case VolledigeDatum volledigeDatum -> volledigeDatum.getDatum().toString();
            case JaarMaandDatum jaarMaandDatum -> "%d2-%d4".formatted(jaarMaandDatum.getMaand(),
                                                                      jaarMaandDatum.getJaar());
            case JaarDatum jaarDatum -> "%d4".formatted(jaarDatum.getJaar());
            case DatumOnbekend datumOnbekend -> ONBEKEND;
            default -> null;
        };
    }

    private String convertVerblijfplaats(final AbstractVerblijfplaats abstractVerblijfplaats) {
        return switch (abstractVerblijfplaats) {
            case Adres adres && adres.getVerblijfadres() != null ->
                    convertVerblijfadresBinnenland(adres.getVerblijfadres());
            case VerblijfplaatsBuitenland verblijfplaatsBuitenland && verblijfplaatsBuitenland.getVerblijfadres() != null ->
                    convertVerblijfadresBuitenland(verblijfplaatsBuitenland.getVerblijfadres());
            case VerblijfplaatsOnbekend verblijfplaatsOnbekend -> ONBEKEND;
            default -> null;
        };
    }

    private String convertVerblijfadresBinnenland(final VerblijfadresBinnenland verblijfadresBinnenland) {
        final String adres = replace(joinNonBlankWith(NON_BREAKING_SPACE,
                                                      verblijfadresBinnenland.getOfficieleStraatnaam(),
                                                      Objects.toString(verblijfadresBinnenland.getHuisnummer(), null),
                                                      verblijfadresBinnenland.getHuisnummertoevoeging(),
                                                      verblijfadresBinnenland.getHuisletter()),
                                     SPACE, NON_BREAKING_SPACE);
        final String postcode = replace(verblijfadresBinnenland.getPostcode(), SPACE, NON_BREAKING_SPACE);
        final String woonplaats = replace(verblijfadresBinnenland.getWoonplaats(), SPACE, NON_BREAKING_SPACE);
        return joinNonBlankWith(", ", adres, postcode, woonplaats);
    }

    private String convertVerblijfadresBuitenland(VerblijfadresBuitenland verblijfadresBuitenland) {
        return joinNonBlankWith(", ", verblijfadresBuitenland.getRegel1(),
                                verblijfadresBuitenland.getRegel2(),
                                verblijfadresBuitenland.getRegel3());
    }

}
