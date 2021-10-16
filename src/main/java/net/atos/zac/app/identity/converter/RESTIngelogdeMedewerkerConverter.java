/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.converter;

import javax.inject.Inject;

import net.atos.zac.app.identity.model.RESTIngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;

public class RESTIngelogdeMedewerkerConverter {

    @Inject
    private RESTGroepConverter groepConverter;

    public RESTIngelogdeMedewerker convertIngelogdeMedewerker(final Medewerker ingelogdeMedewerker) {
        if (ingelogdeMedewerker != null) {
            final RESTIngelogdeMedewerker restIngelogdeMedewerker = new RESTIngelogdeMedewerker();
            restIngelogdeMedewerker.setGebruikersnaam(ingelogdeMedewerker.getGebruikersnaam());
            restIngelogdeMedewerker.setNaam(ingelogdeMedewerker.getNaam());
            restIngelogdeMedewerker.setGroepen(groepConverter.convertGroups(ingelogdeMedewerker.getGroups()));
            return restIngelogdeMedewerker;
        }

        return null;
    }

}
