/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import net.atos.zac.authentication.LoggedInUser;

public final class JWTTokenGenerator {

    private JWTTokenGenerator() {
    }

    public static String generate(final String clientId, final String secret, final LoggedInUser loggedInUser) {
        final Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("client_identifier", clientId);
        final JWTCreator.Builder jwtBuilder = JWT.create();
        jwtBuilder.withIssuer(clientId)
                .withIssuedAt(new Date())
                .withHeader(headerClaims)
                .withClaim("client_id", clientId);
        if (loggedInUser != null) {
            jwtBuilder.withClaim("user_id", loggedInUser.getId());
            jwtBuilder.withClaim("user_representation", loggedInUser.getFullName());
        }
        String jwtToken = jwtBuilder.sign(Algorithm.HMAC256(secret));
        return "Bearer " + jwtToken;
    }
}
