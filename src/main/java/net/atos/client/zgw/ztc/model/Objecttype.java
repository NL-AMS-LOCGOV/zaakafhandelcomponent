/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(Objecttype.Adapter.class)
public enum Objecttype implements AbstractEnum<Objecttype> {

    ADRES("adres"),

    BESLUIT("besluit"),

    BUURT("buurt"),

    ENKELVOUDIG_DOCUMENT("enkelvoudig_document"),

    GEMEENTE("gemeente"),

    GEMEENTELIJKE_OPENBARE_RUIMTE("gemeentelijke_openbare_ruimte"),

    HUISHOUDEN("huishouden"),

    INRICHTINGSELEMENT("inrichtingselement"),

    KADASTRALE_ONROERENDE_ZAAK("kadastrale_onroerende_zaak"),

    KUNSTWERKDEEL("kunstwerkdeel"),

    MAATSCHAPPELIJKE_ACTIVITEIT("maatschappelijke_activiteit"),

    MEDEWERKER("medewerker"),

    NATUURLIJK_PERSOON("natuurlijk_persoon"),

    NIET_NATUURLIJK_PERSOON("niet_natuurlijk_persoon"),

    OPENBARE_RUIMTE("openbare_ruimte"),

    ORGANISATORISCHE_EENHEID("organisatorische_eenheid"),

    PAND("pand"),

    SPOORBAANDEEL("spoorbaandeel"),

    STATUS("status"),

    TERREINDEEL("terreindeel"),

    TERREIN_GEBOUWD_OBJECT("terrein_gebouwd_object"),

    VESTIGING("vestiging"),

    WATERDEEL("waterdeel"),

    WEGDEEL("wegdeel"),

    WIJK("wijk"),

    WOONPLAATS("woonplaats"),

    WOZ_DEELOBJECT("woz_deelobject"),

    WOZ_OBJECT("woz_object"),

    WOZ_WAARDE("woz_waarde"),

    ZAKELIJK_RECHT("zakelijk_recht"),

    OVERIGE("overige");

    private final String value;

    Objecttype(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Objecttype fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Objecttype> {

        @Override
        protected Objecttype[] getEnums() {
            return values();
        }
    }
}
