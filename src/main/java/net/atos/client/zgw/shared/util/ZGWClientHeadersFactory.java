/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import net.atos.client.util.JWTTokenGenerator;
import net.atos.zac.authentication.LoggedInUser;

/**
 *
 */
public class ZGWClientHeadersFactory implements ClientHeadersFactory {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @ConfigProperty(name = "ZGW_API_CLIENTID")
    private String clientId;

    @Inject
    @ConfigProperty(name = "ZGW_API_SECRET")
    private String secret;

    private static final Map<String, String> AUDIT_TOELICHTINGEN = new ConcurrentHashMap<>();

    @Override
    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders,
            final MultivaluedMap<String, String> outgoingHeaders) {
        final LoggedInUser loggedInUser = loggedInUserInstance.get();
        try {
            addAutorizationHeader(outgoingHeaders, loggedInUser);
            addXAuditToelichtingHeader(outgoingHeaders, loggedInUser);
            return outgoingHeaders;
        } finally {
            clearAuditToelichting(loggedInUser);
        }
    }

    public String generateJWTToken() {
        return JWTTokenGenerator.generate(clientId, secret, loggedInUserInstance.get());
    }

    public void setAuditToelichting(final String toelichting) {
        if (toelichting != null) {
            final LoggedInUser loggedInUser = loggedInUserInstance.get();
            if (loggedInUser != null) {
                AUDIT_TOELICHTINGEN.put(loggedInUser.getId(), toelichting);
            }
        }
    }

    private void clearAuditToelichting(final LoggedInUser loggedInUser) {
        if (loggedInUser != null) {
            AUDIT_TOELICHTINGEN.remove(loggedInUser.getId());
        }
    }

    private void addAutorizationHeader(final MultivaluedMap<String, String> outgoingHeaders,
            final LoggedInUser loggedInUser) {
        outgoingHeaders.add(HttpHeaders.AUTHORIZATION, JWTTokenGenerator.generate(clientId, secret, loggedInUser));
    }

    private void addXAuditToelichtingHeader(final MultivaluedMap<String, String> outgoingHeaders,
            final LoggedInUser loggedInUser) {
        if (loggedInUser != null) {
            final String toelichting = AUDIT_TOELICHTINGEN.get(loggedInUser.getId());
            if (toelichting != null) {
                outgoingHeaders.add("X-Audit-Toelichting", toelichting);
            }
        }
    }
}
