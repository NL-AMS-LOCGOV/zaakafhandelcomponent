/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.net.URI;
import java.util.UUID;

/**
 *
 */
public final class UriUtil {

    private UriUtil() {
    }

    public static UUID uuidFromURI(final URI uri) {
        return UUID.fromString(extractUUID(uri.getPath()));
    }

    private static String extractUUID(final String path) {
        return contains(path, "/") ? substringAfterLast(path, "/") : path;
    }
}
