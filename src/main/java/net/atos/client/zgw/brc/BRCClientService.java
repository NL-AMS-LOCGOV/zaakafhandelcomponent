/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.BesluitInformatieobject;
import net.atos.client.zgw.brc.model.BesluitenListParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.util.UriUtil;

/**
 * BRC Client Service
 */
@ApplicationScoped
public class BRCClientService {

    @Inject
    @RestClient
    private BRCClient brcClient;

    @Inject
    private ZGWClientHeadersFactory zgwClientHeadersFactory;

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

    public Besluit updateBesluit(final Besluit besluit) {
        final UUID uuid = UriUtil.uuidFromURI(besluit.getUrl());
        besluit.setUrl(null);
        return brcClient.besluitUpdate(uuid, besluit);
    }

    public Besluit readBesluit(final UUID uuid) {
        return brcClient.besluitRead(uuid);
    }

    public BesluitInformatieobject createBesluitInformatieobject(final BesluitInformatieobject besluitInformatieobject, final String toelichting) {
        zgwClientHeadersFactory.setAuditToelichting(toelichting);
        return brcClient.besluitinformatieobjectCreate(besluitInformatieobject);
    }

    public List<BesluitInformatieobject> listBesluitInformatieobjecten(final URI besluit) {
        return brcClient.listBesluitInformatieobjecten(besluit);
    }
}
