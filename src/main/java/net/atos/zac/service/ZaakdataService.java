/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import java.net.URI;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.or.shared.ObjectRegistratieClientService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.zac.zaakdata.Zaakdata;

@Stateless
public class ZaakdataService {

    private static final String ZAAKDATA_OBJECT_TYPE_OVERIGE = "zaakdata";

    private static final String ZAAKDATA_ZAAKOBJECT_RELATIE_OMSCHRIJVING = "Data gebruikt tijdens behandelen van zaak";

    private static final String ZAAKDATA_OBJECTTYPE_NAAM = "zaakdata";

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private ObjectRegistratieClientService objectRegistratieClientService;

    public Zaakdata retrieveZaakdata(final UUID zaakUUID) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        final Zaakobject zaakobject = findZaakobject(zaak.getUrl());
        final ORObject object = objectsClientService.readObject(zaakobject.getObject());
        final Zaakdata zaakdata = new Zaakdata();
        object.getRecord().getDataAsHashMap().forEach((key, value) -> zaakdata.addElement(key, value.toString()));
        return zaakdata;
    }

    public Zaakdata storeZaakdata(final UUID zaakUUID, final Zaakdata zaakdata) {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        Zaakobject zaakobject = findZaakobject(zaak.getUrl());
        if (zaakobject == null) {
            final ORObject object = objectRegistratieClientService.createObject(ZAAKDATA_OBJECTTYPE_NAAM, zaakdata.getElementen());
            zaakobject = convertToZaakobject(zaak.getUrl(), object.getUrl());
            zrcClientService.createZaakobject(zaakobject);
        } else {
            final ORObject object = objectsClientService.readObject(zaakobject.getObject());
            object.getRecord().setData(zaakdata.getElementen());
            objectsClientService.updateObject(object);
        }
        return zaakdata;
    }

    private Zaakobject findZaakobject(final URI zaakURI) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        zaakobjectListParameters.setZaak(zaakURI);
        zaakobjectListParameters.setObjectType(Objecttype.OVERIGE);
        return zrcClientService.listZaakobjecten(zaakobjectListParameters).getSinglePageResults().stream()
                .filter(zaakobject -> ZAAKDATA_OBJECT_TYPE_OVERIGE.equals(zaakobject.getObjectTypeOverige()))
                .findAny()
                .orElse(null);
    }

    private Zaakobject convertToZaakobject(final URI zaakURI, final URI objectURI) {
        final Zaakobject zaakobject = new Zaakobject();
        zaakobject.setZaak(zaakURI);
        zaakobject.setObject(objectURI);
        zaakobject.setObjectType(Objecttype.OVERIGE);
        zaakobject.setObjectTypeOverige(ZAAKDATA_OBJECT_TYPE_OVERIGE);
        zaakobject.setRelatieomschrijving(ZAAKDATA_ZAAKOBJECT_RELATIE_OMSCHRIJVING);
        return zaakobject;
    }
}
