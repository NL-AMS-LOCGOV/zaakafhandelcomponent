/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import static net.atos.client.or.object.util.ObjectsClientHeadersFactory.generateToken;
import static net.atos.client.or.shared.util.Constant.APPLICATION_PROBLEM_JSON;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 */
public final class ObjectsClientFactory {

    private static Client client;

    private ObjectsClientFactory() {
    }

    public static Invocation.Builder getInvocationBuilder(final URI uri) {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        return client.target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateToken())
                .header(ObjectsClient.ACCEPT_CRS, ObjectsClient.ACCEPT_CRS_VALUE)
                .header(ObjectsClient.CONTENT_CRS, ObjectsClient.ACCEPT_CRS_VALUE);
    }
}
