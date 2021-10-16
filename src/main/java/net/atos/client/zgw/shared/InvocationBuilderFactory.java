/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared;

import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;
import static net.atos.client.zgw.shared.util.ZGWClientHeadersFactory.generateJWTToken;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.shared.util.LoggingFilter;
import net.atos.client.zgw.zrc.ZRCClient;

/**
 *
 */
public final class InvocationBuilderFactory {

    private static Client client;

    private InvocationBuilderFactory() {
    }

    public static Invocation.Builder create(final URI uri) {
        if (client == null) {
            client = ClientBuilder.newClient().register(LoggingFilter.class);
        }
        return client.target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateJWTToken())
                .header(ZRCClient.ACCEPT_CRS, ZRCClient.ACCEPT_CRS_VALUE)
                .header(ZRCClient.CONTENT_CRS, ZRCClient.ACCEPT_CRS_VALUE);
    }
}
