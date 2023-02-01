/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.vestigingsprofiel.model.Vestiging;
import net.atos.client.kvk.zoeken.model.Resultaat;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.zac.app.klanten.KlantenUtil;
import net.atos.zac.app.klanten.model.bedrijven.RESTBedrijf;
import net.atos.zac.app.klanten.model.bedrijven.RESTListBedrijvenParameters;

public class RESTBedrijfConverter {

    final static String ADRES_ONBEKEND = "-";

    public KVKZoekenParameters convert(final RESTListBedrijvenParameters restListParameters) {
        final KVKZoekenParameters zoekenParameters = new KVKZoekenParameters();
        if (StringUtils.isNotBlank(restListParameters.kvkNummer)) {
            zoekenParameters.setKvkNummer(restListParameters.kvkNummer);
        }
        if (StringUtils.isNotBlank(restListParameters.vestigingsnummer)) {
            zoekenParameters.setVestigingsnummer(restListParameters.vestigingsnummer);
        }
        if (StringUtils.isNotBlank(restListParameters.rsin)) {
            zoekenParameters.setRsin(restListParameters.rsin);
        }
        if (StringUtils.isNotBlank(restListParameters.handelsnaam)) {
            zoekenParameters.setHandelsnaam(restListParameters.handelsnaam);
        }
        if (restListParameters.type != null) {
            zoekenParameters.setType(restListParameters.type.getType());
        }
        if (StringUtils.isNotBlank(restListParameters.postcode)) {
            zoekenParameters.setPostcode(restListParameters.postcode);
        }
        if (restListParameters.huisnummer != null) {
            zoekenParameters.setHuisnummer(String.valueOf(restListParameters.huisnummer));
        }
        return zoekenParameters;
    }

    public Stream<RESTBedrijf> convert(final Resultaat resultaat) {
        if (CollectionUtils.isEmpty(resultaat.getResultaten())) {
            return Stream.empty();
        }
        return resultaat.getResultaten().stream().map(this::convert);
    }

    public RESTBedrijf convert(final ResultaatItem bedrijf) {
        final RESTBedrijf restBedrijf = new RESTBedrijf();
        restBedrijf.kvkNummer = bedrijf.getKvkNummer();
        restBedrijf.vestigingsnummer = bedrijf.getVestigingsnummer();
        restBedrijf.handelsnaam = convertToNaam(bedrijf);
        restBedrijf.postcode = bedrijf.getPostcode();
        restBedrijf.rsin = bedrijf.getRsin();
        restBedrijf.type = bedrijf.getType().toUpperCase(Locale.getDefault());
        restBedrijf.adres = convertAdres(bedrijf);
        return restBedrijf;
    }

    public RESTBedrijf convert(final Vestiging bedrijf) {
        final RESTBedrijf restBedrijf = new RESTBedrijf();
        restBedrijf.kvkNummer = bedrijf.getKvkNummer();
        restBedrijf.vestigingsnummer = bedrijf.getVestigingsnummer();
        restBedrijf.handelsnaam = convertToNaam(bedrijf);
        restBedrijf.postcode = convertPostcode(bedrijf);
        restBedrijf.rsin = bedrijf.getRsin();
        restBedrijf.type = convertType(bedrijf);
        restBedrijf.adres = convertAdres(bedrijf);
        return restBedrijf;
    }

    private String convertToNaam(final ResultaatItem bedrijf) {
        return KlantenUtil.nonBreaking(bedrijf.getHandelsnaam());
    }

    private String convertToNaam(final Vestiging bedrijf) {
        return KlantenUtil.nonBreaking(bedrijf.getEersteHandelsnaam());
    }

    private String convertAdres(final ResultaatItem bedrijf) {
        final String adres = KlantenUtil.nonBreaking(bedrijf.getStraatnaam(),
                                                     Objects.toString(bedrijf.getHuisnummer(), null),
                                                     bedrijf.getHuisnummerToevoeging());
        final String postcode = KlantenUtil.nonBreaking(bedrijf.getPostcode());
        final String woonplaats = KlantenUtil.nonBreaking(bedrijf.getPlaats());
        return KlantenUtil.breakingAfterCommas(adres,
                                               postcode,
                                               woonplaats);
    }

    private String convertAdres(final Vestiging bedrijf) {
        return bedrijf.getAdressen().stream()
                .findFirst()
                .map(locatie -> {
                    final String adres = KlantenUtil.nonBreaking(locatie.getStraatnaam(),
                                                                 Objects.toString(locatie.getHuisnummer(), null),
                                                                 locatie.getHuisnummerToevoeging());
                    final String postcode = KlantenUtil.nonBreaking(locatie.getPostcode());
                    final String woonplaats = KlantenUtil.nonBreaking(locatie.getPlaats());
                    return KlantenUtil.breakingAfterCommas(adres,
                                                           postcode,
                                                           woonplaats);
                })
                .orElse(ADRES_ONBEKEND);
    }

    private String convertPostcode(final Vestiging bedrijf) {
        return bedrijf.getAdressen().stream()
                .findFirst()
                .map(locatie -> KlantenUtil.nonBreaking(locatie.getPostcode()))
                .orElse(ADRES_ONBEKEND);
    }

    private String convertType(final Vestiging bedrijf) {
        return bedrijf.getAdressen().stream()
                .findFirst()
                .map(locatie -> KlantenUtil.nonBreaking(locatie.getType()))
                .orElse(ADRES_ONBEKEND);
    }
}
