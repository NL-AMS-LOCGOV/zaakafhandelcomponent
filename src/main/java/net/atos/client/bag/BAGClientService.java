/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.bag.model.AdresHalCollectie;
import net.atos.client.bag.model.RaadpleegAdressenParameters;

@ApplicationScoped
public class BAGClientService {

    @Inject
    @RestClient
    private AdresApiClient adresApiClient;

    public AdresHalCollectie raadpleegAdressen(final RaadpleegAdressenParameters raadpleegAdressenParameters) {
        return adresApiClient.raadpleegAdressen(raadpleegAdressenParameters);
    }
}
