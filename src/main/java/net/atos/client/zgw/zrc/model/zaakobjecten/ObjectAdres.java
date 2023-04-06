/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectAdres
 */
public class ObjectAdres extends ObjectBAGObject {

    /**
     * Woonplaats naam
     * - maxLength: 100
     * - required
     */
    private String wplWoonplaatsNaam;

    /**
     * Een door het bevoegde gemeentelijke orgaan aan een OPENBARE RUIMTE toegekende benaming
     * - maxLength: 100
     * - required
     */
    private String gorOpenbareRuimteNaam;

    /**
     * Huisnummer
     * - maxSize: 99999
     * - required
     */
    private int huisnummer;

    /**
     * Huisletter
     * - maxLength: 1
     */
    private String huisletter;

    /**
     * Huisnummertoevoeging
     * - maxLength: 4
     */
    private String huisnummertoevoeging;

    /**
     * Postcode
     * - maxLength: 7
     */
    private String postcode;

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectAdres() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectAdres(final String identificatie, final String wplWoonplaatsNaam, final String gorOpenbareRuimteNaam, final int huisnummer,
            final String huisletter, final String huisnummertoevoeging,
            final String postcode) {
        super(identificatie);
        this.wplWoonplaatsNaam = wplWoonplaatsNaam;
        this.gorOpenbareRuimteNaam = gorOpenbareRuimteNaam;
        this.huisnummer = huisnummer;
        this.huisletter = huisletter;
        this.huisnummertoevoeging = huisnummertoevoeging;
        this.postcode = postcode;
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

    public int getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(final int huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getHuisletter() {
        return huisletter;
    }

    public void setHuisletter(final String huisletter) {
        this.huisletter = huisletter;
    }

    public String getHuisnummertoevoeging() {
        return huisnummertoevoeging;
    }

    public void setHuisnummertoevoeging(final String huisnummertoevoeging) {
        this.huisnummertoevoeging = huisnummertoevoeging;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }
}
