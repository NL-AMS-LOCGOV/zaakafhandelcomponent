/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object.util;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.or.shared.util.Constant.APPLICATION_PROBLEM_JSON;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

/**
 *
 */
public class ObjectsClientHeadersFactory implements ClientHeadersFactory {

    private static final String TOKEN = ConfigProvider.getConfig().getValue("objects.api.token", String.class);

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(HttpHeaders.AUTHORIZATION, generateToken());
        acceptHeaderBugWorkaround(clientOutgoingHeaders);
        return clientOutgoingHeaders;
    }

    public static String generateToken() {
        return String.format("Token %s", TOKEN);
    }

    private static void acceptHeaderBugWorkaround(final MultivaluedMap<String, String> clientOutgoingHeaders) {
        // Allthough clients are annotated with @Produces({APPLICATION_JSON, APPLICATION_PROBLEM_JSON}), only the first MedIa Type is used.
        // This method provides a workaround for this bug.
        final String accept = "Accept";
        if (clientOutgoingHeaders.getFirst(accept).equals(APPLICATION_JSON)) {
            clientOutgoingHeaders.putSingle(accept, APPLICATION_JSON + ", " + APPLICATION_PROBLEM_JSON);
        }
    }
}
