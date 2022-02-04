/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;


public class BRPClientHeadersFactory implements ClientHeadersFactory {

    private static final String X_API_KEY = "X-API-KEY";

    private static final String API_KEY = ConfigProvider.getConfig().getValue("brp.api.key", String.class);

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(X_API_KEY, API_KEY);
        return clientOutgoingHeaders;
    }
}
