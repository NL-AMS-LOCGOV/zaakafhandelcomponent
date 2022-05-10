/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public class ZoekParameters {

    private String tekst;

    private int rows;

    private int start;

    public String getTekst() {
        return tekst;
    }

    public void setTekst(final String tekst) {
        this.tekst = tekst;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(final int rows) {
        this.rows = rows;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }
}
