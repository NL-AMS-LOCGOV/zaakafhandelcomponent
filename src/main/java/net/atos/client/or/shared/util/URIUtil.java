/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared.util;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.net.URI;
import java.util.UUID;

/**
 *
 */
public final class URIUtil {

    public static UUID getUUID(final URI resourceURI) {
        final String resourceURIString = resourceURI.toString();
        return UUID.fromString(contains(resourceURIString, "/") ? substringAfterLast(resourceURIString, "/") : resourceURIString);
    }

    private URIUtil() {
    }
}
