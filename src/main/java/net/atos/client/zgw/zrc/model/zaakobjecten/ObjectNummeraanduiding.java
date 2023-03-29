/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectNummeraanduiding
 */
public class ObjectNummeraanduiding extends ObjectBAGObject {

    private String huisnummerWeergave;

    private int huisnummer;

    private String huisletter;

    private String huisnummertoevoeging;

    private String postcode;

    private String typeAdresseerbaarObject;

    private String status;

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectNummeraanduiding() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectNummeraanduiding(
            final String identificatie,
            final int huisnummer,
            final String huisletter,
            final String huisnummertoevoeging, final String postcode,
            final String typeAdresseerbaarObject, final String status) {
        super(identificatie);
        this.huisnummer = huisnummer;
        this.huisletter = huisletter;
        this.huisnummertoevoeging = huisnummertoevoeging;
        this.postcode = postcode;
        this.typeAdresseerbaarObject = typeAdresseerbaarObject;
        this.status = status;
    }

    public String getHuisnummerWeergave() {
        return huisnummerWeergave;
    }

    public void setHuisnummerWeergave(final String huisnummerWeergave) {
        this.huisnummerWeergave = huisnummerWeergave;
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

    public String getTypeAdresseerbaarObject() {
        return typeAdresseerbaarObject;
    }

    public void setTypeAdresseerbaarObject(final String typeAdresseerbaarObject) {
        this.typeAdresseerbaarObject = typeAdresseerbaarObject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
