/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.zac.app.klanten.model.bedrijven.RESTBedrijf;
import net.atos.zac.app.klanten.model.bedrijven.RESTListBedrijvenParameters;

public class RESTBedrijfConverter {

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
        if (bedrijf == null) {
            return new RESTBedrijf();
        }
        RESTBedrijf restBedrijf = new RESTBedrijf();
        restBedrijf.kvkNummer = bedrijf.getKvkNummer();
        restBedrijf.vestigingsnummer = bedrijf.getVestigingsnummer();
        restBedrijf.handelsnaam = bedrijf.getHandelsnaam();
        restBedrijf.postcode = bedrijf.getPostcode();
        restBedrijf.rsin = bedrijf.getRsin();
        restBedrijf.type = bedrijf.getType().toUpperCase(Locale.getDefault());
        restBedrijf.adres = convertAdres(bedrijf);
        return restBedrijf;
    }

    private String convertAdres(final ResultaatItem bedrijf) {
        final String adres = Stream.of(bedrijf.getStraatnaam(),
                                       Objects.toString(bedrijf.getHuisnummer(), null),
                                       bedrijf.getHuisnummerToevoeging())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
        return StringUtils.isNotBlank(adres) && StringUtils.isNotBlank(bedrijf.getPlaats()) ? "%s, %s".formatted(adres,
                                                                                                                 bedrijf.getPlaats()) : bedrijf.getPlaats();
    }
}
