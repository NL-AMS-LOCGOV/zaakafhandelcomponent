/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;
import static net.atos.client.zgw.shared.util.ZGWClientHeadersFactory.generateJWTToken;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

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
            client = createClient();
        }

        return client.target(uri)
                .request(MediaType.APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.AUTHORIZATION, generateJWTToken())
                .header(ZRCClient.ACCEPT_CRS, ZRCClient.ACCEPT_CRS_VALUE)
                .header(ZRCClient.CONTENT_CRS, ZRCClient.ACCEPT_CRS_VALUE);
    }

    private static Client createClient() {
        final String proxyHost = System.getProperty("http.proxyHost");
        final String proxyPort = System.getProperty("http.proxyPort");
        try {
            final ClientBuilder clientBuilder = ClientBuilder.newBuilder().sslContext(SSLContext.getDefault());
            if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNumeric(proxyPort)) {
                clientBuilder.property("org.jboss.resteasy.jaxrs.client.proxy.host", proxyHost)
                        .property("org.jboss.resteasy.jaxrs.client.proxy.port", proxyPort);
            }
            return clientBuilder.build().register(LoggingFilter.class);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Fout tijdens initialiseren van client", e);
        }
    }
}
