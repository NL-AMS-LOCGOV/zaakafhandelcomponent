/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class Medewerker {

    /**
     * Een korte unieke aanduiding van de MEDEWERKER.
     * - maxLength: 24
     */
    private String identificatie;

    /**
     * De achternaam zoals de MEDEWERKER die in het dagelijkse verkeer gebruikt.
     * - maxLength: 200
     */
    private String achternaam;

    /**
     * De verzameling letters die gevormd wordt door de eerste letter van alle in volgorde voorkomende voornamen.
     * - maxLength: 20
     */
    private String voorletters;

    /**
     * Dat deel van de geslachtsnaam dat voorkomt in Tabel 36 (GBA), voorvoegseltabel, en door een spatie van de geslachtsnaam is
     * - maxLength: 10
     */
    private String voorvoegselAchternaam;

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(final String achternaam) {
        this.achternaam = achternaam;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public void setVoorletters(final String voorletters) {
        this.voorletters = voorletters;
    }

    public String getVoorvoegselAchternaam() {
        return voorvoegselAchternaam;
    }

    public void setVoorvoegselAchternaam(final String voorvoegselAchternaam) {
        this.voorvoegselAchternaam = voorvoegselAchternaam;
    }
}
