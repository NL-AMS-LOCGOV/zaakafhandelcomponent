/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.util;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.util.Constant.APPLICATION_PROBLEM_JSON;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

public final class AcceptHeaderBugWorkaroundUtil {

    private AcceptHeaderBugWorkaroundUtil() {}

    /**
     * Allthough clients are annotated with @Produces({APPLICATION_JSON, APPLICATION_PROBLEM_JSON}), only the first Media Type gets in the header.
     * This method provides a workaround for this bug by adding the APPLICATION_PROBLEM_JSON.
     *
     * @param clientOutgoingHeaders Headers to fix
     */
    public static void fix(final MultivaluedMap<String, String> clientOutgoingHeaders) {
        if (clientOutgoingHeaders.getFirst(HttpHeaders.ACCEPT).equals(APPLICATION_JSON)) {
            clientOutgoingHeaders.putSingle(HttpHeaders.ACCEPT, APPLICATION_JSON + ", " + APPLICATION_PROBLEM_JSON);
        }
    }

}
