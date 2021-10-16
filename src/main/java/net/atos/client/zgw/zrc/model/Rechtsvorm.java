/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(Rechtsvorm.Adapter.class)
public enum Rechtsvorm implements AbstractEnum<Rechtsvorm> {

    BESLOTEN_VENNOOTSCHAP("besloten_vennootschap"),
    COOPERATIE_EUROPEES_ECONOMISCHE_SAMENWERKING("cooperatie_europees_economische_samenwerking"),
    EUROPESE_COOPERATIEVE_VENOOTSCHAP("europese_cooperatieve_venootschap"),
    EUROPESE_NAAMLOZE_VENNOOTSCHAP("europese_naamloze_vennootschap"),
    KERKELIJKE_ORGANISATIE("kerkelijke_organisatie"),
    NAAMLOZE_VENNOOTSCHAP("naamloze_vennootschap"),
    ONDERLINGE_WAARBORG_MAATSCHAPPIJ("onderlinge_waarborg_maatschappij"),
    OVERIG_PRIVAATRECHTELIJKE_RECHTSPERSOON("overig_privaatrechtelijke_rechtspersoon"),
    STICHTING("stichting"),
    VERENIGING("vereniging"),
    VERENIGING_VAN_EIGENAARS("vereniging_van_eigenaars"),
    PUBLIEKRECHTELIJKE_RECHTSPERSOON("publiekrechtelijke_rechtspersoon"),
    VENNOOTSCHAP_ONDER_FIRMA("vennootschap_onder_firma"),
    MAATSCHAP("maatschap"),
    REDERIJ("rederij"),
    COMMANDITAIRE_VENNOOTSCHAP("commanditaire_vennootschap"),
    KAPITAALVENNOOTSCHAP_BINNEN_EER("kapitaalvennootschap_binnen_eer"),
    OVERIGE_BUITENLANDSE_RECHTSPERSOON_VENNOOTSCHAP("overige_buitenlandse_rechtspersoon_vennootschap"),
    KAPITAALVENNOOTSCHAP_BUITEN_EER("kapitaalvennootschap_buiten_eer");

    private final String value;

    Rechtsvorm(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Rechtsvorm fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Rechtsvorm> {

        @Override
        protected Rechtsvorm[] getEnums() {
            return values();
        }
    }
}
