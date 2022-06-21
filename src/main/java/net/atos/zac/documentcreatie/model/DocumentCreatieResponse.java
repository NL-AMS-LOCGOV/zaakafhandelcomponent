/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import java.net.URI;

public class DocumentCreatieResponse {

    private final URI redirectUrl;

    private final String message;

    public DocumentCreatieResponse(final URI redirectUrl) {
        this.redirectUrl = redirectUrl;
        message = null;
    }

    public DocumentCreatieResponse(final String message) {
        this.message = message;
        redirectUrl = null;
    }

    public URI getRedirectUrl() {
        return redirectUrl;
    }

    public String getMessage() {
        return message;
    }
}
