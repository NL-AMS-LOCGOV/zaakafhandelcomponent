/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

public class RESTZaakKenmerk {

    public String kenmerk;

    public String bron;

    public RESTZaakKenmerk(final String kenmerk, final String bron) {
        this.kenmerk = kenmerk;
        this.bron = bron;
    }
}
