/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.net.URI;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.shared.model.ObjectType;
/**
 *
 */
public final class URIUtil {

    public static boolean equals(final URI resourceURI1, final URI resourceURI2) {
        return StringUtils.equals(parseUUIDAsStringFromResourceURI(resourceURI1), parseUUIDAsStringFromResourceURI(resourceURI2));
    }

    public static UUID parseUUIDFromResourceURI(final URI resourceURI) {
        return UUID.fromString(parseUUIDAsStringFromResourceURI(resourceURI));
    }

    public static ObjectType getObjectTypeFromResourceURL(final String url) {
        if (contains(url, "zaken/api/v1/rollen/")) {
            return ObjectType.ROL;
        } else if (contains(url, "zaken/api/v1/zaken/")) {
            return ObjectType.ZAAK;
        } else if (contains(url, "zaken/api/v1/statussen/")) {
            return ObjectType.STATUS;
        } else if (contains(url, "zaken/api/v1/resultaten/")) {
            return ObjectType.RESULTAAT;
        } else if (contains(url, "/zaken/api/v1/zaakinformatieobjecten")) {
            return ObjectType.ZAAK_INFORMATIEOBJECT;
        } else if (contains(url, "documenten/api/v1/enkelvoudiginformatieobjecten/")) {
            return ObjectType.ENKELVOUDIG_INFORMATIEOBJECT;
        } else if (contains(url, "/documenten/api/v1/gebruiksrechten")) {
            return ObjectType.GEBRUIKSRECHTEN;
        } else {
            throw new RuntimeException(String.format("URL '%s' wordt niet ondersteund", url));
        }
    }

    private static String parseUUIDAsStringFromResourceURI(final URI resourceURI) {
        final String resourceURIString = resourceURI.toString();
        return contains(resourceURIString, "/") ? substringAfterLast(resourceURIString, "/") : resourceURIString;
    }

    private URIUtil() {
    }
}
