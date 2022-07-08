/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.gebruikersvoorkeuren.model;


public class ZoekopdrachtListParameters {

    public ZoekopdrachtListParameters(final String lijstID, final String medewerkerID) {
        this.lijstID = lijstID;
        this.medewerkerID = medewerkerID;
    }

    private String lijstID;

    private String medewerkerID;

    public String getLijstID() {
        return lijstID;
    }

    public void setLijstID(final String lijstID) {
        this.lijstID = lijstID;
    }

    public String getMedewerkerID() {
        return medewerkerID;
    }

    public void setMedewerkerID(final String medewerkerID) {
        this.medewerkerID = medewerkerID;
    }
}
