/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class SubVerblijfBuitenland {

    /**
     * De code, behorende bij de landnaam, zoals opgenomen in de Land/Gebied-tabel van de BRP.
     * - required
     * - maxLength: 4
     */
    private String lndLandcode;

    /**
     * De naam van het land, zoals opgenomen in de Land/Gebied-tabel van de BRP.
     * - required
     * - maxLength: 40
     */
    private String lndLandnaam;

    /**
     * - maxLength: 35
     */
    private String subAdresBuitenland_1;

    /**
     * - maxLength: 35
     */
    private String subAdresBuitenland_2;

    /**
     * - maxLength: 35
     */
    private String subAdresBuitenland_3;

    public String getLndLandcode() {
        return lndLandcode;
    }

    public void setLndLandcode(final String lndLandcode) {
        this.lndLandcode = lndLandcode;
    }

    public String getLndLandnaam() {
        return lndLandnaam;
    }

    public void setLndLandnaam(final String lndLandnaam) {
        this.lndLandnaam = lndLandnaam;
    }

    public String getSubAdresBuitenland_1() {
        return subAdresBuitenland_1;
    }

    public void setSubAdresBuitenland_1(final String subAdresBuitenland_1) {
        this.subAdresBuitenland_1 = subAdresBuitenland_1;
    }

    public String getSubAdresBuitenland_2() {
        return subAdresBuitenland_2;
    }

    public void setSubAdresBuitenland_2(final String subAdresBuitenland_2) {
        this.subAdresBuitenland_2 = subAdresBuitenland_2;
    }

    public String getSubAdresBuitenland_3() {
        return subAdresBuitenland_3;
    }

    public void setSubAdresBuitenland_3(final String subAdresBuitenland_3) {
        this.subAdresBuitenland_3 = subAdresBuitenland_3;
    }
}
