/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag;

import static net.atos.client.bag.util.BAGClientHeadersFactory.API_KEY;
import static net.atos.client.bag.util.BAGClientHeadersFactory.X_API_KEY;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.bag.api.AdresApi;
import net.atos.client.bag.model.AdresIOHal;
import net.atos.client.bag.model.AdresIOHalCollectionEmbedded;
import net.atos.client.bag.model.BevraagAdressenParameters;
import net.atos.client.util.ClientFactory;

@ApplicationScoped
public class BAGClientService {

    @Inject
    @RestClient
    private AdresApi adresApi;

    public List<AdresIOHal> listAdressen(final BevraagAdressenParameters parameters) {
        final AdresIOHalCollectionEmbedded embedded = adresApi.bevraagAdressen(parameters).getEmbedded();
        if (embedded.getAdressen() != null) {
            return embedded.getAdressen();
        } else {
            return Collections.emptyList();
        }
    }

    public AdresIOHal readAdres(final URI adresURI) {
        return createInvocationBuilder(adresURI).get(AdresIOHal.class);
    }

    private Invocation.Builder createInvocationBuilder(final URI uri) {
        return ClientFactory.create().target(uri)
                .request("application/hal+json", "application/problem+json")
                .header(X_API_KEY, API_KEY);
    }
}
