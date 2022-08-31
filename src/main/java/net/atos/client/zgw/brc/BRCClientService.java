/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.BesluitenListParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.model.Zaak;

/**
 * BRC Client Service
 */
@ApplicationScoped
public class BRCClientService {

    @Inject
    @RestClient
    private BRCClient brcClient;

    public Besluit findBesluit(final Zaak zaak) {
        final BesluitenListParameters listParameters = new BesluitenListParameters(zaak.getUrl());
        final Results<Besluit> results = brcClient.besluitList(listParameters);
        if (results.getCount() > 0) {
            return results.getResults().get(0);
        }
        return null;
    }

    public Besluit createBesluit(final Besluit besluit) {
        return brcClient.besluitCreate(besluit);
    }

}
