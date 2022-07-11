/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.gebruikersvoorkeuren.model;


public class ZoekopdrachtListParameters {

    private final Werklijst lijstID;

    private final String medewerkerID;

    public ZoekopdrachtListParameters(final Werklijst lijstID, final String medewerkerID) {
        this.lijstID = lijstID;
        this.medewerkerID = medewerkerID;
    }


    public Werklijst getLijstID() {
        return lijstID;
    }

    public String getMedewerkerID() {
        return medewerkerID;
    }

}
