/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;

/**
 *
 */
@ApplicationScoped
public class ZGWClientHeadersFactory implements ClientHeadersFactory {

    @Inject
    @IngelogdeMedewerker
    private Instance<Medewerker> ingelogdeMedewerker;

    private static final Config config = ConfigProvider.getConfig();

    private static final String CLIENT_ID = config.getValue("zgw.api.clientId", String.class);

    private static final String SECRET = config.getValue("zgw.api.secret", String.class);

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(HttpHeaders.AUTHORIZATION, generateJWTTokenWithUser(ingelogdeMedewerker.get()));
        return clientOutgoingHeaders;
    }

    public static String generateJWTToken() {
        return generateJWTTokenWithUser(null);
    }

    public String generateJWTTokenWithUser() {
        return generateJWTTokenWithUser(ingelogdeMedewerker.get());
    }

    private static String generateJWTTokenWithUser(final Medewerker ingelogdeMedewerker) {
        final Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("client_identifier", CLIENT_ID);
        final JWTCreator.Builder jwtBuilder = JWT.create();
        jwtBuilder.withIssuer(CLIENT_ID)
                .withIssuedAt(new Date())
                .withHeader(headerClaims)
                .withClaim("client_id", CLIENT_ID);
        if (ingelogdeMedewerker != null) {
            if (ingelogdeMedewerker.getGebruikersnaam() != null) {
                jwtBuilder.withClaim("user_id", ingelogdeMedewerker.getGebruikersnaam());
            }
            if (ingelogdeMedewerker.getNaam() != null) {
                jwtBuilder.withClaim("user_representation", ingelogdeMedewerker.getNaam());
            }
        }
        String jwtToken = jwtBuilder.sign(Algorithm.HMAC256(SECRET));
        return "Bearer " + jwtToken;
    }
}
