/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import java.net.URI;

public enum SignaleringSubject {

    ZAAK,
    TAAK,
    INFORMATIEOBJECT;

    public static final class Link {
        public final String id;

        public final String naam;

        public final String url;

        public Link(final String id, final String naam, final URI url) {
            this.id = id;
            this.naam = naam;
            this.url = url.toString();
        }
    }
}
