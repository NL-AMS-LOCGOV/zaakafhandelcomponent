/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

public enum SignaleringTarget {

    GROUP,
    USER;

    public static final class Mail {

        public final String naam;

        public final String emailadres;

        public Mail(final String naam, final String emailadres) {
            this.naam = naam;
            this.emailadres = emailadres;
        }
    }
}
