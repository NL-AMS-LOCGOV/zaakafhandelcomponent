/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import net.atos.client.util.AcceptHeaderBugWorkaroundUtil;

/**
 *
 */
public class ZGWClientHeadersFactory implements ClientHeadersFactory {

    private static final Config config = ConfigProvider.getConfig();

    private static final String CLIENT_ID = config.getValue("zgw.api.clientId", String.class);

    private static final String SECRET = config.getValue("zgw.api.secret", String.class);

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(HttpHeaders.AUTHORIZATION, generateJWTToken());
        AcceptHeaderBugWorkaroundUtil.fix(clientOutgoingHeaders);
        return clientOutgoingHeaders;
    }

    public static String generateJWTToken() {
        final Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("client_identifier", CLIENT_ID);
        final JWTCreator.Builder jwtBuilder = JWT.create();
        final String jwtToken = jwtBuilder
                .withIssuer(CLIENT_ID)
                .withIssuedAt(new Date())
                .withHeader(headerClaims)
                .withClaim("client_id", CLIENT_ID)
                .sign(Algorithm.HMAC256(SECRET));
        return "Bearer " + jwtToken;
    }
}
