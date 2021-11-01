/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object.util;

import static net.atos.client.or.object.util.ObjectsClientHeadersFactory.generateToken;
import static net.atos.client.or.shared.util.Constant.APPLICATION_PROBLEM_JSON;

import java.net.URI;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import net.atos.client.or.object.ObjectsClient;
import net.atos.client.util.ClientFactory;

/**
 *
 */
public final class ObjectsInvocationBuilderFactory {

    private ObjectsInvocationBuilderFactory() {
    }

    public static Invocation.Builder create(final URI uri) {
        return ClientFactory.create().target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateToken())
                .header(ObjectsClient.ACCEPT_CRS, ObjectsClient.ACCEPT_CRS_VALUE)
                .header(ObjectsClient.CONTENT_CRS, ObjectsClient.ACCEPT_CRS_VALUE);
    }
}
