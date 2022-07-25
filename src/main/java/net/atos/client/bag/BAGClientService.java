/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag;

import static net.atos.client.bag.util.BAGClientHeadersFactory.API_KEY;
import static net.atos.client.bag.util.BAGClientHeadersFactory.X_API_KEY;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.bag.model.AdresHal;
import net.atos.client.bag.model.AdresHalCollectie;
import net.atos.client.bag.model.RaadpleegAdressenParameters;
import net.atos.client.util.ClientFactory;

@ApplicationScoped
public class BAGClientService {

    @Inject
    @RestClient
    private AdresApiClient adresApiClient;

    public AdresHalCollectie raadpleegAdressen(final RaadpleegAdressenParameters raadpleegAdressenParameters) {
        return adresApiClient.raadpleegAdressen(raadpleegAdressenParameters);
    }

    public AdresHal readAdres(final URI adresURI) {
        return createInvocationBuilder(adresURI).get(AdresHal.class);
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request("application/hal+json", "application/problem+json")
                .header(X_API_KEY, API_KEY);
    }
}
