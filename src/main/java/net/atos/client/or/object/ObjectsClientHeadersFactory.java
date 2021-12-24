/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import net.atos.client.util.AcceptHeaderBugWorkaroundUtil;

/**
 *
 */
public class ObjectsClientHeadersFactory implements ClientHeadersFactory {

    private static final String TOKEN = ConfigProvider.getConfig().getValue("objects.api.token", String.class);

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(AUTHORIZATION, generateToken());
        AcceptHeaderBugWorkaroundUtil.fix(clientOutgoingHeaders);
        return clientOutgoingHeaders;
    }

    public static String generateToken() {
        return String.format("Token %s", TOKEN);
    }
}
