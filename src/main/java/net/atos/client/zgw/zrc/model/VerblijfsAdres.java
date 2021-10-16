/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class VerblijfsAdres {
    /**
     * De unieke identificatie van het OBJECT
     * - required
     * -maxLength: 100
     */
    private String aoaIdentificatie;

    /**
     * - required
     * - maxLength: 80
     */
    private String wplWoonplaatsNaam;

    /**
     * Een door het bevoegde gemeentelijke orgaan aan een OPENBARE RUIMTE toegekende benaming
     * - required
     * - maxLength: 80
     */
    private String gorOpenbareRuimteNaam;

    /**
     * - maxLength: 7
     */
    private String aoaPostcode;

    /**
     * - required
     * - maximum: 99999
     * - minimum: 0
     */
    private Integer aoaHuisnummer;

    /**
     * - maxLength: 1
     */
    private String aoaHuisletter;

    /**
     * - maxLength: 4
     */
    private String aoaHuisnummertoevoeging;

    /**
     * maxLength: 1000
     */
    private String inpLocatiebeschrijving;

    public String getAoaIdentificatie() {
        return aoaIdentificatie;
    }

    public void setAoaIdentificatie(final String aoaIdentificatie) {
        this.aoaIdentificatie = aoaIdentificatie;
    }

    public String getWplWoonplaatsNaam() {
        return wplWoonplaatsNaam;
    }

    public void setWplWoonplaatsNaam(final String wplWoonplaatsNaam) {
        this.wplWoonplaatsNaam = wplWoonplaatsNaam;
    }

    public String getGorOpenbareRuimteNaam() {
        return gorOpenbareRuimteNaam;
    }

    public void setGorOpenbareRuimteNaam(final String gorOpenbareRuimteNaam) {
        this.gorOpenbareRuimteNaam = gorOpenbareRuimteNaam;
    }

    public String getAoaPostcode() {
        return aoaPostcode;
    }

    public void setAoaPostcode(final String aoaPostcode) {
        this.aoaPostcode = aoaPostcode;
    }

    public Integer getAoaHuisnummer() {
        return aoaHuisnummer;
    }

    public void setAoaHuisnummer(final Integer aoaHuisnummer) {
        this.aoaHuisnummer = aoaHuisnummer;
    }

    public String getAoaHuisletter() {
        return aoaHuisletter;
    }

    public void setAoaHuisletter(final String aoaHuisletter) {
        this.aoaHuisletter = aoaHuisletter;
    }

    public String getAoaHuisnummertoevoeging() {
        return aoaHuisnummertoevoeging;
    }

    public void setAoaHuisnummertoevoeging(final String aoaHuisnummertoevoeging) {
        this.aoaHuisnummertoevoeging = aoaHuisnummertoevoeging;
    }

    public String getInpLocatiebeschrijving() {
        return inpLocatiebeschrijving;
    }

    public void setInpLocatiebeschrijving(final String inpLocatiebeschrijving) {
        this.inpLocatiebeschrijving = inpLocatiebeschrijving;
    }
}
