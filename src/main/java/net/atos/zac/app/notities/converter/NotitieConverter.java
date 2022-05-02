/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.notities.converter;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import net.atos.zac.app.notities.model.RESTNotitie;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.User;
import net.atos.zac.notities.model.Notitie;

public class NotitieConverter {

    @Inject
    private IdentityService identityService;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    public RESTNotitie convertToRESTNotitie(final Notitie notitie) {
        final RESTNotitie restNotitie = new RESTNotitie();

        restNotitie.id = notitie.getId();
        restNotitie.zaakUUID = notitie.getZaakUUID();
        restNotitie.tekst = notitie.getTekst();
        restNotitie.tijdstipLaatsteWijziging = notitie.getTijdstipLaatsteWijziging();
        restNotitie.gebruikersnaamMedewerker = notitie.getGebruikersnaamMedewerker();

        final User medewerker = identityService.readUser(notitie.getGebruikersnaamMedewerker());
        restNotitie.voornaamAchternaamMedewerker = String.format("%s %s", medewerker.getFirstName(),
                                                                 medewerker.getLastName());

        restNotitie.bewerkenToegestaan =
                ingelogdeMedewerker.getGebruikersnaam().equals(notitie.getGebruikersnaamMedewerker());

        return restNotitie;
    }

    public Notitie convertToNotitie(final RESTNotitie restNotitie) {
        final Notitie notitie = new Notitie();

        notitie.setId(restNotitie.id);
        notitie.setZaakUUID(restNotitie.zaakUUID);
        notitie.setTekst(restNotitie.tekst);
        notitie.setTijdstipLaatsteWijziging(ZonedDateTime.now());
        notitie.setGebruikersnaamMedewerker(restNotitie.gebruikersnaamMedewerker);

        return notitie;
    }
}
