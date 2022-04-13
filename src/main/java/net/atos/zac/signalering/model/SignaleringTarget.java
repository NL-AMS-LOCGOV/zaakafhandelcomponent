/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

public enum SignaleringTarget {

    GROEP,
    MEDEWERKER;

    public static final class Mail {
        public final String naam;

        public final String adres;

        public Mail(final String naam, final String adres) {
            this.naam = naam;
            this.adres = adres;
        }
    }
}
