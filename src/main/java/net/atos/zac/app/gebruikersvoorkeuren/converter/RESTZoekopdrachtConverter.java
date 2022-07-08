/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.gebruikersvoorkeuren.converter;

import java.time.ZonedDateTime;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.atos.zac.app.gebruikersvoorkeuren.model.RESTZoekopdracht;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;

public class RESTZoekopdrachtConverter {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public RESTZoekopdracht convert(final Zoekopdracht zoekopdracht) {
        final RESTZoekopdracht restZoekopdracht = new RESTZoekopdracht();
        restZoekopdracht.id = zoekopdracht.getId();
        restZoekopdracht.lijstID = zoekopdracht.getLijstID();
        restZoekopdracht.naam = zoekopdracht.getNaam();
        restZoekopdracht.actief = zoekopdracht.isActief();
        restZoekopdracht.creatiedatum = zoekopdracht.getCreatiedatum().toLocalDate();
        restZoekopdracht.json = zoekopdracht.getJson();
        return restZoekopdracht;
    }

    public Zoekopdracht convert(final RESTZoekopdracht restZoekopdracht) {
        final Zoekopdracht zoekopdracht = new Zoekopdracht();
        zoekopdracht.setId(restZoekopdracht.id);
        zoekopdracht.setLijstID(restZoekopdracht.lijstID);
        zoekopdracht.setMedewerkerID(loggedInUserInstance.get().getId());
        zoekopdracht.setNaam(restZoekopdracht.naam);
        zoekopdracht.setActief(restZoekopdracht.actief);
        zoekopdracht.setCreatiedatum(ZonedDateTime.now());
        zoekopdracht.setJson(restZoekopdracht.json);
        return zoekopdracht;
    }

    public List<RESTZoekopdracht> convert(final List<Zoekopdracht> zoekopdrachten) {
        return zoekopdrachten.stream().map(this::convert).toList();
    }
}
