/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.util;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class ClientFactory {

    private static Client client;

    private ClientFactory() {
    }

    public static Client create() {
        if (client == null) {
            client = createClient();
        }
        return client;
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
